package com.blib.engine.input;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import com.blib.api.BLibAPI;

/**
 * Filesystem I/O for {@link KeybindingProfile}s. Mirrors {@code LayoutStorage}'s style: one file per profile under
 * {@code <gameDir>/blib/engine/profiles/<id>.json}, hand-rolled Gson adapters for the sealed {@link Input} hierarchy.
 * <p>
 * The active-profile pointer lives in a sibling file {@code <gameDir>/blib/engine/keybindings.json} with shape
 * {@code {"version": 1, "activeProfileId": "..."}}. It's small and isolated from the profile files so the active
 * selection survives even if a profile file is corrupt (and vice versa).
 */
@ApiStatus.Internal
public final class KeybindingProfileStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeybindingProfileStorage.class);

    private static final String PROFILES_SUBDIR = "blib/engine/profiles";

    private static final String ACTIVE_POINTER_PATH = "blib/engine/keybindings.json";

    private static final int POINTER_VERSION = 1;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private KeybindingProfileStorage() {}

    public static Path profilesRoot() {
        return BLibAPI.getGameDirectory().resolve(PROFILES_SUBDIR);
    }

    public static Path profileFile(String id) {
        return profilesRoot().resolve(id + ".json");
    }

    public static Path activePointerFile() {
        return BLibAPI.getGameDirectory().resolve(ACTIVE_POINTER_PATH);
    }

    public static void ensureRootExists() throws IOException {
        Files.createDirectories(profilesRoot());
    }

    public static boolean exists(String id) {
        return Files.isRegularFile(profileFile(id));
    }

    public static Optional<KeybindingProfile> read(String id) {
        var path = profileFile(id);
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            var text = Files.readString(path);
            var parsed = JsonParser.parseString(text);
            if (!parsed.isJsonObject()) {
                LOGGER.warn("[BLib] KeybindingProfileStorage.read: {} is not a JSON object; skipping", path);
                return Optional.empty();
            }
            return Optional.of(fromJson(parsed.getAsJsonObject(), id));
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("[BLib] KeybindingProfileStorage.read: failed to parse {}; skipping", path, e);
            return Optional.empty();
        }
    }

    public static void write(KeybindingProfile profile) throws IOException {
        KeybindingProfile.validateId(profile.id());
        KeybindingProfile.validateDisplayName(profile.displayName());
        ensureRootExists();
        var path = profileFile(profile.id());
        Files.writeString(path, GSON.toJson(toJson(profile)));
    }

    public static List<KeybindingProfile> list() {
        var root = profilesRoot();
        if (!Files.isDirectory(root)) {
            return List.of();
        }
        var out = new ArrayList<KeybindingProfile>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, "*.json")) {
            for (var entry : stream) {
                if (!Files.isRegularFile(entry)) {
                    continue;
                }
                var fileName = entry.getFileName().toString();
                var id = fileName.substring(0, fileName.length() - ".json".length());
                read(id).ifPresent(out::add);
            }
        } catch (IOException e) {
            LOGGER.warn("[BLib] KeybindingProfileStorage.list: failed to scan {}", root, e);
            return List.of();
        }
        out.sort(Comparator.comparing(KeybindingProfile::displayName, String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    public static boolean delete(String id) throws IOException {
        return Files.deleteIfExists(profileFile(id));
    }

    /** Writes an empty "default" profile if it doesn't exist. Idempotent. Caller should invoke on startup. */
    public static void seedDefaultIfMissing() throws IOException {
        ensureRootExists();
        if (!exists(KeybindingProfile.DEFAULT_PROFILE_ID)) {
            write(KeybindingProfile.defaultProfile());
            LOGGER.info("[BLib] KeybindingProfileStorage: wrote default profile");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Active-pointer file
    // ---------------------------------------------------------------------------------------------

    public static Optional<String> readActiveProfileId() {
        var path = activePointerFile();
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            var text = Files.readString(path);
            var parsed = JsonParser.parseString(text);
            if (!parsed.isJsonObject()) {
                return Optional.empty();
            }
            var obj = parsed.getAsJsonObject();
            if (!obj.has("activeProfileId") || obj.get("activeProfileId").isJsonNull()) {
                return Optional.empty();
            }
            return Optional.of(obj.get("activeProfileId").getAsString());
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("[BLib] KeybindingProfileStorage.readActiveProfileId: failed to read {}", path, e);
            return Optional.empty();
        }
    }

    public static void writeActiveProfileId(String id) throws IOException {
        KeybindingProfile.validateId(id);
        Files.createDirectories(activePointerFile().getParent());
        var obj = new JsonObject();
        obj.addProperty("version", POINTER_VERSION);
        obj.addProperty("activeProfileId", id);
        Files.writeString(activePointerFile(), GSON.toJson(obj));
    }

    // ---------------------------------------------------------------------------------------------
    // JSON conversion
    // ---------------------------------------------------------------------------------------------

    private static JsonObject toJson(KeybindingProfile profile) {
        var obj = new JsonObject();
        obj.addProperty("version", profile.version());
        obj.addProperty("id", profile.id());
        obj.addProperty("displayName", profile.displayName());
        var overrides = new JsonObject();
        var sorted = new ArrayList<>(profile.overrides().keySet());
        sorted.sort(Comparator.naturalOrder());
        for (var id : sorted) {
            overrides.add(id, inputToJson(profile.overrides().get(id)));
        }
        obj.add("overrides", overrides);
        return obj;
    }

    private static KeybindingProfile fromJson(JsonObject obj, String fallbackId) {
        var version = obj.has("version") ? obj.get("version").getAsInt() : KeybindingProfile.CURRENT_VERSION;
        if (version != KeybindingProfile.CURRENT_VERSION) {
            throw new IllegalArgumentException(
                "Unsupported profile version " + version + " (expected " + KeybindingProfile.CURRENT_VERSION + ")"
            );
        }
        var rawId = obj.has("id") ? obj.get("id").getAsString() : fallbackId;
        String resolvedId;
        try {
            KeybindingProfile.validateId(rawId);
            resolvedId = rawId;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("[BLib] KeybindingProfileStorage: invalid inner id '{}' in {}.json; falling back to filename", rawId, fallbackId);
            KeybindingProfile.validateId(fallbackId);
            resolvedId = fallbackId;
        }
        if (!resolvedId.equals(fallbackId)) {
            LOGGER.warn(
                "[BLib] KeybindingProfileStorage: inner id '{}' differs from filename '{}'; preferring filename",
                resolvedId,
                fallbackId
            );
            resolvedId = fallbackId;
        }
        var displayName = obj.has("displayName") ? obj.get("displayName").getAsString() : resolvedId;
        var overrides = new LinkedHashMap<String, Input>();
        if (obj.has("overrides") && obj.get("overrides").isJsonObject()) {
            var ovObj = obj.getAsJsonObject("overrides");
            for (var entry : ovObj.entrySet()) {
                try {
                    overrides.put(entry.getKey(), inputFromJson(entry.getValue()));
                } catch (RuntimeException e) {
                    LOGGER.warn(
                        "[BLib] KeybindingProfileStorage: dropping invalid override '{}' in profile '{}'",
                        entry.getKey(),
                        resolvedId,
                        e
                    );
                }
            }
        }
        return new KeybindingProfile(version, resolvedId, displayName, overrides);
    }

    private static JsonObject inputToJson(Input input) {
        var obj = new JsonObject();
        switch (input) {
            case Input.Key k -> {
                obj.addProperty("kind", "key");
                obj.addProperty("keyCode", k.keyCode());
                obj.addProperty("modifierMask", k.modifierMask());
            }
            case Input.MouseButton mb -> {
                obj.addProperty("kind", "mouse_button");
                obj.addProperty("button", mb.button());
                obj.addProperty("modifierMask", mb.modifierMask());
            }
            case Input.MouseDrag md -> {
                obj.addProperty("kind", "mouse_drag");
                obj.addProperty("button", md.button());
                obj.addProperty("modifierMask", md.modifierMask());
            }
            case Input.Scroll s -> {
                obj.addProperty("kind", "scroll");
                obj.addProperty("modifierMask", s.modifierMask());
            }
            case Input.Modifier m -> {
                obj.addProperty("kind", "modifier");
                obj.addProperty("modifierMask", m.modifierMask());
            }
        }
        return obj;
    }

    private static Input inputFromJson(JsonElement elem) {
        if (elem == null || !elem.isJsonObject()) {
            throw new IllegalArgumentException("input must be a JSON object");
        }
        var obj = elem.getAsJsonObject();
        var kind = obj.has("kind") ? obj.get("kind").getAsString() : "";
        return switch (kind) {
            case "key" -> new Input.Key(obj.get("keyCode").getAsInt(), obj.get("modifierMask").getAsInt());
            case "mouse_button" -> new Input.MouseButton(obj.get("button").getAsInt(), obj.get("modifierMask").getAsInt());
            case "mouse_drag" -> new Input.MouseDrag(obj.get("button").getAsInt(), obj.get("modifierMask").getAsInt());
            case "scroll" -> new Input.Scroll(obj.get("modifierMask").getAsInt());
            case "modifier" -> new Input.Modifier(obj.get("modifierMask").getAsInt());
            default -> throw new IllegalArgumentException("Unknown input kind: '" + kind + "'");
        };
    }
}
