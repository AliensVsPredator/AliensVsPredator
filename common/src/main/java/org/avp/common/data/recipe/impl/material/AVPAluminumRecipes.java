package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.data.block.ore.OreBauxiteBlockDataContainer;
import org.avp.common.data.block.raw.RawBauxiteBlockDataContainer;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

/**
 * @deprecated
 */
public final class AVPAluminumRecipes {

    public static void addAluminumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.rawBauxite.get(),
            RawBauxiteBlockDataContainer.INSTANCE.getHolder().get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            RawBauxiteBlockDataContainer.INSTANCE.getHolder().get(),
            AVPItemRegistry.INSTANCE.rawBauxite.get()
        );

        // Smelting
        // TODO: Remove when items are migrated over.
        AVPRecipeHelper.oreSmelting(
            recipeOutput,
            RecipeCategory.MISC,
            AVPItemRegistry.INSTANCE.rawBauxite,
            OreBauxiteBlockDataContainer.INSTANCE.getHolder(),
            AVPItemRegistry.INSTANCE.ingotAluminum
        );
    }

    private AVPAluminumRecipes() {
        throw new UnsupportedOperationException();
    }
}
