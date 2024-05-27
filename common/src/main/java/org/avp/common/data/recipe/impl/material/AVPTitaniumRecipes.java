package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.block.AVPBlocks;
import org.avp.common.block.AVPOreBlocks;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.item.AVPItems;

public final class AVPTitaniumRecipes {

    public static void addTitaniumRecipes(RecipeOutput recipeOutput) {
        var ingotHolder = AVPItems.INSTANCE.ingotTitanium;

        AVPMetalRecipeHelper.addFullMetalSetRecipes(recipeOutput, ingotHolder, AVPBlocks.INSTANCE.titanium);

        // Compressed raw
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INSTANCE.rawTitanium.get(),
            AVPOreBlocks.INSTANCE.rawTitaniumBlock.get()
        );

        // Decompressed raw
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.INSTANCE.rawTitaniumBlock.get(),
            AVPItems.INSTANCE.rawTitanium.get()
        );

        // Smelting
        AVPRecipeHelper.oreSmelting(
            recipeOutput,
            RecipeCategory.MISC,
            AVPItems.INSTANCE.rawTitanium,
            AVPOreBlocks.INSTANCE.oreTitanium,
            AVPItems.INSTANCE.ingotTitanium
        );

        // Furnace recipes
    }

    private AVPTitaniumRecipes() {
        throw new UnsupportedOperationException();
    }
}
