package com.blib.engine.modeler;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.internal.client.model.BoneStructure;
import com.blib.internal.client.model.Cube;
import com.blib.internal.client.model.GeometryTree;
import com.blib.internal.client.model.Model;
import com.blib.internal.common.io.ResourceFileLoader;
import com.blib.internal.common.io.util.JsonUtil;

/**
 * Loads a Bedrock geo model into the active {@link ModelerScene}. Sources two ways:
 * <ul>
 * <li>{@link #load(ResourceLocation)} / {@link #loadByString(String)} — read from the active resource manager (any pack
 * registered under {@code assets/&lt;namespace&gt;/}).</li>
 * <li>{@link #loadFromFile(Path)} — read from an arbitrary filesystem path the user picked via the OS file
 * browser.</li>
 * </ul>
 * <p>
 * Both paths parse the JSON to the raw {@link Model} DTO (via {@link JsonUtil#GEO_GSON}), resolve the bone hierarchy
 * via {@link GeometryTree#fromModel}, and convert directly into {@link ModelerBone} / {@link ModelerCube}. We
 * deliberately bypass {@code AzBakedModelCache} / {@code AzBakedModelFactory} — the bake step's coordinate
 * transformations (X-flip, divide-by-16 into block-space, radians, sign negation on X/Y rotation) are tailored to
 * Minecraft's entity render system, not to the Bedrock pixel-space convention the modeler edits in. Going through the
 * bake and then undoing it is lossy and error-prone; reading the JSON DTO straight through preserves the authored
 * origin / size / pivot / rotation / inflate verbatim.
 */
@ApiStatus.Internal
public final class ModelerSceneLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelerSceneLoader.class);

    private ModelerSceneLoader() {}

    /**
     * Load the geo model at {@code resourceLocation} from the active resource manager (any namespace / pack that
     * contains the file at {@code assets/<namespace>/<path>}). Returns {@code true} on success, {@code false} if the
     * file is missing, malformed, or empty.
     */
    public static boolean load(ResourceLocation resourceLocation) {
        var manager = Minecraft.getInstance().getResourceManager();
        var loadResult = ResourceFileLoader.loadObjectFromFile(JsonUtil.GEO_GSON, Model.class, resourceLocation, manager);
        if (loadResult.isErr()) {
            LOGGER.warn("ModelerSceneLoader: failed to load {}: {}", resourceLocation, loadResult.unwrapErr());
            return false;
        }

        var model = loadResult.unwrap();
        if (model == null) {
            LOGGER.warn("ModelerSceneLoader: parsed null model for {}", resourceLocation);
            return false;
        }

        return applyModel(model, "root[" + resourceLocation + "]");
    }

    /**
     * Parse and load a geo model from an arbitrary filesystem path — the entry point for the "Open Model from File…"
     * menu action. Bypasses the resource manager entirely.
     */
    public static boolean loadFromFile(Path path) {
        String json;
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            LOGGER.warn("ModelerSceneLoader: failed to read {}: {}", path, e.getMessage());
            return false;
        }

        Model model;
        try {
            model = JsonUtil.GEO_GSON.fromJson(json, Model.class);
        } catch (JsonSyntaxException e) {
            LOGGER.warn("ModelerSceneLoader: malformed JSON in {}: {}", path, e.getMessage());
            return false;
        }
        if (model == null) {
            LOGGER.warn("ModelerSceneLoader: parsed null model from {}", path);
            return false;
        }

        return applyModel(model, "root[" + path.getFileName() + "]");
    }

    /**
     * Convenience for callers that have a string id rather than a {@link ResourceLocation}. Returns {@code false} on a
     * malformed id (and logs) or any error inside {@link #load}.
     */
    public static boolean loadByString(@Nullable String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        var rl = ResourceLocation.tryParse(id);
        if (rl == null) {
            LOGGER.warn("ModelerSceneLoader: malformed resource id {}", id);
            return false;
        }
        return load(rl);
    }

    /**
     * Shared tail of both loader paths — convert the parsed {@link Model} into a fresh {@link ModelerBone} tree and
     * replace the active scene's root. Selection is cleared so any in-flight gizmo state resets cleanly.
     */
    private static boolean applyModel(Model model, String rootLabel) {
        if (model.minecraftGeometry() == null || model.minecraftGeometry().length == 0) {
            LOGGER.warn("ModelerSceneLoader: model has no minecraft:geometry entries");
            return false;
        }

        var tree = GeometryTree.fromModel(model);
        var root = new ModelerBone(rootLabel);
        for (var topLevel : tree.topLevelBones().values()) {
            root.addChild(convertBone(topLevel, null));
        }

        var scene = ModelerScene.get();
        scene.root = root;
        // Carry texture_width/height from the model's description block into the scene so panels (the UV map in
        // particular) can render against the correct sheet bounds. The first geometry's properties win — Bedrock allows
        // multiple geometries per file but the modeler is single-geometry today.
        var properties = model.minecraftGeometry()[0].modelProperties();
        if (properties != null) {
            scene.textureWidth = properties.textureWidth();
            scene.textureHeight = properties.textureHeight();
        } else {
            scene.textureWidth = 64.0;
            scene.textureHeight = 64.0;
        }
        scene.selection = null;
        // Existing undo entries reference bone/cube instances from the previous scene tree — those instances aren't
        // reachable any more, so applying their undo() would mutate detached objects. Wipe history on load so the
        // first action in the new scene starts a fresh stack.
        ModelerActionHistory.clear();
        return true;
    }

    /**
     * Recursive JSON-DTO walk that builds a {@link ModelerBone} from a {@link BoneStructure}, applying the same X flip
     * + X/Y rotation negation that {@code AzBuiltinBakedModelFactory} uses to map Bedrock JSON conventions onto MC's
     * entity-render space. We stay in pixel units and degrees (no {@code /16}, no radians), so the modeler displays
     * values that match Blockbench's flipped-layout view of the same Bedrock model — which is the view a Java-mod
     * author authoring a model edits against.
     * <p>
     * {@code inheritedInflate} carries the nearest ancestor's bone-level {@code inflate} so per-cube defaults fall back
     * to it when a cube omits its own. Bedrock allows {@code inflate} on either bones (applies to all child cubes) or
     * per-cube (overrides bone-level); we resolve it here so {@link ModelerCube}'s inflate field is the effective
     * value.
     */
    private static ModelerBone convertBone(BoneStructure structure, @Nullable Double inheritedInflate) {
        var source = structure.self();
        var name = source.name() != null ? source.name() : "bone";

        var rawPivot = toVec3(source.pivot());
        var rawRotation = toVec3(source.rotation());
        // Bedrock bones have no "position" field — only pivot + rotation. Position stays at origin.
        var modelerBone = new ModelerBone(
            name,
            Vec3.ZERO,
            new Vec3(-rawRotation.x, -rawRotation.y, rawRotation.z),
            new Vec3(1, 1, 1),
            new Vec3(-rawPivot.x, rawPivot.y, rawPivot.z)
        );

        var effectiveInflate = source.inflate() != null ? source.inflate() : inheritedInflate;

        if (source.cubes() != null) {
            int cubeIndex = 0;
            for (var cube : source.cubes()) {
                modelerBone.cubes.add(convertCube(cube, name + "_cube" + cubeIndex++, effectiveInflate));
            }
        }

        for (var child : structure.children().values()) {
            modelerBone.addChild(convertBone(child, effectiveInflate));
        }

        return modelerBone;
    }

    /**
     * Cube DTO → {@link ModelerCube}. Mirrors the bake's X-flip + X/Y rotation negation so values display the way
     * Blockbench shows the model in flipped layout and the way the entity actually renders in-game. Specifically:
     * <ul>
     * <li>{@code origin.x} becomes {@code -(jsonOrigin.x + jsonSize.x)} — the cube spans the same range mirrored around
     * X=0, so a JSON "right_foot" with negative origin ends up on the +X side of the modeler (entity right) matching
     * its in-game position.</li>
     * <li>{@code pivot.x} is negated.</li>
     * <li>{@code rotation.x} and {@code rotation.y} are negated; {@code rotation.z} stays.</li>
     * <li>{@code size} and {@code inflate} carry through unchanged.</li>
     * </ul>
     * Inflate falls back to the inherited bone-level value when the cube doesn't specify its own; zero if neither is
     * set.
     */
    private static ModelerCube convertCube(Cube source, String name, @Nullable Double inheritedInflate) {
        double inflate = source.inflate() != null
            ? source.inflate()
            : (inheritedInflate != null ? inheritedInflate : 0.0);

        var origin = toVec3(source.origin());
        var size = toVec3(source.size());
        var rotation = toVec3(source.rotation());
        var pivot = toVec3(source.pivot());

        var cube = new ModelerCube(
            name,
            new Vec3(-(origin.x + size.x), origin.y, origin.z),
            size,
            new Vec3(-rotation.x, -rotation.y, rotation.z),
            new Vec3(-pivot.x, pivot.y, pivot.z),
            inflate
        );

        cube.mirrorUv = source.mirror() == Boolean.TRUE;

        // Box-UV origin carries through unchanged — UV space is texture pixels, untouched by the X-flip applied to
        // geometry. Per-face UV cubes are out of scope for v1; their cubes stay at (0, 0) and the UV map renders a
        // hint so users aren't confused why they stack at the origin.
        var uv = source.uv();
        if (uv != null) {
            if (uv.isBoxUV() && uv.boxUVCoords() != null && uv.boxUVCoords().length >= 2) {
                cube.uvOriginU = uv.boxUVCoords()[0];
                cube.uvOriginV = uv.boxUVCoords()[1];
            } else if (!uv.isBoxUV()) {
                cube.hasPerFaceUv = true;
            }
        }
        return cube;
    }

    /**
     * Defensive {@code double[3]} → {@link Vec3} converter — falls back to {@link Vec3#ZERO} for null / short arrays.
     */
    private static Vec3 toVec3(@Nullable double[] arr) {
        if (arr == null || arr.length < 3) {
            return Vec3.ZERO;
        }
        return new Vec3(arr[0], arr[1], arr[2]);
    }
}
