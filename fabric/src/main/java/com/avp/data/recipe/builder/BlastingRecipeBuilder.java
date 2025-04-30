package com.avp.data.recipe.builder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import com.avp.AVP;
import com.avp.data.recipe.util.RecipeProviderProxy;

public class BlastingRecipeBuilder {

    private final RecipeBuilder recipeBuilder;

    private final ItemLike source;

    private RecipeCategory recipeCategory;

    private float experience;

    private int cookTime;

    BlastingRecipeBuilder(RecipeBuilder recipeBuilder, ItemLike source) {
        this.recipeBuilder = recipeBuilder;
        this.source = source;
        this.recipeCategory = RecipeCategory.MISC;
        this.cookTime = 100; // 5 seconds
    }

    public BlastingRecipeBuilder withCategory(RecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
        return this;
    }

    public BlastingRecipeBuilder withExperience(float experience) {
        this.experience = experience;
        return this;
    }

    public BlastingRecipeBuilder withCookTime(int cookTime) {
        this.cookTime = cookTime;
        return this;
    }

    public void into(ItemLike destination) {
        var ingredient = Ingredient.of(source);
        var sourceName = RecipeProviderProxy.getNameForItem(source.asItem());
        var destinationName = RecipeProviderProxy.getNameForItem(destination.asItem());

        SimpleCookingRecipeBuilder.blasting(ingredient, recipeCategory, destination, experience, cookTime)
            .unlockedBy("has_" + sourceName, RecipeProviderProxy.has(source))
            .save(recipeBuilder.getRecipeOutput(), AVP.MOD_ID + ":" + destinationName + "_from_blasting_" + sourceName);
    }
}
