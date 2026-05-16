package com.blib.engine.modeler.animation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
import com.blib.engine.session.ProjectSession;
import com.blib.internal.client.animation.primitive.AzBakedAnimations;
import com.blib.internal.common.io.util.JsonUtil;
import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Heap-only authoring state for the Animation layout. The runtime animation classes bake source JSON into ticks,
 * radians, and deltas, so this editor keeps the source document as an editable {@link JsonObject} and only touches the
 * paths the user edits.
 */
@ApiStatus.Internal
public final class AnimationEditorState {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnimationEditorState.class);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final AnimationEditorState INSTANCE = new AnimationEditorState();

    public enum TransformChannel {
        POSITION("position"),
        ROTATION("rotation"),
        SCALE("scale");

        private final String jsonName;

        TransformChannel(String jsonName) {
            this.jsonName = jsonName;
        }

        public String jsonName() {
            return jsonName;
        }

        public static TransformChannel fromJsonName(@Nullable String value) {
            if (value == null) {
                return ROTATION;
            }
            for (var channel : values()) {
                if (channel.jsonName.equals(value)) {
                    return channel;
                }
            }
            return ROTATION;
        }
    }

    public record KeyframeRef(
        String animationName,
        String boneName,
        TransformChannel channel,
        double timestamp,
        JsonObject keyframe
    ) {}

    private @Nullable JsonObject draft;

    /** External file save target, mutually exclusive with {@link #projectResourceId}. */
    private @Nullable Path externalSavePath;

    /** Project resource-pack save target, resolved against the active project on each save. */
    private @Nullable ResourceLocation projectResourceId;

    private @Nullable Path lastSavedPath;

    private @Nullable String selectedAnimationName;

    private @Nullable String selectedBoneName;

    private TransformChannel selectedChannel = TransformChannel.ROTATION;

    private @Nullable Double selectedTimestamp;

    private boolean playing;

    private double playheadSeconds;

    private long lastPlaybackNanos;

    private boolean dirty;

    private @Nullable String statusMessage;

    private AnimationEditorState() {}

    public static AnimationEditorState get() {
        return INSTANCE;
    }

    public @Nullable JsonObject draft() {
        return draft;
    }

    public boolean hasDraft() {
        return draft != null;
    }

    public boolean isDirty() {
        return dirty;
    }

    public @Nullable String statusMessage() {
        return statusMessage;
    }

    public @Nullable String selectedAnimationName() {
        return selectedAnimationName;
    }

    public @Nullable String selectedBoneName() {
        return selectedBoneName;
    }

    public TransformChannel selectedChannel() {
        return selectedChannel;
    }

    public @Nullable Double selectedTimestamp() {
        return selectedTimestamp;
    }

    public boolean isPlaying() {
        return playing;
    }

    public double playheadSeconds() {
        return playheadSeconds;
    }

    public @Nullable ResourceLocation projectResourceId() {
        return projectResourceId;
    }

    public @Nullable Path externalSavePath() {
        return externalSavePath;
    }

    public @Nullable Path lastSavedPath() {
        return lastSavedPath;
    }

    public String targetLabel() {
        if (projectResourceId != null) {
            return projectResourceId.toString();
        }
        if (externalSavePath != null) {
            var name = externalSavePath.getFileName();
            return name == null ? externalSavePath.toString() : name.toString();
        }
        return "(unsaved)";
    }

    public void newDraft() {
        var root = new JsonObject();
        root.add("animations", new JsonObject());
        draft = root;
        externalSavePath = null;
        projectResourceId = null;
        lastSavedPath = null;
        selectedAnimationName = null;
        selectedTimestamp = null;
        stopPlayback();
        dirty = true;
        statusMessage = "New animation file";
    }

    public boolean openFromFile(Path path) {
        try {
            var parsed = JsonParser.parseString(Files.readString(path));
            if (!parsed.isJsonObject()) {
                statusMessage = "Animation file root must be a JSON object.";
                return false;
            }
            var root = parsed.getAsJsonObject();
            ensureAnimationsObject(root);
            draft = root;
            dirty = false;
            selectedAnimationName = firstAnimationName();
            selectedTimestamp = null;
            stopPlayback();
            lastSavedPath = path.toAbsolutePath().normalize();

            var projectResource = inferProjectResourceId(path);
            if (projectResource != null) {
                projectResourceId = projectResource;
                externalSavePath = null;
            } else {
                projectResourceId = null;
                externalSavePath = lastSavedPath;
            }

            var valid = validateCompatibility();
            statusMessage = valid
                ? "Opened " + targetLabel()
                : "Opened " + targetLabel() + "; Az validation failed.";
            return true;
        } catch (IOException | JsonSyntaxException | IllegalStateException e) {
            LOGGER.warn("AnimationEditorState: failed to open {}: {}", path, e.getMessage());
            statusMessage = "Failed to open " + path.getFileName();
            return false;
        }
    }

    public boolean canSave() {
        return draft != null
            && dirty
            && (externalSavePath != null || (projectResourceId != null && !ProjectSession.activeProjectName().isEmpty()));
    }

    public boolean save() {
        if (draft == null || !canSave()) {
            return false;
        }
        if (!validateCompatibility()) {
            statusMessage = "Animation JSON is not compatible with the Az parser.";
            return false;
        }
        if (projectResourceId != null) {
            return writeProject(projectResourceId);
        }
        if (externalSavePath != null) {
            return writeFile(externalSavePath);
        }
        return false;
    }

    public boolean saveAsFile(Path path) {
        if (draft == null) {
            return false;
        }
        if (!validateCompatibility()) {
            statusMessage = "Animation JSON is not compatible with the Az parser.";
            return false;
        }
        var target = ensureJsonExtension(path).toAbsolutePath().normalize();
        if (!writeFile(target)) {
            return false;
        }
        externalSavePath = target;
        projectResourceId = null;
        return true;
    }

    public boolean saveAsProject(ResourceLocation resourceId) {
        if (draft == null) {
            return false;
        }
        if (!validateCompatibility()) {
            statusMessage = "Animation JSON is not compatible with the Az parser.";
            return false;
        }
        var normalized = normalizeAnimationResourceId(resourceId);
        if (!writeProject(normalized)) {
            return false;
        }
        projectResourceId = normalized;
        externalSavePath = null;
        return true;
    }

    public boolean validateCompatibility() {
        if (draft == null) {
            return false;
        }
        try {
            JsonUtil.GEO_GSON.fromJson(draft, AzBakedAnimations.class);
            return true;
        } catch (RuntimeException e) {
            LOGGER.warn("AnimationEditorState: Az validation failed: {}", e.getMessage());
            return false;
        }
    }

    public List<String> animationNames() {
        var animations = animationsObjectOrNull();
        if (animations == null) {
            return List.of();
        }
        var out = new ArrayList<String>();
        for (var entry : animations.entrySet()) {
            out.add(entry.getKey());
        }
        out.sort(String.CASE_INSENSITIVE_ORDER);
        return out;
    }

    public @Nullable JsonObject selectedAnimationObject() {
        return animationObject(selectedAnimationName);
    }

    public @Nullable JsonObject animationObject(@Nullable String name) {
        var animations = animationsObjectOrNull();
        if (animations == null || name == null || !animations.has(name) || !animations.get(name).isJsonObject()) {
            return null;
        }
        return animations.getAsJsonObject(name);
    }

    public double selectedAnimationLengthSeconds() {
        var animation = selectedAnimationObject();
        if (animation == null) {
            return 1.0;
        }
        var length = readAnimationLength(animation);
        length = Math.max(length, maxKeyframeTimestamp(selectedAnimationName, animation));
        return Math.max(1.0, length);
    }

    public void updatePlaybackClock() {
        if (!playing) {
            return;
        }
        if (selectedAnimationName == null || selectedAnimationObject() == null) {
            stopPlayback();
            return;
        }
        var now = System.nanoTime();
        if (lastPlaybackNanos == 0L) {
            lastPlaybackNanos = now;
            return;
        }
        var elapsed = Math.max(0.0, (now - lastPlaybackNanos) / 1_000_000_000.0);
        lastPlaybackNanos = now;
        if (elapsed <= 0.0) {
            return;
        }
        var duration = selectedAnimationLengthSeconds();
        playheadSeconds += Math.min(elapsed, 0.25);
        if (playheadSeconds > duration) {
            playheadSeconds = duration <= 0.0 ? 0.0 : playheadSeconds % duration;
        }
    }

    public void togglePlayback() {
        setPlaying(!playing);
    }

    public void setPlaying(boolean playing) {
        if (!playing || selectedAnimationName == null || selectedAnimationObject() == null) {
            this.playing = false;
            lastPlaybackNanos = 0L;
            return;
        }
        this.playing = true;
        lastPlaybackNanos = System.nanoTime();
        playheadSeconds = clampPlayhead(playheadSeconds);
    }

    public void setPlayheadSeconds(double seconds) {
        playheadSeconds = clampPlayhead(seconds);
        if (playing) {
            lastPlaybackNanos = System.nanoTime();
        }
    }

    public void stopPlayback() {
        playing = false;
        playheadSeconds = 0.0;
        lastPlaybackNanos = 0L;
    }

    public void selectAnimation(@Nullable String name) {
        if (name == null || animationObject(name) == null) {
            selectedAnimationName = firstAnimationName();
        } else {
            selectedAnimationName = name;
        }
        selectedTimestamp = null;
        stopPlayback();
    }

    public String createAnimation(@Nullable String requestedName) {
        var animations = ensureAnimationsObject();
        var base = requestedName == null || requestedName.isBlank() ? "animation.new" : requestedName.trim();
        var name = uniqueName(base);
        var obj = new JsonObject();
        obj.addProperty("animation_length", 1.0);
        obj.add("bones", new JsonObject());
        animations.add(name, obj);
        selectedAnimationName = name;
        selectedTimestamp = null;
        stopPlayback();
        markDirty("Created " + name);
        return name;
    }

    public boolean renameAnimation(String oldName, String newName) {
        var animations = animationsObjectOrNull();
        if (animations == null || oldName == null || newName == null || newName.isBlank() || !animations.has(oldName)) {
            return false;
        }
        var clean = newName.trim();
        if (oldName.equals(clean)) {
            return false;
        }
        if (animations.has(clean)) {
            statusMessage = "Animation '" + clean + "' already exists.";
            return false;
        }

        var rebuilt = new JsonObject();
        JsonElement moved = null;
        for (var entry : animations.entrySet()) {
            if (entry.getKey().equals(oldName)) {
                moved = entry.getValue();
                rebuilt.add(clean, moved);
            } else {
                rebuilt.add(entry.getKey(), entry.getValue());
            }
        }
        draft.add("animations", rebuilt);
        selectedAnimationName = clean;
        markDirty("Renamed " + oldName + " to " + clean);
        return true;
    }

    public @Nullable String duplicateAnimation(@Nullable String sourceName) {
        var source = animationObject(sourceName);
        if (source == null) {
            return null;
        }
        var animations = ensureAnimationsObject();
        var name = uniqueName(sourceName + "_copy");
        animations.add(name, source.deepCopy());
        selectedAnimationName = name;
        selectedTimestamp = null;
        stopPlayback();
        markDirty("Duplicated " + sourceName);
        return name;
    }

    public boolean deleteAnimation(@Nullable String name) {
        var animations = animationsObjectOrNull();
        if (animations == null || name == null || !animations.has(name)) {
            return false;
        }
        animations.remove(name);
        if (name.equals(selectedAnimationName)) {
            selectedAnimationName = firstAnimationName();
            selectedTimestamp = null;
            stopPlayback();
        }
        markDirty("Deleted " + name);
        return true;
    }

    public void selectBone(@Nullable String boneName) {
        selectedBoneName = boneName == null || boneName.isBlank() ? null : boneName;
        selectedTimestamp = null;
    }

    public void syncSelectedBoneFromScene() {
        var selection = ModelerScene.get().selection;
        if (selection instanceof Selection.BoneSelection bs) {
            var nextBoneName = bs.bone().name;
            if (!nextBoneName.equals(selectedBoneName)) {
                selectedBoneName = nextBoneName;
                selectedTimestamp = null;
            }
        }
    }

    public void onBoneRenamed(ModelerBone bone, String oldName, String newName) {
        if (selectedBoneName != null && selectedBoneName.equals(oldName)) {
            selectedBoneName = newName;
        }
    }

    public void selectChannel(TransformChannel channel) {
        selectedChannel = channel == null ? TransformChannel.ROTATION : channel;
        selectedTimestamp = null;
    }

    public void selectKeyframe(String animationName, String boneName, TransformChannel channel, double timestamp) {
        selectedAnimationName = animationName;
        selectedBoneName = boneName;
        selectedChannel = channel;
        selectedTimestamp = timestamp;
    }

    public List<KeyframeRef> selectedKeyframes() {
        if (selectedAnimationName == null || selectedBoneName == null) {
            return List.of();
        }
        return keyframes(selectedAnimationName, selectedBoneName, selectedChannel);
    }

    public List<KeyframeRef> keyframes(String animationName, String boneName, TransformChannel channel) {
        var boneObj = boneAnimationObject(animationName, boneName);
        if (boneObj == null || !boneObj.has(channel.jsonName())) {
            return List.of();
        }
        var refs = readKeyframes(animationName, boneName, channel, boneObj.get(channel.jsonName()));
        refs.sort(Comparator.comparingDouble(KeyframeRef::timestamp));
        return refs;
    }

    public @Nullable KeyframeRef selectedKeyframe() {
        if (selectedAnimationName == null || selectedBoneName == null || selectedTimestamp == null) {
            return null;
        }
        for (var frame : keyframes(selectedAnimationName, selectedBoneName, selectedChannel)) {
            if (Math.abs(frame.timestamp() - selectedTimestamp) < 1.0e-6) {
                return frame;
            }
        }
        return null;
    }

    public KeyframeRef createOrUpdateSelectedKeyframe() {
        var animation = selectedAnimationName != null ? selectedAnimationName : createAnimation(null);
        var bone = selectedBoneName;
        if (bone == null || bone.isBlank()) {
            bone = "bone";
            selectedBoneName = bone;
        }
        var timestamp = selectedTimestamp != null ? selectedTimestamp : 0.0;
        var frame = ensureKeyframe(animation, bone, selectedChannel, timestamp);
        selectedTimestamp = timestamp;
        markDirty("Edited keyframe");
        return new KeyframeRef(animation, bone, selectedChannel, timestamp, frame);
    }

    public boolean deleteSelectedKeyframe() {
        if (selectedAnimationName == null || selectedBoneName == null || selectedTimestamp == null) {
            return false;
        }
        var deleted = deleteKeyframe(selectedAnimationName, selectedBoneName, selectedChannel, selectedTimestamp);
        if (deleted) {
            selectedTimestamp = null;
            markDirty("Deleted keyframe");
        }
        return deleted;
    }

    public void moveSelectedKeyframe(String newBoneName, TransformChannel newChannel, double newTimestamp) {
        var current = selectedKeyframe();
        if (current == null) {
            selectedBoneName = newBoneName;
            selectedChannel = newChannel;
            selectedTimestamp = newTimestamp;
            createOrUpdateSelectedKeyframe();
            return;
        }

        var copy = current.keyframe().deepCopy();
        deleteKeyframe(current.animationName(), current.boneName(), current.channel(), current.timestamp());
        var normalizedTimestamp = Math.max(0.0, newTimestamp);
        putKeyframe(current.animationName(), newBoneName, newChannel, normalizedTimestamp, copy);
        selectedBoneName = newBoneName;
        selectedChannel = newChannel;
        selectedTimestamp = normalizedTimestamp;
        markDirty("Moved keyframe");
    }

    public void setSelectedVectorAxis(int axis, JsonElement value) {
        var frame = createOrUpdateSelectedKeyframe();
        var vector = ensureVector(frame.keyframe());
        while (vector.size() < 3) {
            vector.add(0.0);
        }
        vector.set(Math.max(0, Math.min(2, axis)), value);
        markDirty("Edited keyframe vector");
    }

    public void setSelectedEasing(@Nullable String easing) {
        var frame = createOrUpdateSelectedKeyframe();
        if (easing == null || easing.isBlank()) {
            frame.keyframe().remove("easing");
        } else {
            frame.keyframe().addProperty("easing", easing.trim());
        }
        markDirty("Edited keyframe easing");
    }

    public void setSelectedEasingArgs(@Nullable List<Double> args) {
        var frame = createOrUpdateSelectedKeyframe();
        if (args == null || args.isEmpty()) {
            frame.keyframe().remove("easingArgs");
        } else {
            var array = new JsonArray();
            for (var arg : args) {
                array.add(arg);
            }
            frame.keyframe().add("easingArgs", array);
        }
        markDirty("Edited keyframe easing args");
    }

    private @Nullable JsonObject boneAnimationObject(String animationName, String boneName) {
        var animation = animationObject(animationName);
        if (animation == null || !animation.has("bones") || !animation.get("bones").isJsonObject()) {
            return null;
        }
        var bones = animation.getAsJsonObject("bones");
        if (!bones.has(boneName) || !bones.get(boneName).isJsonObject()) {
            return null;
        }
        return bones.getAsJsonObject(boneName);
    }

    private JsonObject ensureBoneAnimationObject(String animationName, String boneName) {
        var animation = animationObject(animationName);
        if (animation == null) {
            animation = new JsonObject();
            animation.add("bones", new JsonObject());
            ensureAnimationsObject().add(animationName, animation);
            selectedAnimationName = animationName;
        }
        if (!animation.has("bones") || !animation.get("bones").isJsonObject()) {
            animation.add("bones", new JsonObject());
        }
        var bones = animation.getAsJsonObject("bones");
        if (!bones.has(boneName) || !bones.get(boneName).isJsonObject()) {
            bones.add(boneName, new JsonObject());
        }
        return bones.getAsJsonObject(boneName);
    }

    private JsonObject ensureKeyframe(String animationName, String boneName, TransformChannel channel, double timestamp) {
        var bone = ensureBoneAnimationObject(animationName, boneName);
        ensureAnimationLength(animationName, timestamp);
        var channelObj = normalizeChannelObject(animationName, boneName, channel, bone.get(channel.jsonName()));
        bone.add(channel.jsonName(), channelObj);
        var key = formatTimestamp(timestamp);
        if (!channelObj.has(key) || !channelObj.get(key).isJsonObject()) {
            var frame = new JsonObject();
            var vector = new JsonArray();
            vector.add(0.0);
            vector.add(0.0);
            vector.add(0.0);
            frame.add("vector", vector);
            channelObj.add(key, frame);
        }
        return channelObj.getAsJsonObject(key);
    }

    private void putKeyframe(
        String animationName,
        String boneName,
        TransformChannel channel,
        double timestamp,
        JsonObject frame
    ) {
        var bone = ensureBoneAnimationObject(animationName, boneName);
        ensureAnimationLength(animationName, timestamp);
        var channelObj = normalizeChannelObject(animationName, boneName, channel, bone.get(channel.jsonName()));
        bone.add(channel.jsonName(), channelObj);
        channelObj.add(formatTimestamp(timestamp), frame);
    }

    private void ensureAnimationLength(String animationName, double timestamp) {
        var animation = animationObject(animationName);
        if (animation == null || timestamp <= 0.0) {
            return;
        }
        var current = 0.0;
        try {
            if (animation.has("animation_length") && animation.get("animation_length").isJsonPrimitive()) {
                current = animation.get("animation_length").getAsDouble();
            }
        } catch (RuntimeException ignored) {
            current = 0.0;
        }
        if (timestamp > current) {
            animation.addProperty("animation_length", timestamp);
        }
        playheadSeconds = clampPlayhead(playheadSeconds);
    }

    private double clampPlayhead(double seconds) {
        if (!Double.isFinite(seconds)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(selectedAnimationLengthSeconds(), seconds));
    }

    private static double readAnimationLength(JsonObject animation) {
        if (!animation.has("animation_length") || !animation.get("animation_length").isJsonPrimitive()) {
            return 0.0;
        }
        try {
            return Math.max(0.0, animation.get("animation_length").getAsDouble());
        } catch (RuntimeException ignored) {
            return 0.0;
        }
    }

    private static double maxKeyframeTimestamp(@Nullable String animationName, JsonObject animation) {
        if (animationName == null || !animation.has("bones") || !animation.get("bones").isJsonObject()) {
            return 0.0;
        }
        var max = 0.0;
        for (var boneEntry : animation.getAsJsonObject("bones").entrySet()) {
            if (!boneEntry.getValue().isJsonObject()) {
                continue;
            }
            var bone = boneEntry.getValue().getAsJsonObject();
            for (var channel : TransformChannel.values()) {
                if (!bone.has(channel.jsonName())) {
                    continue;
                }
                for (var ref : readKeyframes(animationName, boneEntry.getKey(), channel, bone.get(channel.jsonName()))) {
                    max = Math.max(max, ref.timestamp());
                }
            }
        }
        return max;
    }

    private boolean deleteKeyframe(String animationName, String boneName, TransformChannel channel, double timestamp) {
        var bone = boneAnimationObject(animationName, boneName);
        if (bone == null || !bone.has(channel.jsonName())) {
            return false;
        }
        var channelObj = normalizeChannelObject(animationName, boneName, channel, bone.get(channel.jsonName()));
        bone.add(channel.jsonName(), channelObj);
        return channelObj.remove(formatTimestamp(timestamp)) != null;
    }

    private JsonObject normalizeChannelObject(
        String animationName,
        String boneName,
        TransformChannel channel,
        @Nullable JsonElement existing
    ) {
        var out = new JsonObject();
        if (existing == null || existing.isJsonNull()) {
            return out;
        }
        for (var ref : readKeyframes(animationName, boneName, channel, existing)) {
            out.add(formatTimestamp(ref.timestamp()), ref.keyframe().deepCopy());
        }
        return out;
    }

    private static List<KeyframeRef> readKeyframes(
        String animationName,
        String boneName,
        TransformChannel channel,
        JsonElement element
    ) {
        if (element == null || element.isJsonNull()) {
            return List.of();
        }
        var out = new ArrayList<KeyframeRef>();
        if (element.isJsonPrimitive()) {
            out.add(new KeyframeRef(animationName, boneName, channel, 0.0, frame(vectorFromPrimitive(element.getAsJsonPrimitive()))));
            return out;
        }
        if (element.isJsonArray()) {
            out.add(new KeyframeRef(animationName, boneName, channel, 0.0, frame(copyVector(element.getAsJsonArray()))));
            return out;
        }
        if (!element.isJsonObject()) {
            return out;
        }
        var obj = element.getAsJsonObject();
        if (obj.has("vector")) {
            out.add(new KeyframeRef(animationName, boneName, channel, 0.0, normalizeFrameObject(obj)));
            return out;
        }
        for (var entry : obj.entrySet()) {
            if (!NumberUtils.isCreatable(entry.getKey())) {
                continue;
            }
            var timestamp = Double.parseDouble(entry.getKey());
            var normalized = normalizeTimestampEntry(entry.getValue());
            if (normalized != null) {
                out.add(new KeyframeRef(animationName, boneName, channel, timestamp, normalized));
            }
        }
        return out;
    }

    private static @Nullable JsonObject normalizeTimestampEntry(JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return null;
        }
        if (value.isJsonArray()) {
            return frame(copyVector(value.getAsJsonArray()));
        }
        if (!value.isJsonObject()) {
            return null;
        }
        var obj = value.getAsJsonObject();
        if (obj.has("vector")) {
            return normalizeFrameObject(obj);
        }
        if (obj.has("post")) {
            var frame = frame(vectorFromPrePostValue(obj.get("post")));
            if (obj.has("lerp_mode")) {
                frame.add("easing", obj.get("lerp_mode").deepCopy());
            }
            return frame;
        }
        if (obj.has("pre")) {
            return frame(vectorFromPrePostValue(obj.get("pre")));
        }
        return null;
    }

    private static JsonObject normalizeFrameObject(JsonObject source) {
        var out = new JsonObject();
        var vector = source.has("vector") && source.get("vector").isJsonArray()
            ? copyVector(source.getAsJsonArray("vector"))
            : defaultVector();
        out.add("vector", vector);
        copyIfPresent(source, out, "easing");
        copyIfPresent(source, out, "easingArgs");
        return out;
    }

    private static void copyIfPresent(JsonObject source, JsonObject target, String key) {
        if (source.has(key)) {
            target.add(key, source.get(key).deepCopy());
        }
    }

    private static JsonObject frame(JsonArray vector) {
        var obj = new JsonObject();
        obj.add("vector", vector);
        return obj;
    }

    private static JsonArray vectorFromPrimitive(JsonPrimitive primitive) {
        var array = new JsonArray();
        array.add(primitive.deepCopy());
        array.add(primitive.deepCopy());
        array.add(primitive.deepCopy());
        return array;
    }

    private static JsonArray vectorFromPrePostValue(JsonElement value) {
        if (value != null && value.isJsonArray()) {
            return copyVector(value.getAsJsonArray());
        }
        if (value != null && value.isJsonObject()) {
            var obj = value.getAsJsonObject();
            if (obj.has("vector") && obj.get("vector").isJsonArray()) {
                return copyVector(obj.getAsJsonArray("vector"));
            }
        }
        return defaultVector();
    }

    private static JsonArray copyVector(JsonArray source) {
        var out = new JsonArray();
        for (var i = 0; i < 3; i++) {
            if (i < source.size()) {
                out.add(source.get(i).deepCopy());
            } else {
                out.add(0.0);
            }
        }
        return out;
    }

    private static JsonArray defaultVector() {
        var out = new JsonArray();
        out.add(0.0);
        out.add(0.0);
        out.add(0.0);
        return out;
    }

    private static JsonArray ensureVector(JsonObject frame) {
        if (!frame.has("vector") || !frame.get("vector").isJsonArray()) {
            frame.add("vector", defaultVector());
        }
        return frame.getAsJsonArray("vector");
    }

    private @Nullable JsonObject animationsObjectOrNull() {
        if (draft == null || !draft.has("animations") || !draft.get("animations").isJsonObject()) {
            return null;
        }
        return draft.getAsJsonObject("animations");
    }

    private JsonObject ensureAnimationsObject() {
        if (draft == null) {
            newDraft();
        }
        ensureAnimationsObject(draft);
        return draft.getAsJsonObject("animations");
    }

    private static void ensureAnimationsObject(JsonObject root) {
        if (!root.has("animations") || !root.get("animations").isJsonObject()) {
            root.add("animations", new JsonObject());
        }
    }

    private @Nullable String firstAnimationName() {
        var animations = animationsObjectOrNull();
        if (animations == null) {
            return null;
        }
        for (var entry : animations.entrySet()) {
            return entry.getKey();
        }
        return null;
    }

    private String uniqueName(String base) {
        var animations = ensureAnimationsObject();
        var cleanBase = base == null || base.isBlank() ? "animation.new" : base.trim();
        if (!animations.has(cleanBase)) {
            return cleanBase;
        }
        for (var i = 2; i < 10_000; i++) {
            var candidate = cleanBase + "_" + i;
            if (!animations.has(candidate)) {
                return candidate;
            }
        }
        return cleanBase + "_" + System.currentTimeMillis();
    }

    private void markDirty(String message) {
        dirty = true;
        statusMessage = message;
    }

    private boolean writeFile(Path path) {
        if (draft == null) {
            return false;
        }
        try {
            var parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, GSON.toJson(draft));
            lastSavedPath = path;
            dirty = false;
            statusMessage = "Saved " + path.getFileName();
            return true;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("AnimationEditorState: failed to save {}: {}", path, e.getMessage());
            statusMessage = "Failed to save " + path.getFileName();
            return false;
        }
    }

    private boolean writeProject(ResourceLocation resourceId) {
        if (draft == null) {
            return false;
        }
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            statusMessage = "Open a project before saving to a resource pack.";
            return false;
        }
        var relPath = "assets/" + resourceId.getNamespace() + "/" + resourceId.getPath();
        try {
            var path = EngineProjectIO.prepareAssetPath(project, relPath);
            Files.writeString(path, GSON.toJson(draft));
            lastSavedPath = path;
            dirty = false;
            statusMessage = "Saved " + resourceId;
            return true;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("AnimationEditorState: failed to save {} in project {}: {}", resourceId, project, e.getMessage());
            statusMessage = "Failed to save " + resourceId;
            return false;
        }
    }

    private static Path ensureJsonExtension(Path path) {
        var value = path.toString();
        return value.toLowerCase(Locale.ROOT).endsWith(".json") ? path : Path.of(value + ".json");
    }

    public static ResourceLocation normalizeAnimationResourceId(ResourceLocation id) {
        var path = id.getPath();
        if (!path.startsWith("animations/")) {
            path = "animations/" + path;
        }
        if (!path.endsWith(".json")) {
            path += ".json";
        }
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path);
    }

    private static @Nullable ResourceLocation inferProjectResourceId(Path path) {
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return null;
        }
        var resourceRoot = EngineProjectIO.resourcepackRoot(project).toAbsolutePath().normalize();
        var normalized = path.toAbsolutePath().normalize();
        if (!normalized.startsWith(resourceRoot.resolve("assets"))) {
            return null;
        }
        var rel = resourceRoot.relativize(normalized).toString().replace('\\', '/');
        if (!rel.startsWith("assets/")) {
            return null;
        }
        var withoutAssets = rel.substring("assets/".length());
        var slash = withoutAssets.indexOf('/');
        if (slash <= 0 || slash >= withoutAssets.length() - 1) {
            return null;
        }
        return ResourceLocation.tryParse(withoutAssets.substring(0, slash) + ":" + withoutAssets.substring(slash + 1));
    }

    public static String formatTimestamp(double value) {
        if (Math.abs(value - Math.rint(value)) < 1.0e-6) {
            return String.valueOf((long) Math.rint(value));
        }
        var formatted = String.format(Locale.ROOT, "%.4f", value);
        while (formatted.contains(".") && formatted.endsWith("0")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        if (formatted.endsWith(".")) {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        return formatted;
    }

    public static String elementToText(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return "0";
        }
        if (element.isJsonPrimitive()) {
            var primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return formatNumber(primitive.getAsDouble());
            }
            return primitive.getAsString();
        }
        return element.toString();
    }

    public static JsonElement textToElement(String text) {
        var clean = text == null ? "" : text.trim();
        if (clean.isEmpty()) {
            return new JsonPrimitive(0.0);
        }
        if (NumberUtils.isCreatable(clean)) {
            return new JsonPrimitive(Double.parseDouble(clean));
        }
        return new JsonPrimitive(clean);
    }

    public static String formatNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 1.0e-6) {
            return String.valueOf((long) Math.rint(value));
        }
        return String.format(Locale.ROOT, "%.3f", value);
    }

    public static @Nullable Double parseSeconds(String text) {
        try {
            return Math.max(0.0, Double.parseDouble(text.trim()));
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static List<Double> parseEasingArgs(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        var out = new ArrayList<Double>();
        for (var part : text.split(",")) {
            var clean = part.trim();
            if (!clean.isEmpty()) {
                out.add(Double.parseDouble(clean));
            }
        }
        return out;
    }

    public static String easingArgsToText(@Nullable JsonElement element) {
        if (element == null || !element.isJsonArray()) {
            return "";
        }
        var parts = new ArrayList<String>();
        for (var item : element.getAsJsonArray()) {
            parts.add(elementToText(item));
        }
        return String.join(", ", parts);
    }
}
