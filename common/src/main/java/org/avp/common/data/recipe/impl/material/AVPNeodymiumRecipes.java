package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.registry.block.AVPOreBlockRegistry;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPNeodymiumRecipes {

    public static void addNeodymiumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.neodymium.get(),
            AVPOreBlockRegistry.INSTANCE.neodymiumBlock.get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlockRegistry.INSTANCE.neodymiumBlock.get(),
            AVPItemRegistry.INSTANCE.neodymium.get()
        );
    }

    private AVPNeodymiumRecipes() {
        throw new UnsupportedOperationException();
    }
}
