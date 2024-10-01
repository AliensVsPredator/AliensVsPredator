package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.data.block.metal.MetalTitaniumBlockSetDataContainer;
import org.avp.common.data.block.ore.OreTitaniumBlockDataContainer;
import org.avp.common.data.block.raw.RawTitaniumBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

/**
 * @deprecated
 */
public final class AVPTitaniumRecipes {

    public static void addTitaniumRecipes(RecipeOutput recipeOutput) {
        // Compressed raw
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.rawTitanium.get(),
            RawTitaniumBlockDataContainer.INSTANCE.getHolder().get()
        );

        // Decompressed raw
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            RawTitaniumBlockDataContainer.INSTANCE.getHolder().get(),
            AVPItemRegistry.INSTANCE.rawTitanium.get()
        );

        // Smelting
        AVPRecipeHelper.oreSmelting(
            recipeOutput,
            RecipeCategory.MISC,
            AVPItemRegistry.INSTANCE.rawTitanium,
            OreTitaniumBlockDataContainer.INSTANCE.getHolder(),
            AVPItemRegistry.INSTANCE.ingotTitanium
        );

        // TODO: Remove eventually.
        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            MetalTitaniumBlockSetDataContainer.INSTANCE.base.getHolder().get(),
            AVPItemRegistry.INSTANCE.ingotTitanium.get()
        );
    }

    private AVPTitaniumRecipes() {
        throw new UnsupportedOperationException();
    }
}
