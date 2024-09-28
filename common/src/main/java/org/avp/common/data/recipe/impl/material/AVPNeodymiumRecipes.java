package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.data.block.raw.NeodymiumBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

/**
 * @deprecated
 */
public final class AVPNeodymiumRecipes {

    public static void addNeodymiumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.neodymium.get(),
            NeodymiumBlockDataContainer.INSTANCE.getHolder().get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            NeodymiumBlockDataContainer.INSTANCE.getHolder().get(),
            AVPItemRegistry.INSTANCE.neodymium.get()
        );
    }

    private AVPNeodymiumRecipes() {
        throw new UnsupportedOperationException();
    }
}
