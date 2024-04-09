package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.block.AVPBlocks;
import org.avp.common.block.AVPOreBlocks;
import org.avp.common.item.AVPItems;

public final class AVPAluminumRecipes {

    public static void addAluminumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INGOT_ALUMINUM.get(),
            AVPBlocks.ALUMINUM_BLOCK.get()
        );
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.RAW_BAUXITE.get(),
            AVPOreBlocks.RAW_BAUXITE_BLOCK.get()
        );

        // Smelting
        AVPRecipeHelper.oreSmelting(
            recipeOutput,
            RecipeCategory.MISC,
            AVPItems.RAW_BAUXITE,
            AVPOreBlocks.ORE_BAUXITE,
            AVPItems.INGOT_ALUMINUM
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPBlocks.ALUMINUM_BLOCK.get(),
            AVPItems.INGOT_ALUMINUM.get()
        );
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.RAW_BAUXITE_BLOCK.get(),
            AVPItems.RAW_BAUXITE.get()
        );

        // Furnace recipes
    }

    private AVPAluminumRecipes() {
        throw new UnsupportedOperationException();
    }
}
