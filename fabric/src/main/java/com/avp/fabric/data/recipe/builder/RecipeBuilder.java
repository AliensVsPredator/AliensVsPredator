package com.avp.fabric.data.recipe.builder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import static net.minecraft.data.recipes.RecipeProvider.getHasName;
import static net.minecraft.data.recipes.RecipeProvider.has;

public class RecipeBuilder {

    public static RecipeBuilder with(RecipeOutput recipeOutput) {
        return new RecipeBuilder(recipeOutput);
    }

    private final RecipeOutput recipeOutput;

    private RecipeBuilder(RecipeOutput recipeOutput) {
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

    public StonecutterRecipeBuilder stonecut(ItemLike source) {
        return new StonecutterRecipeBuilder(this, source);
    }

    public IndustrialFurnaceRecipeBuilder industrialFurnaceSmelting(ItemLike item) {
        IndustrialFurnaceRecipeBuilder builder = IndustrialFurnaceRecipeBuilder.smelting(
            Ingredient.of(item),
            RecipeCategory.MISC,
            item,
            0.1f,
            100
        );

        builder.unlockedBy(getHasName(item), has(item));

        return builder;
    }

    public RecipeOutput getRecipeOutput() {
        return recipeOutput;
    }
}
