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

import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Per-project recent-files list for modeler model imports. Backed by a tiny JSON file at
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

    /** Newest-first list of absolute paths for {@code projectName}'s most recently opened models. */
    public static List<String> list(String projectName) {
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
            var out = new ArrayList<String>(arr.size());
            for (var el : arr) {
                if (el.isJsonPrimitive()) {
                    out.add(el.getAsString());
                }
            }
            return out;
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("[BLib] ModelerRecentFiles.list: failed to read {}", path, e);
            return List.of();
        }
    }

    /**
     * Record that {@code path} was just opened in {@code projectName}: dedupe, push to head, cap at
     * {@link #MAX_RECENT}, write back. Silently no-ops when there's no project context — the modeler may be used in
     * MENU_OVERLAY before a project is picked.
     */
    public static void recordOpen(String projectName, String path) {
        if (projectName == null || projectName.isEmpty() || path == null || path.isEmpty()) {
            return;
        }
        var current = new ArrayList<>(list(projectName));
        current.removeIf(p -> p.equals(path));
        current.add(0, path);
        while (current.size() > MAX_RECENT) {
            current.remove(current.size() - 1);
        }

        var file = EngineProjectIO.projectRoot(projectName).resolve(FILE_NAME);
        try {
            Files.createDirectories(file.getParent());
            var json = new JsonObject();
            var arr = new JsonArray();
            for (var entry : current) {
                arr.add(entry);
            }
            json.add(FIELD, arr);
            Files.writeString(file, GSON.toJson(json));
        } catch (IOException e) {
            LOGGER.warn("[BLib] ModelerRecentFiles.recordOpen: failed to write {}", file, e);
        }
    }
}
