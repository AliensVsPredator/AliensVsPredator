package com.blib.engine.modeler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.api.client.model.v1.AzBone;
import com.blib.internal.client.model.AzBakedModelCache;
import com.blib.internal.client.model.GeoCube;

/**
 * Converts a baked {@link AzBakedModel} into a fresh {@link ModelerScene} state — the render-correctness test. The
 * source model is looked up by {@link ResourceLocation} via {@link AzBakedModelCache}, which the existing resource-pack
 * reload listener keeps in sync. The conversion preserves the full bone hierarchy and per-cube transform fields so the
 * modeler renders an exact visual match for the in-world entity.
 */
@ApiStatus.Internal
public final class ModelerSceneLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelerSceneLoader.class);

    private ModelerSceneLoader() {}

    /**
     * Attempt to load {@code resourceLocation}'s baked model and write it into the active {@link ModelerScene}. Returns
     * {@code true} on success; {@code false} if the cache has no entry for the id (typically a typo or a model that
     * doesn't ship in the current resource packs).
     */
    public static boolean load(ResourceLocation resourceLocation) {
        var baked = AzBakedModelCache.getInstance().getOrNull(resourceLocation);
        if (baked == null) {
            LOGGER.warn("ModelerSceneLoader: no baked model for {}", resourceLocation);
            return false;
        }

        var scene = ModelerScene.get();
        scene.root = convertModel(baked, resourceLocation);
        scene.selection = null;
        return true;
    }

    private static ModelerBone convertModel(AzBakedModel baked, ResourceLocation rl) {
        // AzBakedModel.getTopLevelBones() returns a list (an entity model usually has just one — "bb_main" — but
        // multiple is legal). Wrap them under an implicit "root" bone with identity transforms so the scene retains
        // a single-root invariant; visual output is unchanged because the wrapper applies no transform.
        var root = new ModelerBone("root[" + rl + "]");
        for (var topBone : baked.getTopLevelBones()) {
            root.addChild(convertBone(topBone));
        }
        return root;
    }

    private static ModelerBone convertBone(AzBone source) {
        var bone = new ModelerBone(
            source.getName(),
            new Vec3(source.getPosX(), source.getPosY(), source.getPosZ()),
            new Vec3(source.getRotX(), source.getRotY(), source.getRotZ()),
            new Vec3(source.getScaleX(), source.getScaleY(), source.getScaleZ()),
            new Vec3(source.getPivotX(), source.getPivotY(), source.getPivotZ())
        );
        var cubeIndex = 0;
        for (var geoCube : source.getCubes()) {
            bone.cubes.add(convertCube(geoCube, source.getName() + "_cube" + cubeIndex++));
        }
        for (var child : source.getChildBones()) {
            bone.addChild(convertBone(child));
        }
        return bone;
    }

    private static ModelerCube convertCube(GeoCube source, String name) {
        // GeoCube doesn't store origin directly — the quad vertices already encode the cube's local-space AABB
        // (post-inflate, pre-rotation, in cube-local pivot-relative coords). Recover the min corner by scanning all
        // 24 vertices.
        var origin = minVertex(source);
        return new ModelerCube(
            name,
            origin,
            new Vec3(source.size().x, source.size().y, source.size().z),
            new Vec3(source.rotation().x, source.rotation().y, source.rotation().z),
            new Vec3(source.pivot().x, source.pivot().y, source.pivot().z),
            source.inflate()
        );
    }

    private static Vec3 minVertex(GeoCube cube) {
        Vec3 min = null;
        for (var quad : cube.quads()) {
            if (quad == null)
                continue;
            for (var v : quad.vertices()) {
                if (v == null)
                    continue;
                var pos = v.position();
                if (min == null) {
                    min = new Vec3(pos.x, pos.y, pos.z);
                } else {
                    min = new Vec3(
                        Math.min(min.x, pos.x),
                        Math.min(min.y, pos.y),
                        Math.min(min.z, pos.z)
                    );
                }
            }
        }
        return min != null ? min : Vec3.ZERO;
    }

    /**
     * Convenience for callers that have a string id rather than a {@link ResourceLocation}. Returns {@code null} on a
     * malformed id and logs.
     */
    public static boolean loadByString(@Nullable String id) {
        if (id == null || id.isBlank())
            return false;
        var rl = ResourceLocation.tryParse(id);
        if (rl == null) {
            LOGGER.warn("ModelerSceneLoader: malformed resource id {}", id);
            return false;
        }
        return load(rl);
    }
}
