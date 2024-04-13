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
            AVPItems.INGOT_ALUMINUM,
            AVPToolItems.ALUMINUM_AXE,
            AVPToolItems.ALUMINUM_HOE,
            AVPToolItems.ALUMINUM_PICKAXE,
            AVPToolItems.ALUMINUM_SHOVEL,
            AVPToolItems.ALUMINUM_SWORD
        );
        addToolSetRecipes(
            recipeOutput,
            AVPItems.INGOT_TITANIUM,
            AVPToolItems.TITANIUM_AXE,
            AVPToolItems.TITANIUM_HOE,
            AVPToolItems.TITANIUM_PICKAXE,
            AVPToolItems.TITANIUM_SHOVEL,
            AVPToolItems.TITANIUM_SWORD
        );
        addToolSetRecipes(
            recipeOutput,
            AVPItems.INGOT_ORIONITE,
            AVPToolItems.ORIONITE_AXE,
            AVPToolItems.ORIONITE_HOE,
            AVPToolItems.ORIONITE_PICKAXE,
            AVPToolItems.ORIONITE_SHOVEL,
            AVPToolItems.ORIONITE_SWORD
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
