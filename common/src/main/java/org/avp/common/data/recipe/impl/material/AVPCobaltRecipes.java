package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.data.block.raw.RawCobaltBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

/**
 * @deprecated
 */
public final class AVPCobaltRecipes {

    public static void addCobaltRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.cobalt.get(),
            RawCobaltBlockDataContainer.INSTANCE.getHolder().get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            RawCobaltBlockDataContainer.INSTANCE.getHolder().get(),
            AVPItemRegistry.INSTANCE.cobalt.get()
        );
    }

    private AVPCobaltRecipes() {
        throw new UnsupportedOperationException();
    }
}
