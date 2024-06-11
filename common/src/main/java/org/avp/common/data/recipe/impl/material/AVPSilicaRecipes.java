package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.registry.block.AVPOreBlockRegistry;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPSilicaRecipes {

    public static void addSilicaRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.silica.get(),
            AVPOreBlockRegistry.INSTANCE.silicaBlock.get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlockRegistry.INSTANCE.silicaBlock.get(),
            AVPItemRegistry.INSTANCE.silica.get()
        );
    }

    private AVPSilicaRecipes() {
        throw new UnsupportedOperationException();
    }
}
