package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import org.avp.common.block.AVPOreBlocks;
import org.avp.common.item.AVPItems;

public final class AVPNeodymiumRecipes {

    public static void addNeodymiumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.NEODYMIUM.get(),
            AVPOreBlocks.NEODYMIUM_BLOCK.get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.NEODYMIUM_BLOCK.get(),
            AVPItems.NEODYMIUM.get()
        );
    }

    private AVPNeodymiumRecipes() {
        throw new UnsupportedOperationException();
    }
}
