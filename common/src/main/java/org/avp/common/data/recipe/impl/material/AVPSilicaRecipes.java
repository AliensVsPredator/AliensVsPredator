package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.data.block.raw.RawSilicaBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

/**
 * @deprecated
 */
public final class AVPSilicaRecipes {

    public static void addSilicaRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.silica.get(),
            RawSilicaBlockDataContainer.INSTANCE.getHolder().get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            RawSilicaBlockDataContainer.INSTANCE.getHolder().get(),
            AVPItemRegistry.INSTANCE.silica.get()
        );
    }

    private AVPSilicaRecipes() {
        throw new UnsupportedOperationException();
    }
}
