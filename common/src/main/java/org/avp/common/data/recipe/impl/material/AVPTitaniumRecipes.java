package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.data.block.ore.OreTitaniumBlockDataContainer;
import org.avp.common.data.block.raw.RawTitaniumBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

/**
 * @deprecated
 */
public final class AVPTitaniumRecipes {

    public static void addTitaniumRecipes(RecipeOutput recipeOutput) {
        var ingotHolder = AVPItemRegistry.INSTANCE.ingotTitanium;

//        AVPMetalRecipeHelper.addFullMetalSetRecipes(recipeOutput, ingotHolder, AVPBlockRegistry.INSTANCE.titanium);

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

        // Furnace recipes
    }

    private AVPTitaniumRecipes() {
        throw new UnsupportedOperationException();
    }
}
