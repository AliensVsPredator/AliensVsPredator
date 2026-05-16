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
import net.minecraft.world.phys.Vec3;
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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.blib.engine.modeler.ModelerBone;
import com.blib.engine.modeler.ModelerScene;
import com.blib.engine.modeler.Selection;
import com.blib.engine.session.ProjectSession;
import com.blib.internal.client.animation.easing.AzEasingTypeRegistry;
import com.blib.internal.client.animation.easing.AzEasingTypeLoader;
import com.blib.internal.client.animation.easing.AzEasingTypes;
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

    private static final double DEFAULT_PLAYBACK_SPEED_PERCENT = 100.0;

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
        int documentId,
        String animationName,
        String boneName,
        TransformChannel channel,
        double timestamp,
        JsonObject keyframe
    ) {}

    public record AnimationDocumentRef(
        int id,
        String label,
        boolean dirty,
        boolean active
    ) {}

    public record AnimationKey(
        int documentId,
        String animationName
    ) {}

    public record PreviewBoneTransform(
        @Nullable Vec3 position,
        @Nullable Vec3 rotationDelta,
        @Nullable Vec3 scale
    ) {}

    private static final class AnimationDocument {
        private final int id;

        private JsonObject draft;

        private @Nullable Path externalSavePath;

        private @Nullable ResourceLocation projectResourceId;

        private @Nullable Path lastSavedPath;

        private boolean dirty;

        private AnimationDocument(int id, JsonObject draft) {
            this.id = id;
            this.draft = draft;
        }

        private String targetLabel() {
            if (projectResourceId != null) {
                return projectResourceId.toString();
            }
            if (externalSavePath != null) {
                var name = externalSavePath.getFileName();
                return name == null ? externalSavePath.toString() : name.toString();
            }
            return "(unsaved)";
        }
    }

    private @Nullable JsonObject draft;

    /** External file save target, mutually exclusive with {@link #projectResourceId}. */
    private @Nullable Path externalSavePath;

    /** Project resource-pack save target, resolved against the active project on each save. */
    private @Nullable ResourceLocation projectResourceId;

    private @Nullable Path lastSavedPath;

    private @Nullable String selectedAnimationName;

    private final LinkedHashSet<String> selectedAnimationNames = new LinkedHashSet<>();

    private final LinkedHashSet<AnimationKey> selectedAnimationKeys = new LinkedHashSet<>();

    private final List<AnimationDocument> documents = new ArrayList<>();

    private int nextDocumentId = 1;

    private @Nullable Integer activeDocumentId;

    private @Nullable String selectedBoneName;

    private TransformChannel selectedChannel = TransformChannel.ROTATION;

    private @Nullable Double selectedTimestamp;

    private boolean playing;

    private double playheadSeconds;

    private double playbackSpeedPercent = DEFAULT_PLAYBACK_SPEED_PERCENT;

    private long lastPlaybackNanos;

    private boolean dirty;

    private @Nullable String statusMessage;

    private AnimationEditorState() {}

    public static AnimationEditorState get() {
        return INSTANCE;
    }

    public List<AnimationDocumentRef> documents() {
        syncActiveDocument();
        var out = new ArrayList<AnimationDocumentRef>();
        for (var document : documents) {
            out.add(new AnimationDocumentRef(document.id, document.targetLabel(), document.dirty, isActiveDocument(document.id)));
        }
        return out;
    }

    public @Nullable Integer selectedDocumentId() {
        return activeDocumentId;
    }

    public void selectDocument(int documentId) {
        var document = document(documentId);
        if (document == null) {
            return;
        }
        var changedDocument = activeDocumentId == null || activeDocumentId != documentId;
        syncActiveDocument();
        activateDocument(document);
        if (selectedAnimationName == null || animationObject(documentId, selectedAnimationName) == null) {
            selectedAnimationName = firstAnimationName(document);
        }
        syncSelectedAnimationNames();
        if (changedDocument) {
            selectedTimestamp = null;
        }
    }

    public @Nullable JsonObject draft() {
        return draft;
    }

    public boolean hasDraft() {
        return activeDocument() != null;
    }

    public boolean isDirty() {
        syncActiveDocument();
        for (var document : documents) {
            if (document.dirty) {
                return true;
            }
        }
        return false;
    }

    public @Nullable String statusMessage() {
        return statusMessage;
    }

    public @Nullable String selectedAnimationName() {
        return selectedAnimationName;
    }

    public List<String> selectedAnimationNames() {
        syncSelectedAnimationNames();
        if (selectedAnimationNames.isEmpty()) {
            return List.of();
        }
        var out = new ArrayList<String>();
        for (var name : selectedAnimationNames) {
            if (animationObject(name) != null) {
                out.add(name);
            }
        }
        return out;
    }

    public boolean isAnimationSelected(@Nullable String name) {
        return name != null && activeDocumentId != null && isAnimationSelected(activeDocumentId, name);
    }

    public boolean isAnimationSelected(int documentId, @Nullable String name) {
        return name != null && selectedAnimationKeys.contains(new AnimationKey(documentId, name));
    }

    public List<AnimationKey> selectedAnimationKeys() {
        var out = new ArrayList<AnimationKey>();
        for (var key : selectedAnimationKeys) {
            if (animationObject(key.documentId(), key.animationName()) != null) {
                out.add(key);
            }
        }
        return out;
    }

    public boolean hasPlayableSelection() {
        return !playbackAnimationKeys().isEmpty();
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

    public double playbackSpeedPercent() {
        return playbackSpeedPercent;
    }

    public void setPlaybackSpeedPercent(double playbackSpeedPercent) {
        if (!Double.isFinite(playbackSpeedPercent)) {
            this.playbackSpeedPercent = DEFAULT_PLAYBACK_SPEED_PERCENT;
            return;
        }
        this.playbackSpeedPercent = Math.max(0.0, playbackSpeedPercent);
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
        var document = activeDocument();
        if (document != null) {
            return document.targetLabel();
        }
        if (projectResourceId != null) {
            return projectResourceId.toString();
        }
        if (externalSavePath != null) {
            var name = externalSavePath.getFileName();
            return name == null ? externalSavePath.toString() : name.toString();
        }
        return "(unsaved)";
    }

    public String targetLabel(int documentId) {
        var document = document(documentId);
        return document == null ? "(missing)" : document.targetLabel();
    }

    public void newDraft() {
        syncActiveDocument();
        var root = new JsonObject();
        root.add("animations", new JsonObject());
        var document = new AnimationDocument(nextDocumentId++, root);
        document.dirty = true;
        documents.add(document);
        activateDocument(document);
        selectSingleAnimation(null);
        selectedTimestamp = null;
        stopPlayback();
        statusMessage = "New animation file";
    }

    public boolean openFromFile(Path path) {
        try {
            syncActiveDocument();
            var normalizedPath = path.toAbsolutePath().normalize();
            for (var document : documents) {
                if (Objects.equals(document.lastSavedPath, normalizedPath) || Objects.equals(document.externalSavePath, normalizedPath)) {
                    activateDocument(document);
                    selectSingleAnimation(resolveAnimationName(selectedAnimationName));
                    selectedTimestamp = null;
                    stopPlayback();
                    statusMessage = "Opened " + targetLabel();
                    return true;
                }
            }

            var parsed = JsonParser.parseString(Files.readString(path));
            if (!parsed.isJsonObject()) {
                statusMessage = "Animation file root must be a JSON object.";
                return false;
            }
            var root = parsed.getAsJsonObject();
            ensureAnimationsObject(root);
            var document = new AnimationDocument(nextDocumentId++, root);
            document.dirty = false;
            document.lastSavedPath = normalizedPath;

            var projectResource = inferProjectResourceId(path);
            if (projectResource != null) {
                document.projectResourceId = projectResource;
                document.externalSavePath = null;
            } else {
                document.projectResourceId = null;
                document.externalSavePath = normalizedPath;
            }
            documents.add(document);
            activateDocument(document);
            selectSingleAnimation(firstAnimationName(document));
            selectedTimestamp = null;
            stopPlayback();

            var valid = validateCompatibility(document);
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
        syncActiveDocument();
        var document = activeDocument();
        return document != null
            && document.dirty
            && (document.externalSavePath != null || (document.projectResourceId != null && !ProjectSession.activeProjectName().isEmpty()));
    }

    public boolean save() {
        syncActiveDocument();
        var document = activeDocument();
        if (document == null || !canSave()) {
            return false;
        }
        if (!validateCompatibility(document)) {
            statusMessage = "Animation JSON is not compatible with the Az parser.";
            return false;
        }
        if (document.projectResourceId != null) {
            return writeProject(document, document.projectResourceId);
        }
        if (document.externalSavePath != null) {
            return writeFile(document, document.externalSavePath);
        }
        return false;
    }

    public boolean saveAsFile(Path path) {
        syncActiveDocument();
        var document = activeDocument();
        if (document == null) {
            return false;
        }
        if (!validateCompatibility(document)) {
            statusMessage = "Animation JSON is not compatible with the Az parser.";
            return false;
        }
        var target = ensureJsonExtension(path).toAbsolutePath().normalize();
        if (!writeFile(document, target)) {
            return false;
        }
        document.externalSavePath = target;
        document.projectResourceId = null;
        activateDocument(document);
        return true;
    }

    public boolean saveAsProject(ResourceLocation resourceId) {
        syncActiveDocument();
        var document = activeDocument();
        if (document == null) {
            return false;
        }
        if (!validateCompatibility(document)) {
            statusMessage = "Animation JSON is not compatible with the Az parser.";
            return false;
        }
        var normalized = normalizeAnimationResourceId(resourceId);
        if (!writeProject(document, normalized)) {
            return false;
        }
        document.projectResourceId = normalized;
        document.externalSavePath = null;
        activateDocument(document);
        return true;
    }

    public boolean validateCompatibility() {
        var document = activeDocument();
        if (document == null) {
            return false;
        }
        return validateCompatibility(document);
    }

    private boolean validateCompatibility(AnimationDocument document) {
        try {
            JsonUtil.GEO_GSON.fromJson(document.draft, AzBakedAnimations.class);
            return true;
        } catch (RuntimeException e) {
            LOGGER.warn("AnimationEditorState: Az validation failed: {}", e.getMessage());
            return false;
        }
    }

    public List<String> animationNames() {
        var animations = animationsObjectOrNull();
        return sortedAnimationNames(animations);
    }

    public List<String> animationNames(int documentId) {
        var document = document(documentId);
        return document == null ? List.of() : sortedAnimationNames(animationsObjectOrNull(document));
    }

    private static List<String> sortedAnimationNames(@Nullable JsonObject animations) {
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
        return activeDocumentId == null || selectedAnimationName == null
            ? null
            : animationObject(activeDocumentId, selectedAnimationName);
    }

    public @Nullable JsonObject animationObject(@Nullable String name) {
        if (activeDocumentId == null) {
            return null;
        }
        return animationObject(activeDocumentId, name);
    }

    public @Nullable JsonObject animationObject(int documentId, @Nullable String name) {
        var document = document(documentId);
        var animations = document == null ? null : animationsObjectOrNull(document);
        if (animations == null || name == null || !animations.has(name) || !animations.get(name).isJsonObject()) {
            return null;
        }
        return animations.getAsJsonObject(name);
    }

    public double selectedAnimationLengthSeconds() {
        var animationKeys = playbackAnimationKeys();
        if (animationKeys.isEmpty()) {
            return 1.0;
        }
        var length = 1.0;
        for (var animationKey : animationKeys) {
            var animation = animationObject(animationKey.documentId(), animationKey.animationName());
            if (animation == null) {
                continue;
            }
            var animationLength = readAnimationLength(animation);
            animationLength = Math.max(animationLength, maxKeyframeTimestamp(animationKey.documentId(), animationKey.animationName(), animation));
            length = Math.max(length, animationLength);
        }
        return Math.max(1.0, length);
    }

    public void updatePlaybackClock() {
        if (!playing) {
            return;
        }
        if (playbackAnimationKeys().isEmpty()) {
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
        var speedMultiplier = playbackSpeedPercent / DEFAULT_PLAYBACK_SPEED_PERCENT;
        if (speedMultiplier <= 0.0) {
            return;
        }
        var duration = selectedAnimationLengthSeconds();
        playheadSeconds += Math.min(elapsed, 0.25) * speedMultiplier;
        if (playheadSeconds > duration) {
            playheadSeconds = duration <= 0.0 ? 0.0 : playheadSeconds % duration;
        }
    }

    public void togglePlayback() {
        setPlaying(!playing);
    }

    public void setPlaying(boolean playing) {
        if (!playing || playbackAnimationKeys().isEmpty()) {
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

    public void resetSession() {
        syncActiveDocument();
        draft = null;
        externalSavePath = null;
        projectResourceId = null;
        lastSavedPath = null;
        selectedAnimationName = null;
        selectedAnimationNames.clear();
        selectedAnimationKeys.clear();
        documents.clear();
        nextDocumentId = 1;
        activeDocumentId = null;
        selectedBoneName = null;
        selectedChannel = TransformChannel.ROTATION;
        selectedTimestamp = null;
        playing = false;
        playheadSeconds = 0.0;
        playbackSpeedPercent = DEFAULT_PLAYBACK_SPEED_PERCENT;
        lastPlaybackNanos = 0L;
        dirty = false;
        statusMessage = null;
    }

    public JsonObject sessionSnapshotJson() {
        syncActiveDocument();
        var root = new JsonObject();
        var docs = new JsonArray();
        var documentIndexById = new HashMap<Integer, Integer>();
        for (var document : documents) {
            var path = restorablePath(document);
            if (path == null) {
                continue;
            }
            var index = docs.size();
            documentIndexById.put(document.id, index);
            var obj = new JsonObject();
            obj.addProperty("path", path.toString());
            obj.addProperty("active", isActiveDocument(document.id));
            docs.add(obj);
        }
        root.add("documents", docs);

        if (activeDocumentId != null && documentIndexById.containsKey(activeDocumentId)) {
            root.addProperty("activeDocumentIndex", documentIndexById.get(activeDocumentId));
        }
        if (selectedAnimationName != null) {
            root.addProperty("selectedAnimationName", selectedAnimationName);
        }

        var selectedAnimations = new JsonArray();
        for (var key : selectedAnimationKeys()) {
            var documentIndex = documentIndexById.get(key.documentId());
            if (documentIndex == null) {
                continue;
            }
            var obj = new JsonObject();
            obj.addProperty("documentIndex", documentIndex);
            obj.addProperty("name", key.animationName());
            selectedAnimations.add(obj);
        }
        root.add("selectedAnimations", selectedAnimations);

        if (selectedBoneName != null) {
            root.addProperty("selectedBoneName", selectedBoneName);
        }
        root.addProperty("selectedChannel", selectedChannel.jsonName());
        if (selectedTimestamp != null) {
            root.addProperty("selectedTimestamp", selectedTimestamp);
        }
        root.addProperty("playheadSeconds", playheadSeconds);
        root.addProperty("playbackSpeedPercent", playbackSpeedPercent);
        root.addProperty("playing", playing);
        return root;
    }

    public void restoreSessionJson(@Nullable JsonObject snapshot) {
        resetSession();
        if (snapshot == null) {
            return;
        }

        var restoredDocumentIds = new ArrayList<Integer>();
        var docs = snapshot.getAsJsonArray("documents");
        if (docs != null) {
            for (var el : docs) {
                if (!el.isJsonObject()) {
                    restoredDocumentIds.add(null);
                    continue;
                }
                var pathText = jsonString(el.getAsJsonObject(), "path");
                if (pathText == null || pathText.isBlank()) {
                    restoredDocumentIds.add(null);
                    continue;
                }
                try {
                    var path = Path.of(pathText);
                    if (openFromFile(path)) {
                        restoredDocumentIds.add(selectedDocumentId());
                    } else {
                        restoredDocumentIds.add(null);
                    }
                } catch (RuntimeException ignored) {
                    restoredDocumentIds.add(null);
                }
            }
        }

        selectedAnimationName = null;
        selectedAnimationNames.clear();
        selectedAnimationKeys.clear();

        var activeIndex = jsonInt(snapshot, "activeDocumentIndex", -1);
        if (activeIndex >= 0 && activeIndex < restoredDocumentIds.size()) {
            var documentId = restoredDocumentIds.get(activeIndex);
            if (documentId != null) {
                var document = document(documentId);
                if (document != null) {
                    activateDocument(document);
                }
            }
        }

        var selectedAnimations = snapshot.getAsJsonArray("selectedAnimations");
        if (selectedAnimations != null) {
            for (var el : selectedAnimations) {
                if (!el.isJsonObject()) {
                    continue;
                }
                var obj = el.getAsJsonObject();
                var documentIndex = jsonInt(obj, "documentIndex", -1);
                var animationName = jsonString(obj, "name");
                if (documentIndex < 0 || documentIndex >= restoredDocumentIds.size() || animationName == null) {
                    continue;
                }
                var documentId = restoredDocumentIds.get(documentIndex);
                if (documentId != null && animationObject(documentId, animationName) != null) {
                    selectedAnimationKeys.add(new AnimationKey(documentId, animationName));
                }
            }
        }

        var restoredActiveName = jsonString(snapshot, "selectedAnimationName");
        if (activeDocumentId != null && restoredActiveName != null && animationObject(activeDocumentId, restoredActiveName) != null) {
            selectedAnimationName = restoredActiveName;
            selectedAnimationKeys.add(new AnimationKey(activeDocumentId, restoredActiveName));
        } else {
            selectedAnimationName = firstSelectedAnimationName();
            if (selectedAnimationName == null && activeDocumentId != null) {
                selectedAnimationName = firstAnimationName(activeDocument());
            }
        }
        syncSelectedAnimationNames();

        selectedBoneName = jsonString(snapshot, "selectedBoneName");
        selectedChannel = TransformChannel.fromJsonName(jsonString(snapshot, "selectedChannel"));
        selectedTimestamp = snapshot.has("selectedTimestamp") ? jsonDouble(snapshot, "selectedTimestamp", 0.0) : null;
        playheadSeconds = jsonDouble(snapshot, "playheadSeconds", 0.0);
        setPlaybackSpeedPercent(jsonDouble(snapshot, "playbackSpeedPercent", DEFAULT_PLAYBACK_SPEED_PERCENT));
        playing = false;
        lastPlaybackNanos = 0L;
        setPlayheadSeconds(playheadSeconds);
        setPlaying(jsonBoolean(snapshot, "playing", false));
        statusMessage = restoredDocumentIds.isEmpty() ? null : "Restored animation workspace";
    }

    public @Nullable PreviewBoneTransform previewTransformFor(ModelerBone bone) {
        return previewTransformFor(bone, playheadSeconds);
    }

    public @Nullable PreviewBoneTransform previewTransformFor(ModelerBone bone, double timestamp) {
        if (documents.isEmpty()) {
            return null;
        }
        if (ModelerScene.get().itemSession != null) {
            return null;
        }

        Vec3 position = null;
        Vec3 rotation = null;
        Vec3 scale = null;
        for (var animationKey : playbackAnimationKeys()) {
            var sampledPosition = sampleChannel(animationKey.documentId(), animationKey.animationName(), bone.name, TransformChannel.POSITION, timestamp);
            if (sampledPosition != null) {
                position = toModelerSpace(TransformChannel.POSITION, sampledPosition);
            }
            var sampledRotation = sampleChannel(animationKey.documentId(), animationKey.animationName(), bone.name, TransformChannel.ROTATION, timestamp);
            if (sampledRotation != null) {
                var modelerRotation = toModelerSpace(TransformChannel.ROTATION, sampledRotation);
                rotation = rotation == null ? modelerRotation : rotation.add(modelerRotation);
            }
            var sampledScale = sampleChannel(animationKey.documentId(), animationKey.animationName(), bone.name, TransformChannel.SCALE, timestamp);
            if (sampledScale != null) {
                scale = sampledScale;
            }
        }
        if (position == null && rotation == null && scale == null) {
            return null;
        }
        return new PreviewBoneTransform(position, rotation, scale);
    }

    public void selectAnimation(@Nullable String name) {
        selectSingleAnimation(resolveAnimationName(name));
        selectedTimestamp = null;
        stopPlayback();
    }

    public void selectAnimation(int documentId, @Nullable String name) {
        var document = document(documentId);
        if (document == null) {
            return;
        }
        syncActiveDocument();
        activateDocument(document);
        selectSingleAnimation(resolveAnimationName(documentId, name));
        selectedTimestamp = null;
        stopPlayback();
    }

    public void toggleAnimationSelection(String name) {
        if (activeDocumentId == null) {
            return;
        }
        toggleAnimationSelection(activeDocumentId, name);
    }

    public void toggleAnimationSelection(int documentId, String name) {
        if (animationObject(documentId, name) == null) {
            return;
        }
        syncActiveDocument();
        var document = document(documentId);
        if (document != null) {
            activateDocument(document);
        }
        var key = new AnimationKey(documentId, name);
        if (selectedAnimationKeys.contains(key)) {
            selectedAnimationKeys.remove(key);
            if (documentId == activeDocumentId && name.equals(selectedAnimationName)) {
                selectedAnimationName = firstSelectedAnimationName();
            }
        } else {
            selectedAnimationKeys.add(key);
            selectedAnimationName = name;
        }
        syncSelectedAnimationNames();
        selectedTimestamp = null;
        stopPlayback();
    }

    public void selectAnimationRange(List<String> orderedNames, @Nullable String anchorName, String targetName) {
        if (activeDocumentId == null) {
            return;
        }
        var orderedKeys = new ArrayList<AnimationKey>();
        for (var name : orderedNames) {
            orderedKeys.add(new AnimationKey(activeDocumentId, name));
        }
        var anchorKey = anchorName == null ? null : new AnimationKey(activeDocumentId, anchorName);
        selectAnimationRange(orderedKeys, anchorKey, new AnimationKey(activeDocumentId, targetName));
    }

    public void selectAnimationRange(List<AnimationKey> orderedKeys, @Nullable AnimationKey anchorKey, AnimationKey targetKey) {
        if (animationObject(targetKey.documentId(), targetKey.animationName()) == null || orderedKeys.isEmpty()) {
            return;
        }
        var fallbackAnchor = selectedAnimationName == null || activeDocumentId == null
            ? null
            : new AnimationKey(activeDocumentId, selectedAnimationName);
        syncActiveDocument();
        var targetDocument = document(targetKey.documentId());
        if (targetDocument != null) {
            activateDocument(targetDocument);
        }
        var anchorIndex = orderedKeys.indexOf(anchorKey);
        if (anchorIndex < 0) {
            anchorIndex = orderedKeys.indexOf(fallbackAnchor);
        }
        if (anchorIndex < 0) {
            anchorIndex = orderedKeys.indexOf(targetKey);
        }
        var targetIndex = orderedKeys.indexOf(targetKey);
        if (targetIndex < 0) {
            return;
        }
        var from = Math.min(anchorIndex, targetIndex);
        var to = Math.max(anchorIndex, targetIndex);
        selectedAnimationKeys.clear();
        for (var i = from; i <= to; i++) {
            var key = orderedKeys.get(i);
            if (animationObject(key.documentId(), key.animationName()) != null) {
                selectedAnimationKeys.add(key);
            }
        }
        selectedAnimationName = targetKey.animationName();
        syncSelectedAnimationNames();
        selectedTimestamp = null;
        stopPlayback();
    }

    public String createAnimation(@Nullable String requestedName) {
        if (activeDocument() == null) {
            newDraft();
        }
        var animations = ensureAnimationsObject();
        var base = requestedName == null || requestedName.isBlank() ? "animation.new" : requestedName.trim();
        var name = uniqueName(base);
        var obj = new JsonObject();
        obj.addProperty("animation_length", 1.0);
        obj.add("bones", new JsonObject());
        animations.add(name, obj);
        selectSingleAnimation(name);
        selectedTimestamp = null;
        stopPlayback();
        markDirty("Created " + name);
        return name;
    }

    public boolean renameAnimation(String oldName, String newName) {
        if (activeDocumentId == null) {
            return false;
        }
        return renameAnimation(activeDocumentId, oldName, newName);
    }

    public boolean renameAnimation(int documentId, String oldName, String newName) {
        var document = document(documentId);
        var animations = document == null ? null : animationsObjectOrNull(document);
        if (animations == null || oldName == null || newName == null || newName.isBlank() || !animations.has(oldName)) {
            return false;
        }
        syncActiveDocument();
        activateDocument(document);
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
        document.draft.add("animations", rebuilt);
        selectedAnimationName = clean;
        var oldKey = new AnimationKey(documentId, oldName);
        if (selectedAnimationKeys.remove(oldKey)) {
            selectedAnimationKeys.add(new AnimationKey(documentId, clean));
        }
        selectedAnimationKeys.add(new AnimationKey(documentId, clean));
        syncSelectedAnimationNames();
        markDirty("Renamed " + oldName + " to " + clean);
        return true;
    }

    public @Nullable String duplicateAnimation(@Nullable String sourceName) {
        if (activeDocumentId == null) {
            return null;
        }
        return duplicateAnimation(activeDocumentId, sourceName);
    }

    public @Nullable String duplicateAnimation(int documentId, @Nullable String sourceName) {
        var source = animationObject(documentId, sourceName);
        if (source == null) {
            return null;
        }
        var document = document(documentId);
        syncActiveDocument();
        activateDocument(document);
        var animations = ensureAnimationsObject(document);
        var name = uniqueName(document, sourceName + "_copy");
        animations.add(name, source.deepCopy());
        selectSingleAnimation(name);
        selectedTimestamp = null;
        stopPlayback();
        markDirty("Duplicated " + sourceName);
        return name;
    }

    public boolean deleteAnimation(@Nullable String name) {
        if (activeDocumentId == null) {
            return false;
        }
        return deleteAnimation(activeDocumentId, name);
    }

    public boolean deleteAnimation(int documentId, @Nullable String name) {
        var document = document(documentId);
        var animations = document == null ? null : animationsObjectOrNull(document);
        if (animations == null || name == null || !animations.has(name)) {
            return false;
        }
        syncActiveDocument();
        activateDocument(document);
        animations.remove(name);
        var removedSelected = selectedAnimationKeys.remove(new AnimationKey(documentId, name));
        if (name.equals(selectedAnimationName)) {
            selectedAnimationName = firstSelectedAnimationName();
            if (selectedAnimationName == null) {
                selectSingleAnimation(firstAnimationName(document));
            }
            selectedTimestamp = null;
            stopPlayback();
        } else if (removedSelected) {
            stopPlayback();
        }
        syncSelectedAnimationNames();
        markDirty("Deleted " + name);
        return true;
    }

    public void selectBone(@Nullable String boneName) {
        var nextBoneName = boneName == null || boneName.isBlank() ? null : boneName;
        if (Objects.equals(selectedBoneName, nextBoneName)) {
            return;
        }
        selectedBoneName = nextBoneName;
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
        if (activeDocumentId == null) {
            return;
        }
        selectKeyframe(activeDocumentId, animationName, boneName, channel, timestamp);
    }

    public void selectKeyframe(int documentId, String animationName, String boneName, TransformChannel channel, double timestamp) {
        var document = document(documentId);
        if (document == null) {
            return;
        }
        syncActiveDocument();
        activateDocument(document);
        selectedAnimationName = animationName;
        if (animationObject(documentId, animationName) != null) {
            selectedAnimationKeys.add(new AnimationKey(documentId, animationName));
        }
        syncSelectedAnimationNames();
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
        if (activeDocumentId == null) {
            return List.of();
        }
        return keyframes(activeDocumentId, animationName, boneName, channel);
    }

    public List<KeyframeRef> keyframes(int documentId, String animationName, String boneName, TransformChannel channel) {
        var boneObj = boneAnimationObject(documentId, animationName, boneName);
        if (boneObj == null || !boneObj.has(channel.jsonName())) {
            return List.of();
        }
        var refs = readKeyframes(documentId, animationName, boneName, channel, boneObj.get(channel.jsonName()));
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
        if (activeDocument() == null) {
            newDraft();
        }
        var animation = selectedAnimationName != null ? selectedAnimationName : createAnimation(null);
        var documentId = activeDocumentId == null ? 0 : activeDocumentId;
        var bone = selectedBoneName;
        if (bone == null || bone.isBlank()) {
            bone = "bone";
            selectedBoneName = bone;
        }
        var timestamp = selectedTimestamp != null ? selectedTimestamp : 0.0;
        var frame = ensureKeyframe(documentId, animation, bone, selectedChannel, timestamp);
        selectedTimestamp = timestamp;
        markDirty("Edited keyframe");
        return new KeyframeRef(documentId, animation, bone, selectedChannel, timestamp, frame);
    }

    public boolean deleteSelectedKeyframe() {
        if (selectedAnimationName == null || selectedBoneName == null || selectedTimestamp == null) {
            return false;
        }
        var deleted = activeDocumentId != null
            && deleteKeyframe(activeDocumentId, selectedAnimationName, selectedBoneName, selectedChannel, selectedTimestamp);
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
        deleteKeyframe(current.documentId(), current.animationName(), current.boneName(), current.channel(), current.timestamp());
        var normalizedTimestamp = Math.max(0.0, newTimestamp);
        putKeyframe(current.documentId(), current.animationName(), newBoneName, newChannel, normalizedTimestamp, copy);
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
        var normalized = normalizeEasingName(easing);
        if (normalized.isEmpty()) {
            frame.keyframe().remove("easing");
        } else {
            frame.keyframe().addProperty("easing", normalized);
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
        if (activeDocumentId == null) {
            return null;
        }
        return boneAnimationObject(activeDocumentId, animationName, boneName);
    }

    private @Nullable JsonObject boneAnimationObject(int documentId, String animationName, String boneName) {
        var animation = animationObject(documentId, animationName);
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
        if (activeDocumentId == null) {
            newDraft();
        }
        return ensureBoneAnimationObject(activeDocumentId == null ? 0 : activeDocumentId, animationName, boneName);
    }

    private JsonObject ensureBoneAnimationObject(int documentId, String animationName, String boneName) {
        var document = document(documentId);
        if (document == null) {
            newDraft();
            documentId = activeDocumentId == null ? 0 : activeDocumentId;
            document = activeDocument();
        }
        var animation = animationObject(documentId, animationName);
        if (animation == null) {
            animation = new JsonObject();
            animation.add("bones", new JsonObject());
            ensureAnimationsObject(document).add(animationName, animation);
            selectedAnimationName = animationName;
            selectedAnimationKeys.add(new AnimationKey(documentId, animationName));
            syncSelectedAnimationNames();
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
        if (activeDocumentId == null) {
            newDraft();
        }
        return ensureKeyframe(activeDocumentId == null ? 0 : activeDocumentId, animationName, boneName, channel, timestamp);
    }

    private JsonObject ensureKeyframe(int documentId, String animationName, String boneName, TransformChannel channel, double timestamp) {
        var bone = ensureBoneAnimationObject(documentId, animationName, boneName);
        ensureAnimationLength(documentId, animationName, timestamp);
        var channelObj = normalizeChannelObject(documentId, animationName, boneName, channel, bone.get(channel.jsonName()));
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
        int documentId,
        String animationName,
        String boneName,
        TransformChannel channel,
        double timestamp,
        JsonObject frame
    ) {
        var bone = ensureBoneAnimationObject(documentId, animationName, boneName);
        ensureAnimationLength(documentId, animationName, timestamp);
        var channelObj = normalizeChannelObject(documentId, animationName, boneName, channel, bone.get(channel.jsonName()));
        bone.add(channel.jsonName(), channelObj);
        channelObj.add(formatTimestamp(timestamp), frame);
    }

    private void ensureAnimationLength(String animationName, double timestamp) {
        if (activeDocumentId == null) {
            return;
        }
        ensureAnimationLength(activeDocumentId, animationName, timestamp);
    }

    private void ensureAnimationLength(int documentId, String animationName, double timestamp) {
        var animation = animationObject(documentId, animationName);
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

    private static double maxKeyframeTimestamp(int documentId, @Nullable String animationName, JsonObject animation) {
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
                for (var ref : readKeyframes(documentId, animationName, boneEntry.getKey(), channel, bone.get(channel.jsonName()))) {
                    max = Math.max(max, ref.timestamp());
                }
            }
        }
        return max;
    }

    private boolean deleteKeyframe(String animationName, String boneName, TransformChannel channel, double timestamp) {
        if (activeDocumentId == null) {
            return false;
        }
        return deleteKeyframe(activeDocumentId, animationName, boneName, channel, timestamp);
    }

    private boolean deleteKeyframe(int documentId, String animationName, String boneName, TransformChannel channel, double timestamp) {
        var bone = boneAnimationObject(documentId, animationName, boneName);
        if (bone == null || !bone.has(channel.jsonName())) {
            return false;
        }
        var channelObj = normalizeChannelObject(documentId, animationName, boneName, channel, bone.get(channel.jsonName()));
        bone.add(channel.jsonName(), channelObj);
        return channelObj.remove(formatTimestamp(timestamp)) != null;
    }

    private @Nullable Vec3 sampleChannel(String animationName, String boneName, TransformChannel channel, double timestamp) {
        if (activeDocumentId == null) {
            return null;
        }
        return sampleChannel(activeDocumentId, animationName, boneName, channel, timestamp);
    }

    private @Nullable Vec3 sampleChannel(
        int documentId,
        String animationName,
        String boneName,
        TransformChannel channel,
        double timestamp
    ) {
        if (animationObject(documentId, animationName) == null) {
            return null;
        }
        var frames = keyframes(documentId, animationName, boneName, channel);
        if (frames.isEmpty()) {
            return null;
        }
        if (frames.size() == 1 || timestamp <= frames.getFirst().timestamp()) {
            return vectorFromFrame(frames.getFirst());
        }

        var previous = frames.getFirst();
        for (var i = 1; i < frames.size(); i++) {
            var next = frames.get(i);
            if (timestamp <= next.timestamp()) {
                var span = next.timestamp() - previous.timestamp();
                if (span <= 1.0e-9) {
                    return vectorFromFrame(next);
                }
                var t = Math.max(0.0, Math.min(1.0, (timestamp - previous.timestamp()) / span));
                return lerp(vectorFromFrame(previous), vectorFromFrame(next), easeRatio(next.keyframe(), t));
            }
            previous = next;
        }
        return vectorFromFrame(frames.getLast());
    }

    private static Vec3 vectorFromFrame(KeyframeRef frame) {
        var obj = frame.keyframe();
        if (!obj.has("vector") || !obj.get("vector").isJsonArray()) {
            return Vec3.ZERO;
        }
        var vector = obj.getAsJsonArray("vector");
        return new Vec3(
            numericVectorValue(vector, 0),
            numericVectorValue(vector, 1),
            numericVectorValue(vector, 2)
        );
    }

    private static double numericVectorValue(JsonArray vector, int index) {
        if (index >= vector.size()) {
            return 0.0;
        }
        var element = vector.get(index);
        try {
            if (element != null && element.isJsonPrimitive()) {
                var primitive = element.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    return primitive.getAsDouble();
                }
                if (primitive.isString() && NumberUtils.isCreatable(primitive.getAsString())) {
                    return Double.parseDouble(primitive.getAsString());
                }
            }
        } catch (RuntimeException ignored) {
            return 0.0;
        }
        return 0.0;
    }

    private static Vec3 lerp(Vec3 from, Vec3 to, double t) {
        return new Vec3(
            from.x + (to.x - from.x) * t,
            from.y + (to.y - from.y) * t,
            from.z + (to.z - from.z) * t
        );
    }

    private static double easeRatio(JsonObject keyframe, double linear) {
        if (!keyframe.has("easing")) {
            return linear;
        }
        try {
            var easing = elementToText(keyframe.get("easing")).toLowerCase(Locale.ROOT);
            var firstArg = firstEasingArg(keyframe.get("easingArgs"));
            var eased = AzEasingTypeLoader.fromString(easing).buildTransformer(firstArg).apply(linear);
            return Math.max(0.0, Math.min(1.0, eased));
        } catch (RuntimeException ignored) {
            return linear;
        }
    }

    private static @Nullable Double firstEasingArg(@Nullable JsonElement element) {
        if (element == null || !element.isJsonArray() || element.getAsJsonArray().isEmpty()) {
            return null;
        }
        var first = element.getAsJsonArray().get(0);
        try {
            if (first.isJsonPrimitive()) {
                var primitive = first.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    return primitive.getAsDouble();
                }
                if (primitive.isString() && NumberUtils.isCreatable(primitive.getAsString())) {
                    return Double.parseDouble(primitive.getAsString());
                }
            }
        } catch (RuntimeException ignored) {
            return null;
        }
        return null;
    }

    private static Vec3 toModelerSpace(TransformChannel channel, Vec3 source) {
        return switch (channel) {
            case POSITION -> new Vec3(-source.x, source.y, source.z);
            case ROTATION -> new Vec3(-source.x, -source.y, source.z);
            case SCALE -> source;
        };
    }

    private JsonObject normalizeChannelObject(
        int documentId,
        String animationName,
        String boneName,
        TransformChannel channel,
        @Nullable JsonElement existing
    ) {
        var out = new JsonObject();
        if (existing == null || existing.isJsonNull()) {
            return out;
        }
        for (var ref : readKeyframes(documentId, animationName, boneName, channel, existing)) {
            out.add(formatTimestamp(ref.timestamp()), ref.keyframe().deepCopy());
        }
        return out;
    }

    private static List<KeyframeRef> readKeyframes(
        int documentId,
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
            out.add(new KeyframeRef(documentId, animationName, boneName, channel, 0.0, frame(vectorFromPrimitive(element.getAsJsonPrimitive()))));
            return out;
        }
        if (element.isJsonArray()) {
            out.add(new KeyframeRef(documentId, animationName, boneName, channel, 0.0, frame(copyVector(element.getAsJsonArray()))));
            return out;
        }
        if (!element.isJsonObject()) {
            return out;
        }
        var obj = element.getAsJsonObject();
        if (obj.has("vector")) {
            out.add(new KeyframeRef(documentId, animationName, boneName, channel, 0.0, normalizeFrameObject(obj)));
            return out;
        }
        for (var entry : obj.entrySet()) {
            if (!NumberUtils.isCreatable(entry.getKey())) {
                continue;
            }
            var timestamp = Double.parseDouble(entry.getKey());
            var normalized = normalizeTimestampEntry(entry.getValue());
            if (normalized != null) {
                out.add(new KeyframeRef(documentId, animationName, boneName, channel, timestamp, normalized));
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

    private @Nullable Path restorablePath(AnimationDocument document) {
        if (document.externalSavePath != null) {
            return document.externalSavePath;
        }
        if (document.lastSavedPath != null) {
            return document.lastSavedPath;
        }
        if (document.projectResourceId == null) {
            return null;
        }
        var project = ProjectSession.activeProjectName();
        if (project.isEmpty()) {
            return null;
        }
        return EngineProjectIO
            .resourcepackRoot(project)
            .resolve("assets")
            .resolve(document.projectResourceId.getNamespace())
            .resolve(document.projectResourceId.getPath());
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

    private static boolean jsonBoolean(JsonObject obj, String field, boolean fallback) {
        if (obj == null || !obj.has(field) || !obj.get(field).isJsonPrimitive()) {
            return fallback;
        }
        try {
            return obj.get(field).getAsBoolean();
        } catch (RuntimeException ignored) {
            return fallback;
        }
    }

    private @Nullable AnimationDocument activeDocument() {
        if (activeDocumentId == null) {
            return null;
        }
        return document(activeDocumentId);
    }

    private @Nullable AnimationDocument document(int documentId) {
        for (var document : documents) {
            if (document.id == documentId) {
                return document;
            }
        }
        return null;
    }

    private boolean isActiveDocument(int documentId) {
        return activeDocumentId != null && activeDocumentId == documentId;
    }

    private void syncActiveDocument() {
        var document = activeDocument();
        if (document == null) {
            return;
        }
        document.draft = draft;
        document.externalSavePath = externalSavePath;
        document.projectResourceId = projectResourceId;
        document.lastSavedPath = lastSavedPath;
        document.dirty = dirty;
    }

    private void activateDocument(AnimationDocument document) {
        if (document == null) {
            return;
        }
        activeDocumentId = document.id;
        draft = document.draft;
        externalSavePath = document.externalSavePath;
        projectResourceId = document.projectResourceId;
        lastSavedPath = document.lastSavedPath;
        dirty = document.dirty;
    }

    private void syncSelectedAnimationNames() {
        selectedAnimationNames.clear();
        if (activeDocumentId == null) {
            return;
        }
        for (var key : selectedAnimationKeys) {
            if (key.documentId() == activeDocumentId && animationObject(key.documentId(), key.animationName()) != null) {
                selectedAnimationNames.add(key.animationName());
            }
        }
        if (selectedAnimationName != null && !selectedAnimationNames.contains(selectedAnimationName)) {
            if (animationObject(activeDocumentId, selectedAnimationName) != null) {
                selectedAnimationNames.add(selectedAnimationName);
            }
        }
    }

    private @Nullable String resolveAnimationName(@Nullable String name) {
        if (activeDocumentId == null) {
            return null;
        }
        return resolveAnimationName(activeDocumentId, name);
    }

    private @Nullable String resolveAnimationName(int documentId, @Nullable String name) {
        var document = document(documentId);
        return name != null && animationObject(documentId, name) != null ? name : firstAnimationName(document);
    }

    private void selectSingleAnimation(@Nullable String name) {
        selectedAnimationNames.clear();
        selectedAnimationKeys.clear();
        selectedAnimationName = name;
        if (name != null && activeDocumentId != null && animationObject(activeDocumentId, name) != null) {
            selectedAnimationNames.add(name);
            selectedAnimationKeys.add(new AnimationKey(activeDocumentId, name));
        }
    }

    private List<AnimationKey> playbackAnimationKeys() {
        var selected = selectedAnimationKeys();
        if (!selected.isEmpty()) {
            return selected;
        }
        return selectedAnimationName != null && activeDocumentId != null && animationObject(activeDocumentId, selectedAnimationName) != null
            ? List.of(new AnimationKey(activeDocumentId, selectedAnimationName))
            : List.of();
    }

    private @Nullable String firstSelectedAnimationName() {
        if (activeDocumentId == null) {
            return null;
        }
        for (var key : selectedAnimationKeys) {
            if (key.documentId() == activeDocumentId && animationObject(key.documentId(), key.animationName()) != null) {
                return key.animationName();
            }
        }
        return null;
    }

    private @Nullable JsonObject animationsObjectOrNull() {
        var document = activeDocument();
        return document == null ? null : animationsObjectOrNull(document);
    }

    private static @Nullable JsonObject animationsObjectOrNull(@Nullable AnimationDocument document) {
        if (document == null || document.draft == null || !document.draft.has("animations") || !document.draft.get("animations").isJsonObject()) {
            return null;
        }
        return document.draft.getAsJsonObject("animations");
    }

    private JsonObject ensureAnimationsObject() {
        if (activeDocument() == null) {
            newDraft();
        }
        var document = activeDocument();
        ensureAnimationsObject(document);
        return document.draft.getAsJsonObject("animations");
    }

    private JsonObject ensureAnimationsObject(@Nullable AnimationDocument document) {
        if (document == null) {
            newDraft();
            document = activeDocument();
        }
        ensureAnimationsObject(document.draft);
        return document.draft.getAsJsonObject("animations");
    }

    private static void ensureAnimationsObject(JsonObject root) {
        if (!root.has("animations") || !root.get("animations").isJsonObject()) {
            root.add("animations", new JsonObject());
        }
    }

    private @Nullable String firstAnimationName() {
        return firstAnimationName(activeDocument());
    }

    private @Nullable String firstAnimationName(@Nullable AnimationDocument document) {
        var animations = animationsObjectOrNull(document);
        if (animations == null) {
            return null;
        }
        for (var entry : animations.entrySet()) {
            return entry.getKey();
        }
        return null;
    }

    private String uniqueName(String base) {
        return uniqueName(activeDocument(), base);
    }

    private String uniqueName(@Nullable AnimationDocument document, String base) {
        var animations = ensureAnimationsObject(document);
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
        var document = activeDocument();
        if (document != null) {
            document.dirty = true;
        }
        statusMessage = message;
    }

    private boolean writeFile(AnimationDocument document, Path path) {
        if (document == null || document.draft == null) {
            return false;
        }
        try {
            var parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, GSON.toJson(document.draft));
            document.lastSavedPath = path;
            document.dirty = false;
            if (isActiveDocument(document.id)) {
                lastSavedPath = path;
                dirty = false;
            }
            statusMessage = "Saved " + path.getFileName();
            return true;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("AnimationEditorState: failed to save {}: {}", path, e.getMessage());
            statusMessage = "Failed to save " + path.getFileName();
            return false;
        }
    }

    private boolean writeProject(AnimationDocument document, ResourceLocation resourceId) {
        if (document == null || document.draft == null) {
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
            Files.writeString(path, GSON.toJson(document.draft));
            document.lastSavedPath = path;
            document.dirty = false;
            if (isActiveDocument(document.id)) {
                lastSavedPath = path;
                dirty = false;
            }
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

    public static List<String> easingNames() {
        AzEasingTypes.LINEAR.name();
        var out = new ArrayList<String>();
        for (var name : AzEasingTypeRegistry.getNames()) {
            if (name != null && !name.isBlank() && !out.contains(name)) {
                out.add(name);
            }
        }
        out.sort(String.CASE_INSENSITIVE_ORDER);
        return out;
    }

    public static String normalizeEasingName(@Nullable String text) {
        return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
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
