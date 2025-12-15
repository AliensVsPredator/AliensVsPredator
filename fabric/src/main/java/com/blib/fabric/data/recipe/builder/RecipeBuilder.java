package com.blib.fabric.data.recipe.builder;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import com.blib.BLibMod;

public class RecipeBuilder {

    public static RecipeBuilder with(BLibMod mod, RecipeOutput recipeOutput) {
        return new RecipeBuilder(mod, recipeOutput);
    }

    private final BLibMod mod;

    private final RecipeOutput recipeOutput;

    private RecipeBuilder(BLibMod mod, RecipeOutput recipeOutput) {
        this.mod = mod;
        this.recipeOutput = recipeOutput;
    }

    public BlastingRecipeBuilder blast(Supplier<? extends ItemLike> sourceSupplier) {
        return blast(sourceSupplier.get());
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

    public SmeltingRecipeBuilder smelt(Supplier<? extends ItemLike> sourceSupplier) {
        return smelt(sourceSupplier.get());
    }

    public SmeltingRecipeBuilder smelt(ItemLike source) {
        return new SmeltingRecipeBuilder(this, source);
    }

    public StonecutterRecipeBuilder stonecut(Supplier<? extends ItemLike> sourceSupplier) {
        return stonecut(sourceSupplier.get());
    }

    public StonecutterRecipeBuilder stonecut(ItemLike source) {
        return new StonecutterRecipeBuilder(this, source);
    }

    public BLibMod getMod() {
        return mod;
    }

    public RecipeOutput getRecipeOutput() {
        return recipeOutput;
    }
}
