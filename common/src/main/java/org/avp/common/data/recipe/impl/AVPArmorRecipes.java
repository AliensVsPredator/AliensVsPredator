package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import org.avp.api.Holder;
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
        addTitaniumArmorRecipes(recipeOutput);
        addXenomorphChitinArmorRecipes(recipeOutput);
    }

    private static void addStandardArmorRecipes(
        RecipeOutput recipeOutput,
        Holder<Item> baseIngredientHolder,
        Holder<Item> helmetHolder,
        Holder<Item> chestplateHolder,
        Holder<Item> leggingsHolder,
        Holder<Item> bootsHolder
    ) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplateHolder)
            .defineAndUnlockIfHas('A', baseIngredientHolder)
            .pattern("A A")
            .pattern("AAA")
            .pattern("AAA")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, bootsHolder)
            .defineAndUnlockIfHas('A', baseIngredientHolder)
            .pattern("A A")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmetHolder)
            .defineAndUnlockIfHas('A', baseIngredientHolder)
            .pattern("AAA")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggingsHolder)
            .defineAndUnlockIfHas('A', baseIngredientHolder)
            .pattern("AAA")
            .pattern("A A")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addAluminumArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(
            recipeOutput,
            AVPItems.INSTANCE.ingotAluminum,
            AVPArmorItems.INSTANCE.aluminumHelmet,
            AVPArmorItems.INSTANCE.aluminumBody,
            AVPArmorItems.INSTANCE.aluminumLeggings,
            AVPArmorItems.INSTANCE.aluminumBoots
        );
    }

    private static void addMK50ArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.mk50Body)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.glass)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.mk50Boots)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.glass)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.mk50Helmet)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.glass)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.carbon)
            .defineAndUnlockIfHas('D', AVPItems.INSTANCE.silica)
            .pattern("AAA")
            .pattern("B B")
            .pattern("DCD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.mk50Leggings)
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.glass)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addOrioniteArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(
            recipeOutput,
            AVPItems.INSTANCE.ingotOrionite,
            AVPArmorItems.INSTANCE.orioniteHelmet,
            AVPArmorItems.INSTANCE.orioniteBody,
            AVPArmorItems.INSTANCE.orioniteLeggings,
            AVPArmorItems.INSTANCE.orioniteBoots
        );
    }

    private static void addPressureArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.pressureBody)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.glass)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.pressureBoots)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.glass)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.pressureHelmet)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.glass)
            .defineAndUnlockIfHas('C', AVPItems.INSTANCE.carbon)
            .defineAndUnlockIfHas('D', AVPItems.INSTANCE.silica)
            .pattern("AAA")
            .pattern("B B")
            .pattern("DCD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.pressureLeggings)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', AVPIndustrialBlocks.INSTANCE.glass)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addTacticalArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.tacticalBody)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.tacticalBoots)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItems.INSTANCE.tacticalLeggings)
            .defineAndUnlockIfHas('A', AVPItems.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addTitaniumArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(
            recipeOutput,
            AVPItems.INSTANCE.ingotTitanium,
            AVPArmorItems.INSTANCE.titaniumHelmet,
            AVPArmorItems.INSTANCE.titaniumBody,
            AVPArmorItems.INSTANCE.titaniumLeggings,
            AVPArmorItems.INSTANCE.titaniumBoots
        );
    }

    private static void addXenomorphChitinArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(
            recipeOutput,
            AVPItems.INSTANCE.xenomorphChitin,
            AVPArmorItems.INSTANCE.xenomorphHelmet,
            AVPArmorItems.INSTANCE.xenomorphBody,
            AVPArmorItems.INSTANCE.xenomorphLeggings,
            AVPArmorItems.INSTANCE.xenomorphBoots
        );
    }

    private AVPArmorRecipes() {
        throw new UnsupportedOperationException();
    }
}
