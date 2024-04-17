package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.block.AVPOreBlocks;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.item.AVPItems;

public final class AVPLithiumRecipes {

    public static void addLithiumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INSTANCE.dustLithium.get(),
            AVPOreBlocks.INSTANCE.lithiumBlock.get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.INSTANCE.lithiumBlock.get(),
            AVPItems.INSTANCE.dustLithium.get()
        );
    }

    private AVPLithiumRecipes() {
        throw new UnsupportedOperationException();
    }
}
