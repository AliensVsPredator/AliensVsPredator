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
            AVPItems.INGOT_TITANIUM.get(),
            AVPBlocks.TITANIUM_BLOCK.get()
        );
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.RAW_TITANIUM.get(),
            AVPOreBlocks.RAW_TITANIUM_BLOCK.get()
        );

        // Smelting
        AVPRecipeHelper.oreSmelting(
            recipeOutput,
            RecipeCategory.MISC,
            AVPItems.RAW_TITANIUM,
            AVPOreBlocks.ORE_TITANIUM,
            AVPItems.INGOT_TITANIUM
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPBlocks.TITANIUM_BLOCK.get(),
            AVPItems.INGOT_TITANIUM.get()
        );
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.RAW_TITANIUM_BLOCK.get(),
            AVPItems.RAW_TITANIUM.get()
        );

        // Furnace recipes
    }

    private AVPTitaniumRecipes() {
        throw new UnsupportedOperationException();
    }
}
