package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import org.avp.common.block.AVPOreBlocks;
import org.avp.common.item.AVPItems;

public final class AVPCobaltRecipes {

    public static void addCobaltRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.COBALT.get(),
            AVPOreBlocks.COBALT_BLOCK.get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.ORE_COBALT.get(),
            AVPItems.COBALT.get()
        );
    }

    private AVPCobaltRecipes() {
        throw new UnsupportedOperationException();
    }
}
