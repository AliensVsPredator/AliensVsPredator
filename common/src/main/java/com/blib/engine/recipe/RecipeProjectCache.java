package com.blib.engine.recipe;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Client-side index of recipe JSONs authored in the active project's datapack. The live recipe manager only sees these
 * after a project reload; this cache lets the outliner show project recipes immediately and provide an All / Project
 * filter before reload catches up.
 */
@ApiStatus.Internal
public final class RecipeProjectCache {

    public record Entry(
        ResourceLocation id,
        String typeId
    ) {}

    private static String projectName = "";

    private static List<Entry> entries = List.of();

    private static Map<ResourceLocation, Entry> byId = Map.of();

    private RecipeProjectCache() {}

    public static void refresh(String nextProjectName) {
        projectName = nextProjectName == null ? "" : nextProjectName;
        if (projectName.isEmpty()) {
            entries = List.of();
            byId = Map.of();
            return;
        }

        var next = new ArrayList<Entry>();
        for (var id : EngineProjectIO.listProjectRecipes(projectName)) {
            next.add(new Entry(id, readTypeId(projectName, id)));
        }
        replaceEntries(next);
    }

    public static void refreshIfProjectChanged(String nextProjectName) {
        var normalized = nextProjectName == null ? "" : nextProjectName;
        if (!normalized.equals(projectName)) {
            refresh(normalized);
        }
    }

    public static List<Entry> entries() {
        return entries;
    }

    public static boolean contains(ResourceLocation id) {
        return byId.containsKey(id);
    }

    public static void markPresent(String nextProjectName, ResourceLocation id, String typeId) {
        var normalized = nextProjectName == null ? "" : nextProjectName;
        if (normalized.isEmpty()) {
            return;
        }
        refreshIfProjectChanged(normalized);

        var next = new ArrayList<>(entries);
        next.removeIf(entry -> entry.id().equals(id));
        next.add(new Entry(id, typeId == null || typeId.isBlank() ? "unknown" : typeId));
        replaceEntries(next);
    }

    public static void clear() {
        projectName = "";
        entries = List.of();
        byId = Map.of();
    }

    private static void replaceEntries(List<Entry> next) {
        next.sort(Comparator.comparing(entry -> entry.id().toString(), String.CASE_INSENSITIVE_ORDER));
        entries = List.copyOf(next);
        var map = new LinkedHashMap<ResourceLocation, Entry>();
        for (var entry : entries) {
            map.put(entry.id(), entry);
        }
        byId = Map.copyOf(map);
    }

    private static String readTypeId(String projectName, ResourceLocation id) {
        return EngineProjectIO
            .readRecipeJson(projectName, id)
            .filter(JsonObject.class::isInstance)
            .map(JsonObject.class::cast)
            .map(json -> json.has("type") && json.get("type").isJsonPrimitive() ? json.get("type").getAsString() : "unknown")
            .filter(type -> !type.isBlank())
            .orElse("unknown");
    }
}
