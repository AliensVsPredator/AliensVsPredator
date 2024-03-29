package org.avp.common.data.recipes;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import org.avp.api.GameObject;
import org.avp.common.AVPConstants;

/**
 * @author Boston Vanseghi
 */
public final class AVPRecipeHelper {

    public static void compressedBlockRecipe(
        RecipeOutput recipeOutput,
        RecipeCategory recipeCategory,
        ItemLike input,
        ItemLike output
    ) {
        ShapedRecipeBuilder.shaped(recipeCategory, output)
            .define('A', input)
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .unlockedBy("has_item", AVPRecipeProvider.has(input))
            .save(recipeOutput);
    }

    public static void decompressedItemRecipe(
        RecipeOutput recipeOutput,
        RecipeCategory recipeCategory,
        ItemLike input,
        ItemLike output
    ) {
        ShapelessRecipeBuilder.shapeless(recipeCategory, output, 9)
            .requires(input)
            .unlockedBy("has_block", AVPRecipeProvider.has(input))
            .save(recipeOutput);
    }

    public static void oreSmelting(
        RecipeOutput recipeOutput,
        RecipeCategory recipeCategory,
        GameObject<Item> oreItemGameObject,
        GameObject<Block> oreBlockGameObject,
        GameObject<Item> outputGameObject
    ) {
        var oreItemName = oreItemGameObject.getResourceLocation().getPath();
        var oreBlockName = oreBlockGameObject.getResourceLocation().getPath();
        var outputItemName = outputGameObject.getResourceLocation().getPath();
        var oreItemIngredient = Ingredient.of(oreItemGameObject.get());
        var oreBlockIngredient = Ingredient.of(oreBlockGameObject.get());
        var oreItem = oreItemGameObject.get();
        var oreBlock = oreBlockGameObject.get();
        var output = outputGameObject.get();
        var xp = 0.7F;
        var cookTime = 200;

        SimpleCookingRecipeBuilder.smelting(oreItemIngredient, recipeCategory, output, xp, cookTime)
            .unlockedBy("has_raw_item", AVPRecipeProvider.has(oreItem))
            .save(recipeOutput, AVPConstants.MOD_ID + ":" + outputItemName + "_from_smelting_" + oreItemName);

        SimpleCookingRecipeBuilder.smelting(oreBlockIngredient, recipeCategory, output, xp, cookTime)
            .unlockedBy("has_ore", AVPRecipeProvider.has(oreBlock))
            .save(recipeOutput, AVPConstants.MOD_ID + ":" + outputItemName + "_from_smelting_" + oreBlockName);

        SimpleCookingRecipeBuilder.blasting(oreItemIngredient, recipeCategory, output, xp, cookTime / 2)
            .unlockedBy("has_raw_item", AVPRecipeProvider.has(oreItem))
            .save(recipeOutput, AVPConstants.MOD_ID + ":" + outputItemName + "_from_blasting_" + oreItemName);

        SimpleCookingRecipeBuilder.blasting(oreBlockIngredient, recipeCategory, output, xp, cookTime / 2)
            .unlockedBy("has_ore", AVPRecipeProvider.has(oreBlock))
            .save(recipeOutput, AVPConstants.MOD_ID + ":" + outputItemName + "_from_blasting_" + oreBlockName);
    }

    private AVPRecipeHelper() {
        throw new UnsupportedOperationException();
    }
}
