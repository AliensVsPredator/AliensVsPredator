package org.avp.common.data.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.AVPConstants;

/**
 * @deprecated
 */
public final class AVPRecipeHelper {

    public static void compressedBlockRecipe(
        RecipeOutput recipeOutput,
        RecipeCategory recipeCategory,
        ItemLike input,
        ItemLike output
    ) {
        AVPShapedRecipeBuilder.shaped(recipeCategory, output)
            .defineAndUnlockIfHas('A', input)
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .save(recipeOutput);
    }

    public static void decompressedItemRecipe(
        RecipeOutput recipeOutput,
        RecipeCategory recipeCategory,
        ItemLike input,
        ItemLike output
    ) {
        AVPShapelessRecipeBuilder.shapeless(recipeCategory, output, 9)
            .requiresAndUnlockIfHas('A', input)
            .save(recipeOutput);
    }

    public static void oreSmelting(
        RecipeOutput recipeOutput,
        RecipeCategory recipeCategory,
        BLHolder<Item> oreItemHolder,
        BLHolder<Block> oreBlockHolder,
        BLHolder<Item> outputHolder
    ) {
        var oreItemName = oreItemHolder.getResourceLocation().getPath();
        var oreBlockName = oreBlockHolder.getResourceLocation().getPath();
        var outputItemName = outputHolder.getResourceLocation().getPath();
        var oreItemIngredient = Ingredient.of(oreItemHolder.get());
        var oreBlockIngredient = Ingredient.of(oreBlockHolder.get());
        var oreItem = oreItemHolder.get();
        var oreBlock = oreBlockHolder.get();
        var output = outputHolder.get();
        var xp = 0.7F;
        var cookTime = 200;

        // Raw chunk -> ingot
        // TODO: Move
        SimpleCookingRecipeBuilder.smelting(oreItemIngredient, recipeCategory, output, xp, cookTime)
            .unlockedBy("has_raw_item", AVPRecipeProvider.has(oreItem))
            .save(recipeOutput, AVPConstants.MOD_ID + ":" + outputItemName + "_from_smelting_" + oreItemName);

        // Ore block -> ingot
        // TODO: Move
        SimpleCookingRecipeBuilder.smelting(oreBlockIngredient, recipeCategory, output, xp, cookTime)
            .unlockedBy("has_ore", AVPRecipeProvider.has(oreBlock))
            .save(recipeOutput, AVPConstants.MOD_ID + ":" + outputItemName + "_from_smelting_" + oreBlockName);

        // Raw chunk -> ingot
        // TODO: Move
        SimpleCookingRecipeBuilder.blasting(oreItemIngredient, recipeCategory, output, xp, cookTime / 2)
            .unlockedBy("has_raw_item", AVPRecipeProvider.has(oreItem))
            .save(recipeOutput, AVPConstants.MOD_ID + ":" + outputItemName + "_from_blasting_" + oreItemName);

        // Ore block -> ingot
        // TODO: Move
        SimpleCookingRecipeBuilder.blasting(oreBlockIngredient, recipeCategory, output, xp, cookTime / 2)
            .unlockedBy("has_ore", AVPRecipeProvider.has(oreBlock))
            .save(recipeOutput, AVPConstants.MOD_ID + ":" + outputItemName + "_from_blasting_" + oreBlockName);
    }

    private AVPRecipeHelper() {
        throw new UnsupportedOperationException();
    }
}
