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
            AVPItems.INSTANCE.INGOT_ALUMINUM,
            AVPArmorItems.INSTANCE.ALUMINUM_HELMET,
            AVPArmorItems.INSTANCE.ALUMINUM_BODY,
            AVPArmorItems.INSTANCE.ALUMINUM_LEGGINGS,
            AVPArmorItems.INSTANCE.ALUMINUM_BOOTS
        );
    }

    private static void addMK50ArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.MK50_BODY)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.GLASS)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.MK50_BOOTS)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.GLASS)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.MK50_HELMET)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.GLASS)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.CARBON)
            .defineAndUnlockIfHas('D', AVPItems.INSTANCE.SILICA)
            .pattern("AAA")
            .pattern("B B")
            .pattern("DCD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.MK50_LEGGINGS)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.GLASS)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addOrioniteArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(
            recipeOutput,
            AVPItems.INSTANCE.INGOT_ORIONITE,
            AVPArmorItems.INSTANCE.ORIONITE_HELMET,
            AVPArmorItems.INSTANCE.ORIONITE_BODY,
            AVPArmorItems.INSTANCE.ORIONITE_LEGGINGS,
            AVPArmorItems.INSTANCE.ORIONITE_BOOTS
        );
    }

    private static void addPressureArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.PRESSURE_BODY)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.GLASS)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.PRESSURE_BOOTS)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.GLASS)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.PRESSURE_HELMET)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.GLASS)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.CARBON)
            .defineAndUnlockIfHas('D', AVPItems.INSTANCE.SILICA)
            .pattern("AAA")
            .pattern("B B")
            .pattern("DCD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.PRESSURE_LEGGINGS)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.GLASS)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addTacticalArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.TACTICAL_BODY)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.TACTICAL_BOOTS)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.TACTICAL_LEGGINGS)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.INGOT_ALUMINUM)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addXenomorphChitinArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(
            recipeOutput,
            AVPItems.INSTANCE.XENOMORPH_CHITIN,
            AVPArmorItems.INSTANCE.XENOMORPH_HELMET,
            AVPArmorItems.INSTANCE.XENOMORPH_BODY,
            AVPArmorItems.INSTANCE.XENOMORPH_LEGGINGS,
            AVPArmorItems.INSTANCE.XENOMORPH_BOOTS
        );
    }

    private AVPArmorRecipes() {
        throw new UnsupportedOperationException();
    }
}
