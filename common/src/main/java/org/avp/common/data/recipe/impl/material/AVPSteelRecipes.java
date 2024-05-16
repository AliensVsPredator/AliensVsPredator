package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.item.AVPItems;

public final class AVPSteelRecipes {

    public static void addSteelRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INSTANCE.ingotSteel.get(),
            AVPBlocks.INSTANCE.steelBlock.get()
        );

        // Smelting
        var steelOutput = AVPItems.INSTANCE.ingotSteel.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_INGOT), RecipeCategory.MISC, steelOutput, 0.7F, 100)
            .unlockedBy("has_iron_ingot", AVPRecipeProvider.has(Items.IRON_INGOT))
            .save(recipeOutput, AVPConstants.MOD_ID + ":ingot_steel_from_blasting_iron_ingot");

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPBlocks.INSTANCE.steelBlock.get(),
            AVPItems.INSTANCE.ingotSteel.get()
        );
    }

    private AVPSteelRecipes() {
        throw new UnsupportedOperationException();
    }
}
