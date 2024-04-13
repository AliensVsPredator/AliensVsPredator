package org.avp.common.data.recipes.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import org.avp.common.block.AVPOreBlocks;
import org.avp.common.data.recipes.AVPRecipeHelper;
import org.avp.common.item.AVPItems;

public final class AVPSilicaRecipes {

    public static void addSilicaRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.SILICA.get(),
            AVPOreBlocks.SILICA_BLOCK.get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.SILICA_BLOCK.get(),
            AVPItems.SILICA.get()
        );
    }

    private AVPSilicaRecipes() {
        throw new UnsupportedOperationException();
    }
}
