package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import org.avp.api.GameObject;
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
        GameObject<Item> baseIngredient,
        GameObject<Item> axeItemGameObject,
        GameObject<Item> hoeItemGameObject,
        GameObject<Item> pickaxeItemGameObject,
        GameObject<Item> shovelItemGameObject,
        GameObject<Item> swordItemGameObject
    ) {
        AVPShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axeItemGameObject)
            .defineAndUnlockIfHas('A', baseIngredient)
            .defineAndUnlockIfHas('B', Items.STICK)
            .pattern("AA")
            .pattern("AB")
            .pattern(" B")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hoeItemGameObject)
            .defineAndUnlockIfHas('A', baseIngredient)
            .defineAndUnlockIfHas('B', Items.STICK)
            .pattern("AA")
            .pattern(" B")
            .pattern(" B")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxeItemGameObject)
            .defineAndUnlockIfHas('A', baseIngredient)
            .defineAndUnlockIfHas('B', Items.STICK)
            .pattern("AAA")
            .pattern(" B ")
            .pattern(" B ")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovelItemGameObject)
            .defineAndUnlockIfHas('A', baseIngredient)
            .defineAndUnlockIfHas('B', Items.STICK)
            .pattern("A")
            .pattern("B")
            .pattern("B")
            .save(recipeOutput);
        AVPShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, swordItemGameObject)
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
