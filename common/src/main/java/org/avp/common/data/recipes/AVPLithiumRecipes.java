package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import org.avp.common.block.AVPOreBlocks;
import org.avp.common.item.AVPItems;

public final class AVPLithiumRecipes {

    public static void addLithiumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INGOT_LITHIUM.get(),
            AVPOreBlocks.RAW_LITHIUM_BLOCK.get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.RAW_LITHIUM_BLOCK.get(),
            AVPItems.INGOT_LITHIUM.get()
        );
    }

    private AVPLithiumRecipes() {
        throw new UnsupportedOperationException();
    }
}
