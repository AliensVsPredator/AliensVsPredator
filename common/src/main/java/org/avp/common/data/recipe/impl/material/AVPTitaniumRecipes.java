package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.block.AVPBlocks;
import org.avp.common.block.AVPOreBlocks;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.item.AVPItems;

public final class AVPTitaniumRecipes {

    public static void addTitaniumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INSTANCE.ingotTitanium.get(),
            AVPBlocks.INSTANCE.titaniumBlock.get()
        );
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INSTANCE.rawTitanium.get(),
            AVPOreBlocks.INSTANCE.rawTitaniumBlock.get()
        );

        // Smelting
        AVPRecipeHelper.oreSmelting(
            recipeOutput,
            RecipeCategory.MISC,
            AVPItems.INSTANCE.rawTitanium,
            AVPOreBlocks.INSTANCE.oreTitanium,
            AVPItems.INSTANCE.ingotTitanium
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPBlocks.INSTANCE.titaniumBlock.get(),
            AVPItems.INSTANCE.ingotTitanium.get()
        );
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.INSTANCE.rawTitaniumBlock.get(),
            AVPItems.INSTANCE.rawTitanium.get()
        );

        // Furnace recipes
    }

    private AVPTitaniumRecipes() {
        throw new UnsupportedOperationException();
    }
}
