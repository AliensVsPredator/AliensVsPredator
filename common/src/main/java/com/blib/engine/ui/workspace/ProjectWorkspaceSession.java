package com.blib.engine.ui.workspace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;

import com.blib.engine.modeler.ModelerBlockModelLoader;
import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerCube;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.ModelerSceneLoader;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.animation.AnimationEditorState;
import com.blib.engine.modeler.texture.LoadedTexture;
import com.blib.engine.modeler.texture.TextureLoader;
import com.blib.engine.session.ProjectSession;
import com.blib.engine.texture.TextureEditorState;
import com.blib.engine.texture.TextureTool;
import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.dock.TabbedPanel;
import com.blib.engine.ui.panel.texture.TextureViewportPanel;
import com.blib.engine.ui.panel.uvmap.UvMapPanel;
import com.blib.internal.common.storage.EngineProjectIO;

@ApiStatus.Internal
public final class ProjectWorkspaceSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectWorkspaceSession.class);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String FILE_NAME = "workspace_session.json";

    private ProjectWorkspaceSession() {}

    public static boolean hasSnapshot(String projectName) {
        return projectName != null && !projectName.isEmpty() && Files.isRegularFile(sessionFile(projectName));
    }

    public static void saveCurrent(@Nullable DockNode root) {
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return;
        }
        var file = sessionFile(project);
        var json = new JsonObject();
        json.addProperty("version", 1);
        json.addProperty("savedAt", Instant.now().toString());
        json.add("modeler", captureModeler());
        json.add("textureEditor", captureTextureEditor());
        json.add("textureViewport", captureTextureViewport(root));
        json.add("uvMap", captureUvMap(root));
        json.add("animations", AnimationEditorState.get().sessionSnapshotJson());
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(json));
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("[BLib] ProjectWorkspaceSession.saveCurrent: failed to write {}", file, e);
        }
    }

    public static boolean restoreCurrentProject() {
        resetRuntimeState();
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return false;
        }
        var file = sessionFile(project);
        if (!Files.isRegularFile(file)) {
            return false;
        }
        try {
            var parsed = JsonParser.parseString(Files.readString(file));
            if (!parsed.isJsonObject()) {
                return false;
            }
            var root = parsed.getAsJsonObject();
            restoreModeler(jsonObject(root, "modeler"));
            restoreTextureEditor(jsonObject(root, "textureEditor"));
            restoreTextureViewport(jsonObject(root, "textureViewport"));
            restoreUvMap(jsonObject(root, "uvMap"));
            AnimationEditorState.get().restoreSessionJson(jsonObject(root, "animations"));
            return true;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("[BLib] ProjectWorkspaceSession.restoreCurrentProject: failed to read {}", file, e);
            return false;
        }
    }

    public static void resetRuntimeState() {
        ModelerScene.get().resetToEntity();
        AnimationEditorState.get().resetSession();
        TextureEditorState.setTool(TextureTool.SELECT);
        TextureEditorState.setPrimaryColor(0xFF202020);
        TextureEditorState.clearSelection();
        TextureViewportPanel.restoreSessionView(null);
        UvMapPanel.restoreSessionView(null);
    }

    private static Path sessionFile(String projectName) {
        return EngineProjectIO.projectRoot(projectName).resolve(FILE_NAME);
    }

    private static JsonObject captureModeler() {
        var scene = ModelerScene.get();
        var root = new JsonObject();

        var source = new JsonObject();
        source.addProperty("kind", scene.sourceKind.name().toLowerCase(Locale.ROOT));
        if (scene.sourceId != null) {
            source.addProperty("id", scene.sourceId.toString());
        }
        if (scene.sourcePath != null) {
            source.addProperty("path", scene.sourcePath.toString());
        }
        root.add("source", source);

        var camera = new JsonObject();
        camera.addProperty("yaw", scene.camera.yaw);
        camera.addProperty("pitch", scene.camera.pitch);
        camera.addProperty("distance", scene.camera.distance);
        camera.addProperty("fovDegrees", scene.camera.fovDegrees);
        camera.add("focusPoint", vec(scene.camera.focusPoint));
        root.add("camera", camera);

        root.addProperty("textureWidth", scene.textureWidth);
        root.addProperty("textureHeight", scene.textureHeight);
        root.add("textures", captureTextures(scene));
        root.addProperty("activeTextureIndex", scene.activeTexture == null ? -1 : scene.textures.indexOf(scene.activeTexture));
        var activeTextureKey = textureKey(scene.activeTexture);
        if (activeTextureKey != null) {
            root.addProperty("activeTextureKey", activeTextureKey);
        }

        var selection = captureSelection(scene.selection);
        if (selection != null) {
            root.add("selection", selection);
        }
        return root;
    }

    private static void restoreModeler(@Nullable JsonObject root) {
        if (root == null) {
            return;
        }
        restoreModelerSource(jsonObject(root, "source"));
        restoreCamera(jsonObject(root, "camera"));
        restoreTextures(root);
        restoreSelection(jsonObject(root, "selection"));
    }

    private static void restoreModelerSource(@Nullable JsonObject source) {
        if (source == null) {
            return;
        }
        var kind = jsonString(source, "kind");
        if ("external_geo".equals(kind)) {
            var pathText = jsonString(source, "path");
            if (pathText != null) {
                try {
                    ModelerSceneLoader.loadFromFile(Path.of(pathText));
                } catch (RuntimeException ignored) {
                    // Missing or malformed paths leave the clean seed model in place.
                }
            }
            return;
        }
        if ("java_block".equals(kind)) {
            var id = parseResource(jsonString(source, "id"));
            if (id != null) {
                ModelerBlockModelLoader.load(id);
            }
            return;
        }
        if ("item_config".equals(kind)) {
            var id = parseResource(jsonString(source, "id"));
            if (id != null) {
                ModelerScene.get().attachItemSession(id);
            }
        }
    }

    private static void restoreCamera(@Nullable JsonObject camera) {
        if (camera == null) {
            return;
        }
        var sceneCamera = ModelerScene.get().camera;
        sceneCamera.yaw = (float) jsonDouble(camera, "yaw", sceneCamera.yaw);
        sceneCamera.pitch = (float) jsonDouble(camera, "pitch", sceneCamera.pitch);
        sceneCamera.distance = (float) jsonDouble(camera, "distance", sceneCamera.distance);
        sceneCamera.fovDegrees = (float) jsonDouble(camera, "fovDegrees", sceneCamera.fovDegrees);
        var focus = vec(jsonArray(camera, "focusPoint"));
        if (focus != null) {
            sceneCamera.focusPoint = focus;
        }
        sceneCamera.clampPitch();
        sceneCamera.clampDistance();
    }

    private static JsonArray captureTextures(ModelerScene scene) {
        var arr = new JsonArray();
        for (var texture : scene.textures) {
            var obj = new JsonObject();
            obj.addProperty("displayName", texture.displayName());
            if (texture.sourcePath() != null) {
                obj.addProperty("source", "external");
                obj.addProperty("target", texture.sourcePath().toString());
            } else if (texture.sourceResource() != null) {
                obj.addProperty("source", "resource");
                obj.addProperty("target", texture.sourceResource().toString());
            } else {
                continue;
            }
            arr.add(obj);
        }
        return arr;
    }

    private static void restoreTextures(JsonObject root) {
        var scene = ModelerScene.get();
        var textures = jsonArray(root, "textures");
        if (textures == null) {
            return;
        }
        scene.closeTextures();
        var restored = new ArrayList<LoadedTexture>();
        for (var el : textures) {
            if (!el.isJsonObject()) {
                continue;
            }
            var obj = el.getAsJsonObject();
            var source = jsonString(obj, "source");
            var target = jsonString(obj, "target");
            if (target == null || target.isBlank()) {
                continue;
            }
            LoadedTexture loaded = null;
            if ("external".equals(source)) {
                try {
                    loaded = TextureLoader.loadFromDisk(Path.of(target));
                } catch (RuntimeException ignored) {
                    loaded = null;
                }
            } else if ("resource".equals(source)) {
                var id = parseResource(target);
                if (id != null) {
                    var displayName = jsonString(obj, "displayName");
                    loaded = TextureLoader.loadFromResource(id, displayName == null || displayName.isBlank() ? id.toString() : displayName);
                }
            }
            if (loaded != null) {
                restored.add(loaded);
            }
        }
        scene.textures.addAll(restored);
        var activeKey = jsonString(root, "activeTextureKey");
        if (activeKey != null) {
            for (var texture : scene.textures) {
                if (activeKey.equals(textureKey(texture))) {
                    scene.activeTexture = texture;
                    return;
                }
            }
        }
        var activeIndex = jsonInt(root, "activeTextureIndex", -1);
        scene.activeTexture = activeIndex >= 0 && activeIndex < scene.textures.size() ? scene.textures.get(activeIndex) : null;
    }

    private static @Nullable String textureKey(@Nullable LoadedTexture texture) {
        if (texture == null) {
            return null;
        }
        if (texture.sourcePath() != null) {
            return "external:" + texture.sourcePath();
        }
        if (texture.sourceResource() != null) {
            return "resource:" + texture.sourceResource();
        }
        return null;
    }

    private static @Nullable JsonObject captureSelection(@Nullable Selection selection) {
        if (selection == null) {
            return null;
        }
        var obj = new JsonObject();
        if (selection instanceof Selection.BoneSelection bs) {
            obj.addProperty("type", "bone");
            obj.add("bonePath", bonePath(bs.bone()));
            return obj;
        }
        if (selection instanceof Selection.CubeSelection cs) {
            obj.addProperty("type", "cube");
            obj.add("cube", cubeSelectionJson(cs));
            return obj;
        }
        if (selection instanceof Selection.FaceSelection fs) {
            obj.addProperty("type", "face");
            obj.add("cube", cubeSelectionJson(new Selection.CubeSelection(fs.owner(), fs.cube())));
            obj.addProperty("face", fs.face().name());
            return obj;
        }
        if (selection instanceof Selection.MultiCubeSelection ms) {
            obj.addProperty("type", "multi_cube");
            var cubes = new JsonArray();
            for (var cs : ms.cubes()) {
                cubes.add(cubeSelectionJson(cs));
            }
            obj.add("cubes", cubes);
            return obj;
        }
        return null;
    }

    private static void restoreSelection(@Nullable JsonObject obj) {
        var scene = ModelerScene.get();
        if (obj == null) {
            scene.selection = null;
            return;
        }
        var type = jsonString(obj, "type");
        if ("bone".equals(type)) {
            var bone = findBone(ModelerScene.get().root, jsonArray(obj, "bonePath"));
            scene.selection = bone == null ? null : new Selection.BoneSelection(bone);
            return;
        }
        if ("cube".equals(type)) {
            scene.selection = restoreCubeSelection(jsonObject(obj, "cube"));
            return;
        }
        if ("face".equals(type)) {
            var cubeSelection = restoreCubeSelection(jsonObject(obj, "cube"));
            if (cubeSelection instanceof Selection.CubeSelection cs) {
                try {
                    var face = ModelerCube.Face.valueOf(jsonString(obj, "face"));
                    scene.selection = new Selection.FaceSelection(cs.owner(), cs.cube(), face);
                } catch (RuntimeException ignored) {
                    scene.selection = cubeSelection;
                }
            }
            return;
        }
        if ("multi_cube".equals(type)) {
            var restored = new ArrayList<Selection.CubeSelection>();
            var cubes = jsonArray(obj, "cubes");
            if (cubes != null) {
                for (var el : cubes) {
                    if (!el.isJsonObject()) {
                        continue;
                    }
                    var selection = restoreCubeSelection(el.getAsJsonObject());
                    if (selection instanceof Selection.CubeSelection cs) {
                        restored.add(cs);
                    }
                }
            }
            scene.selection = restored.size() >= 2
                ? new Selection.MultiCubeSelection(restored)
                : (restored.size() == 1 ? restored.get(0) : null);
        }
    }

    private static JsonObject cubeSelectionJson(Selection.CubeSelection selection) {
        var obj = new JsonObject();
        obj.add("ownerPath", bonePath(selection.owner()));
        obj.addProperty("cubeName", selection.cube().name);
        obj.addProperty("cubeIndex", selection.owner().cubes.indexOf(selection.cube()));
        return obj;
    }

    private static @Nullable Selection restoreCubeSelection(@Nullable JsonObject obj) {
        if (obj == null) {
            return null;
        }
        var owner = findBone(ModelerScene.get().root, jsonArray(obj, "ownerPath"));
        if (owner == null) {
            return null;
        }
        var cube = findCube(owner, jsonString(obj, "cubeName"), jsonInt(obj, "cubeIndex", -1));
        return cube == null ? null : new Selection.CubeSelection(owner, cube);
    }

    private static JsonObject captureTextureEditor() {
        var root = new JsonObject();
        root.addProperty("tool", TextureEditorState.tool().name());
        root.addProperty("primaryColor", TextureEditorState.primaryColor());
        var selection = TextureEditorState.selection();
        if (selection != null) {
            var obj = new JsonObject();
            obj.addProperty("x0", selection.x0());
            obj.addProperty("y0", selection.y0());
            obj.addProperty("x1", selection.x1Exclusive());
            obj.addProperty("y1", selection.y1Exclusive());
            root.add("selection", obj);
        }
        return root;
    }

    private static void restoreTextureEditor(@Nullable JsonObject root) {
        if (root == null) {
            return;
        }
        try {
            TextureEditorState.setTool(TextureTool.valueOf(jsonString(root, "tool")));
        } catch (RuntimeException ignored) {
            TextureEditorState.setTool(TextureTool.SELECT);
        }
        TextureEditorState.setPrimaryColor(jsonInt(root, "primaryColor", TextureEditorState.primaryColor()));
        var selection = jsonObject(root, "selection");
        if (selection == null) {
            TextureEditorState.clearSelection();
        } else {
            TextureEditorState.setSelection(
                jsonInt(selection, "x0", 0),
                jsonInt(selection, "y0", 0),
                jsonInt(selection, "x1", 0),
                jsonInt(selection, "y1", 0)
            );
        }
    }

    private static JsonObject captureTextureViewport(@Nullable DockNode root) {
        var obj = new JsonObject();
        var panel = firstPanel(root, TextureViewportPanel.class);
        if (panel == null) {
            return obj;
        }
        var state = panel.sessionViewState();
        obj.addProperty("zoom", state.zoom());
        obj.addProperty("panX", state.panX());
        obj.addProperty("panY", state.panY());
        return obj;
    }

    private static void restoreTextureViewport(@Nullable JsonObject root) {
        if (root == null || !root.has("zoom")) {
            return;
        }
        TextureViewportPanel.restoreSessionView(
            new TextureViewportPanel.ViewState(
                jsonDouble(root, "zoom", 1.0),
                jsonDouble(root, "panX", 0.0),
                jsonDouble(root, "panY", 0.0)
            )
        );
    }

    private static JsonObject captureUvMap(@Nullable DockNode root) {
        var obj = new JsonObject();
        var panel = firstPanel(root, UvMapPanel.class);
        if (panel == null) {
            return obj;
        }
        var state = panel.sessionViewState();
        obj.addProperty("zoom", state.zoom());
        obj.addProperty("panOffsetX", state.panOffsetX());
        obj.addProperty("panOffsetY", state.panOffsetY());
        return obj;
    }

    private static void restoreUvMap(@Nullable JsonObject root) {
        if (root == null || !root.has("zoom")) {
            return;
        }
        UvMapPanel.restoreSessionView(
            new UvMapPanel.ViewState(
                jsonDouble(root, "zoom", 1.0),
                jsonDouble(root, "panOffsetX", 0.0),
                jsonDouble(root, "panOffsetY", 0.0)
            )
        );
    }

    private static JsonArray bonePath(ModelerBone bone) {
        var names = new ArrayList<String>();
        for (var current = bone; current != null; current = current.parent) {
            names.add(0, current.name);
        }
        var arr = new JsonArray();
        for (var name : names) {
            arr.add(name);
        }
        return arr;
    }

    private static @Nullable ModelerBone findBone(ModelerBone root, @Nullable JsonArray path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        var current = root;
        var index = jsonArrayString(path, 0).equals(root.name) ? 1 : 0;
        for (var i = index; i < path.size(); i++) {
            var name = jsonArrayString(path, i);
            ModelerBone match = null;
            for (var child : current.children) {
                if (child.name.equals(name)) {
                    match = child;
                    break;
                }
            }
            if (match == null) {
                return null;
            }
            current = match;
        }
        return current;
    }

    private static @Nullable ModelerCube findCube(ModelerBone owner, @Nullable String name, int index) {
        if (index >= 0 && index < owner.cubes.size()) {
            var cube = owner.cubes.get(index);
            if (name == null || name.equals(cube.name)) {
                return cube;
            }
        }
        if (name != null) {
            for (var cube : owner.cubes) {
                if (name.equals(cube.name)) {
                    return cube;
                }
            }
        }
        return null;
    }

    private static <T extends Panel> @Nullable T firstPanel(@Nullable DockNode node, Class<T> type) {
        if (node == null) {
            return null;
        }
        return switch (node) {
            case DockNode.Leaf leaf -> firstPanel(leaf.panel(), type);
            case DockNode.Split split -> {
                var first = firstPanel(split.first(), type);
                yield first != null ? first : firstPanel(split.second(), type);
            }
        };
    }

    private static <T extends Panel> @Nullable T firstPanel(Panel panel, Class<T> type) {
        if (type.isInstance(panel)) {
            return type.cast(panel);
        }
        if (panel instanceof TabbedPanel tabbed) {
            for (var tab : tabbed.tabs()) {
                var match = firstPanel(tab, type);
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    private static JsonArray vec(Vec3 vec) {
        var arr = new JsonArray();
        arr.add(vec.x);
        arr.add(vec.y);
        arr.add(vec.z);
        return arr;
    }

    private static @Nullable Vec3 vec(@Nullable JsonArray arr) {
        if (arr == null || arr.size() < 3) {
            return null;
        }
        return new Vec3(jsonArrayDouble(arr, 0), jsonArrayDouble(arr, 1), jsonArrayDouble(arr, 2));
    }

    private static @Nullable ResourceLocation parseResource(@Nullable String text) {
        return text == null || text.isBlank() ? null : ResourceLocation.tryParse(text);
    }

    private static @Nullable JsonObject jsonObject(JsonObject obj, String field) {
        return obj != null && obj.has(field) && obj.get(field).isJsonObject() ? obj.getAsJsonObject(field) : null;
    }

    private static @Nullable JsonArray jsonArray(JsonObject obj, String field) {
        return obj != null && obj.has(field) && obj.get(field).isJsonArray() ? obj.getAsJsonArray(field) : null;
    }

    private static @Nullable String jsonString(JsonObject obj, String field) {
        if (obj == null || !obj.has(field) || !obj.get(field).isJsonPrimitive()) {
            return null;
        }
        try {
            return obj.get(field).getAsString();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static int jsonInt(JsonObject obj, String field, int fallback) {
        if (obj == null || !obj.has(field) || !obj.get(field).isJsonPrimitive()) {
            return fallback;
        }
        try {
            return obj.get(field).getAsInt();
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static double jsonDouble(JsonObject obj, String field, double fallback) {
        if (obj == null || !obj.has(field) || !obj.get(field).isJsonPrimitive()) {
            return fallback;
        }
        try {
            return obj.get(field).getAsDouble();
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private static String jsonArrayString(JsonArray arr, int index) {
        if (arr == null || index < 0 || index >= arr.size() || !arr.get(index).isJsonPrimitive()) {
            return "";
        }
        try {
            return arr.get(index).getAsString();
        } catch (RuntimeException ignored) {
            return "";
        }
    }

    private static double jsonArrayDouble(JsonArray arr, int index) {
        if (arr == null || index < 0 || index >= arr.size() || !arr.get(index).isJsonPrimitive()) {
            return 0.0;
        }
        try {
            return arr.get(index).getAsDouble();
        } catch (RuntimeException ignored) {
            return 0.0;
        }
    }
}
