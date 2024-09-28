package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.data.block.raw.RawLithiumBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

/**
 * @deprecated
 */
public final class AVPLithiumRecipes {

    public static void addLithiumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.dustLithium.get(),
            RawLithiumBlockDataContainer.INSTANCE.getHolder().get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            RawLithiumBlockDataContainer.INSTANCE.getHolder().get(),
            AVPItemRegistry.INSTANCE.dustLithium.get()
        );
    }

    private AVPLithiumRecipes() {
        throw new UnsupportedOperationException();
    }
}
