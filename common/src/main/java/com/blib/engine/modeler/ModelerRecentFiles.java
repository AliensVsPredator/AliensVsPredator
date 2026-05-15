package com.blib.engine.modeler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Per-project recent model list for modeler imports. Backed by a tiny JSON file at
 * {@code <gameDir>/blib/projects/<projectName>/modeler_recent.json}, so the list survives game restarts and stays
 * scoped to the project that authored it (different projects keep different lists, which matters when an author juggles
 * unrelated mods).
 * <p>
 * Move-to-front semantics: {@link #recordOpen} dedupes against the existing list and pushes the latest entry to the
 * head, so the menu reads newest-first. Capped at {@link #MAX_RECENT}; oldest entries fall off the tail.
 * <p>
 * Stale entries (files moved / deleted out from under us) are kept in the list — the loader handles missing files
 * gracefully and the user might restore the file. Pruning would risk losing a still-loved entry to a transient
 * filesystem hiccup.
 */
@ApiStatus.Internal
public final class ModelerRecentFiles {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelerRecentFiles.class);

    private static final String FILE_NAME = "modeler_recent.json";

    private static final String FIELD = "recentModels";

    private static final int MAX_RECENT = 10;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ModelerRecentFiles() {}

    public enum Source {
        EXTERNAL("External"),
        BLOCK("Block"),
        ITEM_CONFIG("Item Config");

        private final String label;

        Source(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }

        private static Source parse(String value) {
            if (value == null || value.isBlank()) {
                return EXTERNAL;
            }
            try {
                return Source.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                return EXTERNAL;
            }
        }
    }

    public record Entry(
        Source source,
        String target
    ) {}

    /** Newest-first list of modeler imports for {@code projectName}. Old string-only entries are read as external files. */
    public static List<Entry> list(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return List.of();
        }
        var path = EngineProjectIO.projectRoot(projectName).resolve(FILE_NAME);
        if (!Files.isRegularFile(path)) {
            return List.of();
        }
        try {
            var json = JsonParser.parseString(Files.readString(path));
            if (!json.isJsonObject()) {
                return List.of();
            }
            var arr = json.getAsJsonObject().getAsJsonArray(FIELD);
            if (arr == null) {
                return List.of();
            }
            var out = new ArrayList<Entry>(arr.size());
            for (var el : arr) {
                if (el.isJsonPrimitive()) {
                    out.add(new Entry(Source.EXTERNAL, el.getAsString()));
                } else if (el.isJsonObject()) {
                    var obj = el.getAsJsonObject();
                    var targetEl = obj.get("target");
                    if (targetEl == null || !targetEl.isJsonPrimitive()) {
                        continue;
                    }
                    var target = targetEl.getAsString();
                    if (target == null || target.isBlank()) {
                        continue;
                    }
                    var sourceEl = obj.get("source");
                    var source = sourceEl != null && sourceEl.isJsonPrimitive() ? Source.parse(sourceEl.getAsString()) : Source.EXTERNAL;
                    out.add(new Entry(source, target));
                }
            }
            return out;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("[BLib] ModelerRecentFiles.list: failed to read {}", path, e);
            return List.of();
        }
    }

    /**
     * Record that an external model file was just opened in {@code projectName}.
     */
    public static void recordExternalOpen(String projectName, String path) {
        recordOpen(projectName, new Entry(Source.EXTERNAL, path));
    }

    /** Record that a block model was just opened in {@code projectName}. */
    public static void recordBlockOpen(String projectName, String blockId) {
        recordOpen(projectName, new Entry(Source.BLOCK, blockId));
    }

    /** Record that an item config model was just opened in {@code projectName}. */
    public static void recordItemConfigOpen(String projectName, String itemId) {
        recordOpen(projectName, new Entry(Source.ITEM_CONFIG, itemId));
    }

    /**
     * Record that {@code entry} was just opened in {@code projectName}: dedupe, push to head, cap at
     * {@link #MAX_RECENT}, write back. Silently no-ops when there's no project context — the modeler may be used in
     * MENU_OVERLAY before a project is picked.
     */
    public static void recordOpen(String projectName, Entry entry) {
        if (projectName == null || projectName.isEmpty() || entry == null || entry.target() == null || entry.target().isEmpty()) {
            return;
        }
        var current = new ArrayList<>(list(projectName));
        current.removeIf(existing -> existing.source() == entry.source() && existing.target().equals(entry.target()));
        current.add(0, entry);
        while (current.size() > MAX_RECENT) {
            current.remove(current.size() - 1);
        }

        var file = EngineProjectIO.projectRoot(projectName).resolve(FILE_NAME);
        try {
            Files.createDirectories(file.getParent());
            var json = new JsonObject();
            var arr = new JsonArray();
            for (var recent : current) {
                var obj = new JsonObject();
                obj.addProperty("source", recent.source().name().toLowerCase(Locale.ROOT));
                obj.addProperty("target", recent.target());
                arr.add(obj);
            }
            json.add(FIELD, arr);
            Files.writeString(file, GSON.toJson(json));
        } catch (IOException e) {
            LOGGER.warn("[BLib] ModelerRecentFiles.recordOpen: failed to write {}", file, e);
        }
    }
}
