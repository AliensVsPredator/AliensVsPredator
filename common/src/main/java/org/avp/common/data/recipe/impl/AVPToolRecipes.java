package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import org.avp.api.Holder;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.item.AVPItems;
import org.avp.common.item.AVPToolItems;

public final class AVPToolRecipes {

    public static void addToolRecipes(RecipeOutput recipeOutput) {
        addToolSetRecipes(
            recipeOutput,
            AVPItems.INSTANCE.INGOT_ALUMINUM,
            AVPToolItems.INSTANCE.ALUMINUM_AXE,
            AVPToolItems.INSTANCE.ALUMINUM_HOE,
            AVPToolItems.INSTANCE.ALUMINUM_PICKAXE,
            AVPToolItems.INSTANCE.ALUMINUM_SHOVEL,
            AVPToolItems.INSTANCE.ALUMINUM_SWORD
        );
        addToolSetRecipes(
            recipeOutput,
            AVPItems.INSTANCE.INGOT_TITANIUM,
            AVPToolItems.INSTANCE.TITANIUM_AXE,
            AVPToolItems.INSTANCE.TITANIUM_HOE,
            AVPToolItems.INSTANCE.TITANIUM_PICKAXE,
            AVPToolItems.INSTANCE.TITANIUM_SHOVEL,
            AVPToolItems.INSTANCE.TITANIUM_SWORD
        );
        addToolSetRecipes(
            recipeOutput,
            AVPItems.INSTANCE.INGOT_ORIONITE,
            AVPToolItems.INSTANCE.ORIONITE_AXE,
            AVPToolItems.INSTANCE.ORIONITE_HOE,
            AVPToolItems.INSTANCE.ORIONITE_PICKAXE,
            AVPToolItems.INSTANCE.ORIONITE_SHOVEL,
            AVPToolItems.INSTANCE.ORIONITE_SWORD
        );
    }

    private static void addToolSetRecipes(
        RecipeOutput recipeOutput,
        Holder<Item> baseIngredient,
        Holder<Item> axeItemHolder,
        Holder<Item> hoeItemHolder,
        Holder<Item> pickaxeItemHolder,
        Holder<Item> shovelItemHolder,
        Holder<Item> swordItemHolder
    ) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axeItemHolder)
            .defineAndUnlockIfHas('A', baseIngredient)
            .defineAndUnlockIfHas('B', Items.STICK)
            .pattern("AA")
            .pattern("AB")
            .pattern(" B")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hoeItemHolder)
            .defineAndUnlockIfHas('A', baseIngredient)
            .defineAndUnlockIfHas('B', Items.STICK)
            .pattern("AA")
            .pattern(" B")
            .pattern(" B")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxeItemHolder)
            .defineAndUnlockIfHas('A', baseIngredient)
            .defineAndUnlockIfHas('B', Items.STICK)
            .pattern("AAA")
            .pattern(" B ")
            .pattern(" B ")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovelItemHolder)
            .defineAndUnlockIfHas('A', baseIngredient)
            .defineAndUnlockIfHas('B', Items.STICK)
            .pattern("A")
            .pattern("B")
            .pattern("B")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, swordItemHolder)
            .defineAndUnlockIfHas('A', baseIngredient)
            .defineAndUnlockIfHas('B', Items.STICK)
            .pattern("A")
            .pattern("A")
            .pattern("B")
            .save(recipeOutput);
    }

    private AVPToolRecipes() {
        throw new UnsupportedOperationException();
    }
}
