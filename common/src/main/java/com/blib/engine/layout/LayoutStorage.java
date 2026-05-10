package com.blib.engine.layout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import java.util.List;
import java.util.Optional;

import com.blib.api.BLibAPI;

/**
 * Filesystem I/O for persisted engine layouts. Mirrors {@code EngineProjectIO}'s style: a single seam for all on-disk
 * layout operations, JSON written via Gson with hand-rolled adapters for the sealed {@link BodyNode} and
 * {@link SizingDoc} hierarchies (Gson can't dispatch sealed types natively, and the project doesn't depend on
 * gson-extras).
 * <p>
 * Layout: {@code <gameDir>/blib/engine/layouts/<id>.json}. Each file is fully self-contained — there is no master index
 * — so external tools can drop or remove individual layouts without coordinating with the engine. Corrupt files are
 * skipped from {@link #list} with a WARN log so one broken file doesn't hide its siblings.
 * <p>
 * {@link #seedTemplatesIfMissing} writes each {@link LayoutTemplate} to disk only when its target file doesn't already
 * exist, preserving any user customizations to template-derived layouts.
 */
@ApiStatus.Internal
public final class LayoutStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutStorage.class);

    private static final String LAYOUTS_SUBDIR = "blib/engine/layouts";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private LayoutStorage() {}

    public static Path layoutsRoot() {
        return BLibAPI.getGameDirectory().resolve(LAYOUTS_SUBDIR);
    }

    public static Path layoutFile(String id) {
        return layoutsRoot().resolve(id + ".json");
    }

    public static void ensureRootExists() throws IOException {
        Files.createDirectories(layoutsRoot());
    }

    public static boolean exists(String id) {
        return Files.isRegularFile(layoutFile(id));
    }

    /**
     * Read the layout at {@code <id>.json}. Returns empty on missing file, parse failure, or schema-version mismatch.
     * The {@code id} field inside the JSON is preferred over the filename if they disagree, but if the inner id fails
     * validation we fall back to the filename and log a WARN.
     */
    public static Optional<LayoutDoc> read(String id) {
        var path = layoutFile(id);
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            var text = Files.readString(path);
            var parsed = JsonParser.parseString(text);
            if (!parsed.isJsonObject()) {
                LOGGER.warn("[BLib] LayoutStorage.read: {} is not a JSON object; skipping", path);
                return Optional.empty();
            }
            var doc = fromJson(parsed.getAsJsonObject(), id);
            return Optional.of(doc);
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("[BLib] LayoutStorage.read: failed to parse {}; skipping", path, e);
            return Optional.empty();
        }
    }

    /**
     * Write {@code doc} to {@code <doc.id()>.json}, refreshing {@code modifiedAt}. Caller-side validation should have
     * already run; this throws unchecked on invalid id / displayName as a safety net.
     */
    public static void write(LayoutDoc doc) throws IOException {
        LayoutDoc.validateId(doc.id());
        LayoutDoc.validateDisplayName(doc.displayName());
        ensureRootExists();
        var path = layoutFile(doc.id());
        var withTouched = doc.withTouchedModifiedAt();
        Files.writeString(path, GSON.toJson(toJson(withTouched)));
    }

    /**
     * Scan the layouts dir and return every parseable layout, sorted by {@link LayoutDoc#displayName}. Empty if the
     * directory is missing (e.g. fresh install before {@link #seedTemplatesIfMissing}).
     */
    public static List<LayoutDoc> list() {
        var root = layoutsRoot();
        if (!Files.isDirectory(root)) {
            return List.of();
        }
        var out = new ArrayList<LayoutDoc>();
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
            LOGGER.warn("[BLib] LayoutStorage.list: failed to scan {}", root, e);
            return List.of();
        }
        out.sort(Comparator.comparing(LayoutDoc::displayName, String.CASE_INSENSITIVE_ORDER));
        return out;
    }

    public static boolean delete(String id) throws IOException {
        var path = layoutFile(id);
        return Files.deleteIfExists(path);
    }

    /**
     * Write each {@link LayoutTemplate}'s {@link LayoutTemplate#toDoc} to disk only if its target file doesn't exist.
     * Called by {@link LayoutCatalog#initialize} on workspace open. Idempotent: subsequent calls find the files already
     * present and skip them, preserving any user customizations to template-derived layouts.
     */
    public static void seedTemplatesIfMissing() throws IOException {
        ensureRootExists();
        for (var template : LayoutTemplate.all()) {
            if (!exists(template.id())) {
                write(template.toDoc());
                LOGGER.info("[BLib] LayoutStorage.seedTemplatesIfMissing: wrote '{}'", template.id());
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // JSON conversion
    // ---------------------------------------------------------------------------------------------

    private static JsonObject toJson(LayoutDoc doc) {
        var obj = new JsonObject();
        obj.addProperty("version", doc.version());
        obj.addProperty("id", doc.id());
        obj.addProperty("displayName", doc.displayName());
        if (doc.templateBase() != null) {
            obj.addProperty("templateBase", doc.templateBase());
        }
        obj.addProperty("createdAt", doc.createdAt());
        obj.addProperty("modifiedAt", doc.modifiedAt());
        obj.add("body", bodyToJson(doc.body()));
        return obj;
    }

    private static LayoutDoc fromJson(JsonObject obj, String fallbackId) {
        var version = obj.has("version") ? obj.get("version").getAsInt() : LayoutDoc.CURRENT_VERSION;
        if (version != LayoutDoc.CURRENT_VERSION) {
            throw new IllegalArgumentException("Unsupported layout version " + version + " (expected " + LayoutDoc.CURRENT_VERSION + ")");
        }
        var rawId = obj.has("id") ? obj.get("id").getAsString() : fallbackId;
        String resolvedId;
        try {
            LayoutDoc.validateId(rawId);
            resolvedId = rawId;
        } catch (IllegalArgumentException e) {
            LOGGER.warn("[BLib] LayoutStorage: invalid inner id '{}' in {}.json; falling back to filename", rawId, fallbackId);
            LayoutDoc.validateId(fallbackId);
            resolvedId = fallbackId;
        }
        if (!resolvedId.equals(fallbackId)) {
            LOGGER.warn("[BLib] LayoutStorage: inner id '{}' differs from filename '{}'; preferring filename", resolvedId, fallbackId);
            resolvedId = fallbackId;
        }
        var displayName = obj.has("displayName") ? obj.get("displayName").getAsString() : resolvedId;
        var templateBase = obj.has("templateBase") && !obj.get("templateBase").isJsonNull()
            ? obj.get("templateBase").getAsString()
            : null;
        var createdAt = obj.has("createdAt") ? obj.get("createdAt").getAsString() : "";
        var modifiedAt = obj.has("modifiedAt") ? obj.get("modifiedAt").getAsString() : createdAt;
        var body = bodyFromJson(obj.get("body"));
        return new LayoutDoc(version, resolvedId, displayName, templateBase, createdAt, modifiedAt, body);
    }

    private static JsonObject bodyToJson(BodyNode node) {
        var obj = new JsonObject();
        switch (node) {
            case BodyNode.Leaf leaf -> {
                obj.addProperty("kind", "leaf");
                var tabs = new JsonArray();
                for (var t : leaf.tabs()) {
                    tabs.add(t);
                }
                obj.add("tabs", tabs);
                obj.addProperty("activeIndex", leaf.activeIndex());
            }
            case BodyNode.Split split -> {
                obj.addProperty("kind", "split");
                obj.addProperty("orientation", split.orientation());
                obj.add("first", bodyToJson(split.first()));
                obj.add("second", bodyToJson(split.second()));
                obj.add("sizing", sizingToJson(split.sizing()));
            }
        }
        return obj;
    }

    private static BodyNode bodyFromJson(JsonElement elem) {
        if (elem == null || !elem.isJsonObject()) {
            throw new IllegalArgumentException("body must be a JSON object");
        }
        var obj = elem.getAsJsonObject();
        var kind = obj.has("kind") ? obj.get("kind").getAsString() : "";
        return switch (kind) {
            case "leaf" -> {
                var tabsArr = obj.has("tabs") && obj.get("tabs").isJsonArray() ? obj.getAsJsonArray("tabs") : new JsonArray();
                var tabs = new ArrayList<String>(tabsArr.size());
                for (var t : tabsArr) {
                    tabs.add(t.getAsString());
                }
                var activeIndex = obj.has("activeIndex") ? obj.get("activeIndex").getAsInt() : 0;
                yield new BodyNode.Leaf(tabs, activeIndex);
            }
            case "split" -> {
                var orientation = obj.has("orientation") ? obj.get("orientation").getAsString() : "HORIZONTAL";
                var first = bodyFromJson(obj.get("first"));
                var second = bodyFromJson(obj.get("second"));
                var sizing = sizingFromJson(obj.get("sizing"));
                yield new BodyNode.Split(orientation, first, second, sizing);
            }
            default -> throw new IllegalArgumentException("Unknown body kind: '" + kind + "'");
        };
    }

    private static JsonObject sizingToJson(SizingDoc s) {
        var obj = new JsonObject();
        switch (s) {
            case SizingDoc.Ratio r -> {
                obj.addProperty("kind", "ratio");
                obj.addProperty("value", r.value());
            }
            case SizingDoc.FirstFixed f -> {
                obj.addProperty("kind", "first_fixed");
                obj.addProperty("pixels", f.pixels());
            }
            case SizingDoc.SecondFixed sf -> {
                obj.addProperty("kind", "second_fixed");
                obj.addProperty("pixels", sf.pixels());
            }
        }
        return obj;
    }

    private static SizingDoc sizingFromJson(JsonElement elem) {
        if (elem == null || !elem.isJsonObject()) {
            throw new IllegalArgumentException("sizing must be a JSON object");
        }
        var obj = elem.getAsJsonObject();
        var kind = obj.has("kind") ? obj.get("kind").getAsString() : "";
        return switch (kind) {
            case "ratio" -> new SizingDoc.Ratio(obj.get("value").getAsFloat());
            case "first_fixed" -> new SizingDoc.FirstFixed(obj.get("pixels").getAsInt());
            case "second_fixed" -> new SizingDoc.SecondFixed(obj.get("pixels").getAsInt());
            default -> throw new IllegalArgumentException("Unknown sizing kind: '" + kind + "'");
        };
    }
}
