package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import org.avp.common.registry.block.AVPBlockRegistry;
import org.avp.common.registry.block.AVPOreBlockRegistry;
import org.avp.common.data.recipe.AVPMetalRecipeHelper;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.registry.item.AVPItemRegistry;

public final class AVPTitaniumRecipes {

    public static void addTitaniumRecipes(RecipeOutput recipeOutput) {
        var ingotHolder = AVPItemRegistry.INSTANCE.ingotTitanium;

        AVPMetalRecipeHelper.addFullMetalSetRecipes(recipeOutput, ingotHolder, AVPBlockRegistry.INSTANCE.titanium);

        // Compressed raw
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItemRegistry.INSTANCE.rawTitanium.get(),
            AVPOreBlockRegistry.INSTANCE.rawTitaniumBlock.get()
        );

        // Decompressed raw
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPOreBlockRegistry.INSTANCE.rawTitaniumBlock.get(),
            AVPItemRegistry.INSTANCE.rawTitanium.get()
        );

        // Smelting
        AVPRecipeHelper.oreSmelting(
            recipeOutput,
            RecipeCategory.MISC,
            AVPItemRegistry.INSTANCE.rawTitanium,
            AVPOreBlockRegistry.INSTANCE.oreTitanium,
            AVPItemRegistry.INSTANCE.ingotTitanium
        );

        // Furnace recipes
    }

    private AVPTitaniumRecipes() {
        throw new UnsupportedOperationException();
    }
}
