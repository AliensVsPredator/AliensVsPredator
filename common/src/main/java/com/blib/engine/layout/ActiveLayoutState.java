package com.blib.engine.layout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.blib.api.BLibAPI;

/**
 * Persistence for "which layout is currently active". Stored as a tiny companion file at
 * {@code <gameDir>/blib/engine/state.json} alongside the layouts directory. Two pieces of state:
 * <ul>
 * <li>{@code activeLayoutId} — global default for new project sessions.</li>
 * <li>{@code perProjectActive} — map from project name to layout id, so each project remembers the layout last used
 * inside it. Populated on every {@code switchLayout} / {@code removed()} when a project is open.</li>
 * </ul>
 * The {@link #resolve} precedence is per-project → global → fallback. The fallback is always
 * {@link LayoutTemplate#DEFAULT}'s id so a fresh-install game always lands somewhere sane even with no state file
 * present.
 * <p>
 * The state file is read once on every workspace open and written on every switch / close — small JSON, no
 * synchronization needed (single-player only).
 */
@ApiStatus.Internal
public final class ActiveLayoutState {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveLayoutState.class);

    private static final String STATE_FILE = "blib/engine/state.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ActiveLayoutState() {}

    /**
     * Top-level shape of {@code state.json}. Immutable; callers use {@code with*} helpers to derive a new state, then
     * {@link ActiveLayoutState#write}.
     */
    public record State(
        String activeLayoutId,
        Map<String, String> perProjectActive
    ) {

        public State {
            perProjectActive = perProjectActive == null ? Map.of() : Map.copyOf(perProjectActive);
        }

        public static State empty() {
            return new State(LayoutTemplate.DEFAULT.id(), Map.of());
        }

        public State withGlobalActive(String id) {
            return new State(id, perProjectActive);
        }

        public State withProjectActive(String project, String id) {
            var next = new HashMap<>(perProjectActive);
            next.put(project, id);
            return new State(activeLayoutId, next);
        }

        /**
         * Drop every reference to {@code id} — used when a layout is deleted so a stale entry doesn't keep pointing at
         * a missing file. Both global and per-project entries pointing at the deleted id are removed.
         */
        public State withoutLayout(String id) {
            var next = new HashMap<String, String>();
            for (var e : perProjectActive.entrySet()) {
                if (!id.equals(e.getValue())) {
                    next.put(e.getKey(), e.getValue());
                }
            }
            var newGlobal = id.equals(activeLayoutId) ? LayoutTemplate.DEFAULT.id() : activeLayoutId;
            return new State(newGlobal, next);
        }
    }

    private static Path stateFile() {
        return BLibAPI.getGameDirectory().resolve(STATE_FILE);
    }

    public static State read() {
        var path = stateFile();
        if (!Files.isRegularFile(path)) {
            return State.empty();
        }
        try {
            var text = Files.readString(path);
            var parsed = JsonParser.parseString(text);
            if (!parsed.isJsonObject()) {
                LOGGER.warn("[BLib] ActiveLayoutState.read: {} is not a JSON object; using empty state", path);
                return State.empty();
            }
            return fromJson(parsed.getAsJsonObject());
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("[BLib] ActiveLayoutState.read: failed to parse {}; using empty state", path, e);
            return State.empty();
        }
    }

    public static void write(State state) {
        var path = stateFile();
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(toJson(state)));
        } catch (IOException e) {
            LOGGER.warn("[BLib] ActiveLayoutState.write: failed to write {}", path, e);
        }
    }

    /**
     * Resolve the active layout id given an optional project context. Per-project memory wins; otherwise the global
     * default; otherwise the {@link LayoutTemplate#DEFAULT} id as a final fallback.
     */
    public static String resolve(@Nullable String projectName, State state) {
        if (projectName != null && !projectName.isEmpty()) {
            var perProject = state.perProjectActive().get(projectName);
            if (perProject != null && !perProject.isEmpty()) {
                return perProject;
            }
        }
        var global = state.activeLayoutId();
        if (global != null && !global.isEmpty()) {
            return global;
        }
        return LayoutTemplate.DEFAULT.id();
    }

    private static JsonObject toJson(State state) {
        var obj = new JsonObject();
        obj.addProperty("activeLayoutId", state.activeLayoutId());
        var perProj = new JsonObject();
        for (var e : state.perProjectActive().entrySet()) {
            perProj.addProperty(e.getKey(), e.getValue());
        }
        obj.add("perProjectActive", perProj);
        return obj;
    }

    private static State fromJson(JsonObject obj) {
        var activeId = obj.has("activeLayoutId") ? obj.get("activeLayoutId").getAsString() : LayoutTemplate.DEFAULT.id();
        var perProjectMap = new HashMap<String, String>();
        if (obj.has("perProjectActive") && obj.get("perProjectActive").isJsonObject()) {
            var ppObj = obj.getAsJsonObject("perProjectActive");
            for (var e : ppObj.entrySet()) {
                if (e.getValue().isJsonPrimitive()) {
                    perProjectMap.put(e.getKey(), e.getValue().getAsString());
                }
            }
        }
        return new State(activeId, perProjectMap);
    }
}
