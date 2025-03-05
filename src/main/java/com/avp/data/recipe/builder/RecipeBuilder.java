package com.avp.data.recipe.builder;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;

public class RecipeBuilder {

    public static RecipeBuilder with(RecipeOutput recipeOutput) {
        return new RecipeBuilder(recipeOutput);
    }

    private final RecipeOutput recipeOutput;

    private RecipeBuilder(RecipeOutput recipeOutput) {
        this.recipeOutput = recipeOutput;
    }

    public BlastingRecipeBuilder blast(ItemLike source) {
        return new BlastingRecipeBuilder(this, source);
    }

    public ShapedRecipeBuilder shaped() {
        return new ShapedRecipeBuilder(this);
    }

    public ShapelessRecipeBuilder shapeless() {
        return new ShapelessRecipeBuilder(this);
    }

    public SmeltingRecipeBuilder smelt(ItemLike source) {
        return new SmeltingRecipeBuilder(this, source);
    }

    public StonecutterRecipeBuilder stonecut(ItemLike source) {
        return new StonecutterRecipeBuilder(this, source);
    }

    public RecipeOutput getRecipeOutput() {
        return recipeOutput;
    }
}
