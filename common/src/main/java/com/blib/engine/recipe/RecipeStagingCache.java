package com.blib.engine.recipe;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * Client-side overlay of recipe JSON writes that have not been committed into the live recipe manager by Reload
 * Project yet. Mirrors the tag browser's red staged state: save writes the file, reload clears the staging tint.
 */
@ApiStatus.Internal
public final class RecipeStagingCache {

    private static final Set<ResourceLocation> stagedRecipes = new HashSet<>();

    private RecipeStagingCache() {}

    public static void markRecipeSaved(ResourceLocation recipeId) {
        stagedRecipes.add(recipeId);
    }

    public static boolean isRecipeStaged(ResourceLocation recipeId) {
        return stagedRecipes.contains(recipeId);
    }

    public static Set<ResourceLocation> stagedRecipes() {
        return Set.copyOf(stagedRecipes);
    }

    public static void clear() {
        stagedRecipes.clear();
    }
}
