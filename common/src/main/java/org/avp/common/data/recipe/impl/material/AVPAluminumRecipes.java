package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.block.AVPBlocks;
import org.avp.common.block.AVPOreBlocks;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.item.AVPItems;

public final class AVPAluminumRecipes {

    public static void addAluminumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INSTANCE.ingotAluminum.get(),
            AVPBlocks.INSTANCE.aluminumBlock.get()
        );
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INSTANCE.rawBauxite.get(),
            AVPOreBlocks.INSTANCE.rawBauxiteBlock.get()
        );

        // Smelting
        AVPRecipeHelper.oreSmelting(
            recipeOutput,
            RecipeCategory.MISC,
            AVPItems.INSTANCE.rawBauxite,
            AVPOreBlocks.INSTANCE.oreBauxite,
            AVPItems.INSTANCE.ingotAluminum
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPBlocks.INSTANCE.aluminumBlock.get(),
            AVPItems.INSTANCE.ingotAluminum.get()
        );
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlocks.INSTANCE.rawBauxiteBlock.get(),
            AVPItems.INSTANCE.rawBauxite.get()
        );

        // Furnace recipes
    }

    private AVPAluminumRecipes() {
        throw new UnsupportedOperationException();
    }
}
