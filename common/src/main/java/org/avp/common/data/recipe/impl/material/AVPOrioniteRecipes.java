package org.avp.common.data.recipe.impl.material;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import org.avp.common.AVPConstants;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.recipe.AVPRecipeHelper;
import org.avp.common.data.recipe.AVPRecipeProvider;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPItems;

public final class AVPOrioniteRecipes {

    public static void addOrioniteRecipes(RecipeOutput recipeOutput) {
        // Compressed blocks
        AVPRecipeHelper.compressedBlockRecipe(
            recipeOutput,
            RecipeCategory.BUILDING_BLOCKS,
            AVPItems.INSTANCE.ingotOrionite.get(),
            AVPBlocks.INSTANCE.orioniteBlock.get()
        );

        // Titanium ore + veritanium shards = orionite sheet
        AVPShapedRecipeBuilder.shaped(RecipeCategory.MISC, AVPItems.INSTANCE.sheetOrionite)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.rawTitanium)
            .defineAndUnlockIfHas('B', AVPItems.INSTANCE.veritaniumShard)
            .pattern("AAA")
            .pattern("ABB")
            .pattern("BB ")
            .save(recipeOutput);

        // Blasting orionite sheet = orionite ingot
        var orioniteInput = AVPItems.INSTANCE.sheetOrionite.get();
        var orioniteOutput = AVPItems.INSTANCE.ingotOrionite.get();
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(orioniteInput), RecipeCategory.MISC, orioniteOutput, 0.7F, 100)
            .unlockedBy("has_sheet_orionite", AVPRecipeProvider.has(orioniteInput))
            .save(recipeOutput, AVPConstants.MOD_ID + ":ingot_orionite_from_blasting_sheet_orionite");

        // Decompressed items from blocks
        AVPRecipeHelper.decompressedItemRecipe(
            recipeOutput,
            RecipeCategory.MISC,
            AVPBlocks.INSTANCE.orioniteBlock.get(),
            AVPItems.INSTANCE.ingotOrionite.get()
        );
    }

    private AVPOrioniteRecipes() {
        throw new UnsupportedOperationException();
    }
}
