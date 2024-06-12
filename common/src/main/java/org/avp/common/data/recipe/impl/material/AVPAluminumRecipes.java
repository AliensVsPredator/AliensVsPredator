package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.block.AVPBlockRegistry;
import org.avp.common.registry.block.AVPOreBlockRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPAluminumRecipes {

    public static void addAluminumRecipes(RecipeOutput recipeOutput) {
        var ingotHolder = AVPItemRegistry.INSTANCE.ingotAluminum;

        AVPMetalRecipeHelper.addFullMetalSetRecipes(recipeOutput, ingotHolder, AVPBlockRegistry.INSTANCE.aluminum);

        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.rawBauxite.get(),
            AVPOreBlockRegistry.INSTANCE.rawBauxiteBlock.get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlockRegistry.INSTANCE.rawBauxiteBlock.get(),
            AVPItemRegistry.INSTANCE.rawBauxite.get()
        );

        // Smelting
        AVPRecipeHelper.oreSmelting(
            recipeOutput,
            RecipeCategory.MISC,
            AVPItemRegistry.INSTANCE.rawBauxite,
            AVPOreBlockRegistry.INSTANCE.oreBauxite,
            AVPItemRegistry.INSTANCE.ingotAluminum
        );
    }

    private AVPAluminumRecipes() {
        throw new UnsupportedOperationException();
    }
}
