package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.block.AVPOreBlockRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPLithiumRecipes {

    public static void addLithiumRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.dustLithium.get(),
            AVPOreBlockRegistry.INSTANCE.lithiumBlock.get()
        );

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlockRegistry.INSTANCE.lithiumBlock.get(),
            AVPItemRegistry.INSTANCE.dustLithium.get()
        );
    }

    private AVPLithiumRecipes() {
        throw new UnsupportedOperationException();
    }
}
