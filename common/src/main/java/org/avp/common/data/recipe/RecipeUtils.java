package org.avp.common.data.recipe;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Block;

import org.avp.api.registry.holder.BLHolder;

public class RecipeUtils {

    // Stonecutter - Building Blocks
    public static void stonecutterBuildingBlock(RecipeOutput recipeOutput, BLHolder<Block> input, BLHolder<Block> output) {
        stonecutterBuildingBlock(recipeOutput, input.get(), output.get(), 1);
    }

    public static void stonecutterBuildingBlock(RecipeOutput recipeOutput, Block input, Block output) {
        stonecutterBuildingBlock(recipeOutput, input, output, 1);
    }

    public static void stonecutterBuildingBlock(RecipeOutput recipeOutput, BLHolder<Block> input, BLHolder<Block> output, int count) {
        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            output.get(),
            input.get(),
            count
        );
    }

    public static void stonecutterBuildingBlock(RecipeOutput recipeOutput, Block input, Block output, int count) {
        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            output,
            input,
            count
        );
    }

    private RecipeUtils() {
        throw new UnsupportedOperationException();
    }
}
