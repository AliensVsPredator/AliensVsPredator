package com.avp.fabric.data.recipe.builder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.level.ItemLike;

import com.avp.fabric.data.recipe.RecipeProvider;

import java.util.function.Supplier;

public class StonecutterRecipeBuilder {

    private final RecipeBuilder recipeBuilder;

    private final ItemLike source;

    private RecipeCategory recipeCategory;

    StonecutterRecipeBuilder(RecipeBuilder recipeBuilder, ItemLike source) {
        this.recipeBuilder = recipeBuilder;
        this.source = source;
        this.recipeCategory = RecipeCategory.MISC;
    }

    public StonecutterRecipeBuilder withCategory(RecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
        return this;
    }

    public void into(int count, Supplier<? extends ItemLike> supplier) {
        into(count, supplier.get());
    }

    public void into(int count, ItemLike destination) {
        RecipeProvider.stonecutterResultFromBase(
            recipeBuilder.getRecipeOutput(),
            recipeCategory,
            destination.asItem(),
            source.asItem(),
            count
        );
    }
}
