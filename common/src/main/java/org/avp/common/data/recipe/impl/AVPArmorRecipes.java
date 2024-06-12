package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.registry.holder.ItemHolderArmorSet;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.data.tag.AVPItemTags;
import org.avp.common.registry.item.AVPArmorItemRegistry;
import org.avp.common.registry.item.AVPItemRegistry;

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
        BLHolder<Item> baseIngredientHolder,
        ItemHolderArmorSet itemHolderArmorSet
    ) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, itemHolderArmorSet.body())
            .defineAndUnlockIfHas('A', baseIngredientHolder)
            .pattern("A A")
            .pattern("AAA")
            .pattern("AAA")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, itemHolderArmorSet.boots())
            .defineAndUnlockIfHas('A', baseIngredientHolder)
            .pattern("A A")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, itemHolderArmorSet.helmet())
            .defineAndUnlockIfHas('A', baseIngredientHolder)
            .pattern("AAA")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, itemHolderArmorSet.leggings())
            .defineAndUnlockIfHas('A', baseIngredientHolder)
            .pattern("AAA")
            .pattern("A A")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addAluminumArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(recipeOutput, AVPItemRegistry.INSTANCE.ingotAluminum, AVPArmorItemRegistry.INSTANCE.aluminum);
    }

    private static void addMK50ArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.mk50.body())
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItemTags.INDUSTRIAL_GLASS)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.mk50.boots())
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItemTags.INDUSTRIAL_GLASS)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.mk50.helmet())
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItemTags.INDUSTRIAL_GLASS)
            .defineAndUnlockIfHas('C', AVPItemRegistry.INSTANCE.carbon)
            .defineAndUnlockIfHas('D', AVPItemRegistry.INSTANCE.silica)
            .pattern("AAA")
            .pattern("B B")
            .pattern("DCD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.mk50.leggings())
            .defineAndUnlockIfHas('A', Items.COPPER_INGOT)
            .defineAndUnlockIfHas('B', AVPItemTags.INDUSTRIAL_GLASS)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addOrioniteArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(recipeOutput, AVPItemRegistry.INSTANCE.ingotOrionite, AVPArmorItemRegistry.INSTANCE.orionite);
    }

    private static void addPressureArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.pressure.body())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', AVPItemTags.INDUSTRIAL_GLASS)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.pressure.boots())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', AVPItemTags.INDUSTRIAL_GLASS)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.pressure.helmet())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', AVPItemTags.INDUSTRIAL_GLASS)
            .defineAndUnlockIfHas('C', AVPItemRegistry.INSTANCE.carbon)
            .defineAndUnlockIfHas('D', AVPItemRegistry.INSTANCE.silica)
            .pattern("AAA")
            .pattern("B B")
            .pattern("DCD")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.pressure.leggings())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', AVPItemTags.INDUSTRIAL_GLASS)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addTacticalArmorRecipes(RecipeOutput recipeOutput) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.tactical.body())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("B B")
            .pattern("ABA")
            .pattern("BAB")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.tactical.boots())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.tactical.helmet())
            .defineAndUnlockIfHas('A', ItemTags.WOOL)
            .defineAndUnlockIfHas('B', AVPItemRegistry.INSTANCE.ingotSteel)
            .pattern("AAA")
            .pattern("B B")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, AVPArmorItemRegistry.INSTANCE.tactical.leggings())
            .defineAndUnlockIfHas('A', AVPItemRegistry.INSTANCE.ingotAluminum)
            .defineAndUnlockIfHas('B', ItemTags.WOOL)
            .pattern("BAB")
            .pattern("B B")
            .pattern("A A")
            .save(recipeOutput);
    }

    private static void addTitaniumArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(recipeOutput, AVPItemRegistry.INSTANCE.ingotTitanium, AVPArmorItemRegistry.INSTANCE.titanium);
    }

    private static void addXenomorphChitinArmorRecipes(RecipeOutput recipeOutput) {
        addStandardArmorRecipes(recipeOutput, AVPItemRegistry.INSTANCE.xenomorphChitin, AVPArmorItemRegistry.INSTANCE.xenomorphChitin);
    }

    private AVPArmorRecipes() {
        throw new UnsupportedOperationException();
    }
}
