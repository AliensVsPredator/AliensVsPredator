package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.block.Block;
import org.avp.api.GameObject;

public class RecipeUtils {

    // Stonecutter - Building Blocks
    public static void stonecutterBuildingBlock(RecipeOutput recipeOutput, GameObject<Block> input, GameObject<Block> output) {
        stonecutterBuildingBlock(recipeOutput, input, output, 1);
    }

    public static void stonecutterBuildingBlock(RecipeOutput recipeOutput, GameObject<Block> input, GameObject<Block> output, int count) {
        AVPRecipeProvider.stonecutterRecipeFromBase(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            output.get(),
            input.get(),
            count
        );
    }

    private RecipeUtils() {
        throw new UnsupportedOperationException();
    }
}
