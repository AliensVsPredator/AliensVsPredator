package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import org.avp.api.GameObject;
import org.avp.common.block.AVPIndustrialBlocks;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPArmorItems;
import org.avp.common.item.AVPItems;

public final class AVPArmorRecipes {

    public static void addArmorRecipes(RecipeOutput recipeOutput) {
        addAluminumArmorRecipes(recipeOutput);
        addMK50ArmorRecipes(recipeOutput);
        addOrioniteArmorRecipes(recipeOutput);
        addPressureArmorRecipes(recipeOutput);
        addTacticalArmorRecipes(recipeOutput);
        addXenomorphChitinArmorRecipes(recipeOutput);
    }

    private static void addStandardArmorRecipes(
        RecipeOutput recipeOutput,
        GameObject<Item> baseIngredientGameObject,
        GameObject<Item> helmetGameObject,
        GameObject<Item> chestplateGameObject,
        GameObject<Item> leggingsGameObject,
        GameObject<Item> bootsGameObject
    ) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplateGameObject)
            .defineAndUnlockIfHas('A', baseIngredientGameObject)
            .pattern("A A")
            .pattern("AAA")
            .pattern("AAA")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, bootsGameObject)
            .defineAndUnlockIfHas('A', baseIngredientGameObject)
            .pattern("A A")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmetGameObject)
            .defineAndUnlockIfHas('A', baseIngredientGameObject)
            .pattern("AAA")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggingsGameObject)
            .defineAndUnlockIfHas('A', baseIngredientGameObject)
            .pattern("AAA")
            .pattern("A A")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addAluminumArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(
            recipeOutput,
            AVPItems.INGOT_ALUMINUM,
            AVPArmorItems.ALUMINUM_HELMET,
            AVPArmorItems.ALUMINUM_BODY,
            AVPArmorItems.ALUMINUM_LEGGINGS,
            AVPArmorItems.ALUMINUM_BOOTS
        );
    }

    private static void addMK50ArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.MK50_BODY)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.GLASS)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.MK50_BOOTS)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.GLASS)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.MK50_HELMET)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.GLASS)
            .defineAndUnlockIfHas('C', AVPItems.CARBON)
            .defineAndUnlockIfHas('D', AVPItems.SILICA)
            .pattern("AAA")
            .pattern("B B")
            .pattern("DCD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.MK50_LEGGINGS)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.GLASS)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addOrioniteArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(
            recipeOutput,
            AVPItems.INGOT_ORIONITE,
            AVPArmorItems.ORIONITE_HELMET,
            AVPArmorItems.ORIONITE_BODY,
            AVPArmorItems.ORIONITE_LEGGINGS,
            AVPArmorItems.ORIONITE_BOOTS
        );
    }

    private static void addPressureArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.PRESSURE_BODY)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.GLASS)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.PRESSURE_BOOTS)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.GLASS)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.PRESSURE_HELMET)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.GLASS)
            .defineAndUnlockIfHas('C', AVPItems.CARBON)
            .defineAndUnlockIfHas('D', AVPItems.SILICA)
            .pattern("AAA")
            .pattern("B B")
            .pattern("DCD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.PRESSURE_LEGGINGS)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.GLASS)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addTacticalArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.TACTICAL_BODY)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.TACTICAL_BOOTS)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.TACTICAL_LEGGINGS)
            .defineAndUnlockIfHas('A', AVPItems.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addXenomorphChitinArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(
            recipeOutput,
            AVPItems.XENOMORPH_CHITIN,
            AVPArmorItems.XENOMORPH_HELMET,
            AVPArmorItems.XENOMORPH_BODY,
            AVPArmorItems.XENOMORPH_LEGGINGS,
            AVPArmorItems.XENOMORPH_BOOTS
        );
    }

    private AVPArmorRecipes() {
        throw new UnsupportedOperationException();
    }
}
