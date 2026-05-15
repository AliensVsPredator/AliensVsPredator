package com.blib.engine.modeler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blib.engine.modeler.history.ModelerActionHistory;
import com.blib.engine.modeler.texture.LoadedTexture;
import com.blib.engine.modeler.texture.TextureLoader;
import com.blib.engine.modeler.texture.TextureResourceCatalog;

/**
 * Imports vanilla-style block JSON into the modeler as a block-shaped scene. This is intentionally a preview/import
 * path, not a full blockstate authoring system: it chooses the first blockstate variant, follows the model parent chain,
 * converts JSON {@code elements[]} into modeler cubes, and loads the referenced texture resources.
 */
@ApiStatus.Internal
public final class ModelerBlockModelLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelerBlockModelLoader.class);

    private static final int MAX_PARENT_DEPTH = 32;

    private static final double BLOCK_CENTER_OFFSET = 8.0;

    private static final double BLOCK_TEXTURE_SIZE = 16.0;

    private ModelerBlockModelLoader() {}

    public static boolean load(ResourceLocation blockId) {
        var blockstate = readJson(blockstateResource(blockId));
        if (blockstate == null) {
            LOGGER.warn("ModelerBlockModelLoader: no blockstate JSON found for {}", blockId);
            return false;
        }

        var modelId = firstVariantModel(blockstate, blockId.getNamespace());
        if (modelId == null) {
            LOGGER.warn("ModelerBlockModelLoader: blockstate {} has no importable model variant", blockId);
            return false;
        }

        var model = resolveModel(modelId, 0);
        if (model == null || model.elements().isEmpty()) {
            LOGGER.warn("ModelerBlockModelLoader: block model {} has no elements to import", modelId);
            return false;
        }

        var root = new ModelerBone("block[" + blockId + "]");
        var usedTextures = new LinkedHashSet<ResourceLocation>();
        int index = 0;
        for (var element : model.elements()) {
            var cube = convertElement(element, "element_" + index++, model, usedTextures);
            if (cube != null) {
                root.cubes.add(cube);
            }
        }
        if (root.cubes.isEmpty()) {
            LOGGER.warn("ModelerBlockModelLoader: block model {} produced no cubes", modelId);
            return false;
        }

        applyScene(blockId, root, usedTextures);
        return true;
    }

    private static void applyScene(ResourceLocation blockId, ModelerBone root, Set<ResourceLocation> usedTextures) {
        var scene = ModelerScene.get();
        scene.closeTextures();
        scene.itemSession = null;
        scene.root = root;
        scene.textureWidth = BLOCK_TEXTURE_SIZE;
        scene.textureHeight = BLOCK_TEXTURE_SIZE;
        scene.selection = null;
        scene.gizmoTargetSelection = null;
        scene.hoveredCube = null;

        LoadedTexture first = null;
        for (var texture : usedTextures) {
            var loaded = TextureLoader.loadFromResource(texture, TextureResourceCatalog.displayName(texture));
            if (loaded == null) {
                continue;
            }
            scene.textures.add(loaded);
            if (first == null) {
                first = loaded;
            }
        }
        scene.activeTexture = first;
        if (first == null) {
            LOGGER.warn("ModelerBlockModelLoader: imported {} without any loadable textures", blockId);
        }
        ModelerActionHistory.clear();
    }

    private static @Nullable ModelerCube convertElement(
        JsonObject element,
        String name,
        ResolvedModel model,
        Set<ResourceLocation> usedTextures
    ) {
        var from = readArray3(element.get("from"), new double[] { 0.0, 0.0, 0.0 });
        var to = readArray3(element.get("to"), new double[] { 16.0, 16.0, 16.0 });
        var minX = Math.min(from[0], to[0]);
        var minY = Math.min(from[1], to[1]);
        var minZ = Math.min(from[2], to[2]);
        var maxX = Math.max(from[0], to[0]);
        var maxY = Math.max(from[1], to[1]);
        var maxZ = Math.max(from[2], to[2]);
        var size = new Vec3(maxX - minX, maxY - minY, maxZ - minZ);
        if (size.x <= 0.0 || size.y <= 0.0 || size.z <= 0.0) {
            return null;
        }

        var origin = new Vec3(minX - BLOCK_CENTER_OFFSET, minY, minZ - BLOCK_CENTER_OFFSET);
        var pivot = new Vec3(origin.x + size.x * 0.5, origin.y + size.y * 0.5, origin.z + size.z * 0.5);
        var rotation = Vec3.ZERO;
        if (element.has("rotation") && element.get("rotation").isJsonObject()) {
            var rot = element.getAsJsonObject("rotation");
            var rawPivot = readArray3(rot.get("origin"), new double[] { 8.0, 8.0, 8.0 });
            pivot = new Vec3(rawPivot[0] - BLOCK_CENTER_OFFSET, rawPivot[1], rawPivot[2] - BLOCK_CENTER_OFFSET);
            var angle = readDouble(rot.get("angle"), 0.0);
            var axis = readString(rot.get("axis"));
            if ("x".equals(axis)) {
                rotation = new Vec3(angle, 0.0, 0.0);
            } else if ("y".equals(axis)) {
                rotation = new Vec3(0.0, angle, 0.0);
            } else if ("z".equals(axis)) {
                rotation = new Vec3(0.0, 0.0, angle);
            }
        }

        var cube = new ModelerCube(name, origin, size, rotation, pivot, 0.0);
        if (!element.has("faces") || !element.get("faces").isJsonObject()) {
            return cube;
        }

        var faces = element.getAsJsonObject("faces");
        for (var entry : faces.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            var face = face(entry.getKey());
            if (face == null) {
                continue;
            }

            var faceJson = entry.getValue().getAsJsonObject();
            var uv = readUv(faceJson.get("uv"), entry.getKey(), from, to);
            if (uv == null) {
                continue;
            }

            var texture = resolveFaceTexture(faceJson, model);
            if (texture != null) {
                usedTextures.add(texture);
            }
            var uvRotation = normalizeUvRotation((int) readDouble(faceJson.get("rotation"), 0.0));
            cube.setFaceUv(face, new ModelerCube.FaceUv(uv[0], uv[1], uv[2] - uv[0], uv[3] - uv[1], uvRotation, texture));
        }
        return cube;
    }

    private static @Nullable ResourceLocation resolveFaceTexture(JsonObject faceJson, ResolvedModel model) {
        var textureRef = readString(faceJson.get("texture"));
        if (textureRef == null || textureRef.isBlank()) {
            return null;
        }
        var resolved = resolveTextureValue(textureRef, model.textures());
        if (resolved == null || resolved.isBlank()) {
            return null;
        }
        var textureId = parseId(resolved, model.namespace());
        if (textureId == null) {
            return null;
        }
        var path = textureId.getPath();
        if (!path.startsWith("textures/")) {
            path = "textures/" + path;
        }
        if (!path.endsWith(".png")) {
            path += ".png";
        }
        return ResourceLocation.fromNamespaceAndPath(textureId.getNamespace(), path);
    }

    private static @Nullable String resolveTextureValue(String raw, Map<String, String> textures) {
        var key = raw;
        var seen = new LinkedHashSet<String>();
        while (key.startsWith("#")) {
            key = key.substring(1);
            if (!seen.add(key)) {
                return null;
            }
            var next = textures.get(key);
            if (next == null) {
                return null;
            }
            key = next;
        }
        return key;
    }

    private static @Nullable ResolvedModel resolveModel(ResourceLocation modelId, int depth) {
        if (depth > MAX_PARENT_DEPTH) {
            LOGGER.warn("ModelerBlockModelLoader: parent chain for {} is too deep", modelId);
            return null;
        }
        var json = readJson(modelResource(modelId));
        if (json == null) {
            LOGGER.warn("ModelerBlockModelLoader: missing model JSON {}", modelId);
            return null;
        }

        var textures = new LinkedHashMap<String, String>();
        var elements = new ArrayList<JsonObject>();
        if (json.has("parent")) {
            var parentId = parseId(readString(json.get("parent")), "minecraft");
            if (parentId != null) {
                var parent = resolveModel(parentId, depth + 1);
                if (parent != null) {
                    textures.putAll(parent.textures());
                    elements.addAll(parent.elements());
                }
            }
        }

        if (json.has("textures") && json.get("textures").isJsonObject()) {
            for (var entry : json.getAsJsonObject("textures").entrySet()) {
                var value = readString(entry.getValue());
                if (value != null) {
                    textures.put(entry.getKey(), value);
                }
            }
        }

        if (json.has("elements") && json.get("elements").isJsonArray()) {
            elements.clear();
            for (var element : json.getAsJsonArray("elements")) {
                if (element.isJsonObject()) {
                    elements.add(element.getAsJsonObject());
                }
            }
        }
        return new ResolvedModel(modelId.getNamespace(), textures, elements);
    }

    private static @Nullable ResourceLocation firstVariantModel(JsonObject blockstate, String fallbackNamespace) {
        if (blockstate.has("variants") && blockstate.get("variants").isJsonObject()) {
            for (var entry : blockstate.getAsJsonObject("variants").entrySet()) {
                var model = readVariantModel(entry.getValue(), fallbackNamespace);
                if (model != null) {
                    return model;
                }
            }
        }

        if (blockstate.has("multipart") && blockstate.get("multipart").isJsonArray()) {
            for (var entry : blockstate.getAsJsonArray("multipart")) {
                if (!entry.isJsonObject()) {
                    continue;
                }
                var apply = entry.getAsJsonObject().get("apply");
                var model = readVariantModel(apply, fallbackNamespace);
                if (model != null) {
                    return model;
                }
            }
        }
        return null;
    }

    private static @Nullable ResourceLocation readVariantModel(@Nullable JsonElement element, String fallbackNamespace) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            if (arr.isEmpty()) {
                return null;
            }
            return readVariantModel(arr.get(0), fallbackNamespace);
        }
        if (!element.isJsonObject()) {
            return null;
        }
        var raw = readString(element.getAsJsonObject().get("model"));
        return parseId(raw, fallbackNamespace);
    }

    private static @Nullable JsonObject readJson(ResourceLocation resource) {
        var optional = Minecraft.getInstance().getResourceManager().getResource(resource);
        if (optional.isEmpty()) {
            return null;
        }
        try (var reader = new InputStreamReader(optional.get().open(), StandardCharsets.UTF_8)) {
            var parsed = JsonParser.parseReader(reader);
            return parsed != null && parsed.isJsonObject() ? parsed.getAsJsonObject() : null;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("ModelerBlockModelLoader: failed to parse {}: {}", resource, e.getMessage());
            return null;
        }
    }

    private static ResourceLocation blockstateResource(ResourceLocation blockId) {
        return ResourceLocation.fromNamespaceAndPath(blockId.getNamespace(), "blockstates/" + blockId.getPath() + ".json");
    }

    private static ResourceLocation modelResource(ResourceLocation modelId) {
        return ResourceLocation.fromNamespaceAndPath(modelId.getNamespace(), "models/" + modelId.getPath() + ".json");
    }

    private static @Nullable ResourceLocation parseId(@Nullable String raw, String fallbackNamespace) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        var trimmed = raw.trim();
        if (trimmed.indexOf(':') >= 0) {
            return ResourceLocation.tryParse(trimmed);
        }
        return ResourceLocation.tryParse(fallbackNamespace + ":" + trimmed);
    }

    private static @Nullable ModelerCube.Face face(String key) {
        return switch (key) {
            case "east" -> ModelerCube.Face.EAST;
            case "west" -> ModelerCube.Face.WEST;
            case "up" -> ModelerCube.Face.UP;
            case "down" -> ModelerCube.Face.DOWN;
            case "south" -> ModelerCube.Face.SOUTH;
            case "north" -> ModelerCube.Face.NORTH;
            default -> null;
        };
    }

    private static double @Nullable [] readUv(@Nullable JsonElement element, String face, double[] from, double[] to) {
        if (element != null && element.isJsonArray() && element.getAsJsonArray().size() >= 4) {
            var arr = element.getAsJsonArray();
            return new double[] {
                readDouble(arr.get(0), 0.0),
                readDouble(arr.get(1), 0.0),
                readDouble(arr.get(2), 16.0),
                readDouble(arr.get(3), 16.0)
            };
        }
        return defaultUv(face, from, to);
    }

    private static double[] defaultUv(String face, double[] from, double[] to) {
        return switch (face) {
            case "up", "down" -> new double[] { from[0], from[2], to[0], to[2] };
            case "east", "west" -> new double[] { from[2], 16.0 - to[1], to[2], 16.0 - from[1] };
            case "north", "south" -> new double[] { from[0], 16.0 - to[1], to[0], 16.0 - from[1] };
            default -> new double[] { 0.0, 0.0, 16.0, 16.0 };
        };
    }

    private static double[] readArray3(@Nullable JsonElement element, double[] fallback) {
        if (element == null || !element.isJsonArray() || element.getAsJsonArray().size() < 3) {
            return fallback;
        }
        var arr = element.getAsJsonArray();
        return new double[] {
            readDouble(arr.get(0), fallback[0]),
            readDouble(arr.get(1), fallback[1]),
            readDouble(arr.get(2), fallback[2])
        };
    }

    private static double readDouble(@Nullable JsonElement element, double fallback) {
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        try {
            return element.getAsDouble();
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    private static int normalizeUvRotation(int rotation) {
        var normalized = Math.floorMod(rotation, 360);
        return normalized % 90 == 0 ? normalized : 0;
    }

    private static @Nullable String readString(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        try {
            return element.getAsString();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private record ResolvedModel(
        String namespace,
        Map<String, String> textures,
        List<JsonObject> elements
    ) {}
}
