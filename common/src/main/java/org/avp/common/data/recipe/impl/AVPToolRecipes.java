package org.avp.common.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import org.avp.api.registry.holder.BLHolder;
import org.avp.common.data.recipe.AVPShapedRecipeBuilder;
import org.avp.common.registry.item.AVPItemRegistry;
import org.avp.common.registry.item.AVPToolItemRegistry;

public final class AVPToolRecipes {

    public static void addToolRecipes(RecipeOutput recipeOutput) {
        addToolSetRecipes(
            recipeOutput,
            AVPItemRegistry.INSTANCE.ingotAluminum,
            AVPToolItemRegistry.INSTANCE.aluminumAxe,
            AVPToolItemRegistry.INSTANCE.aluminumHoe,
            AVPToolItemRegistry.INSTANCE.aluminumPickaxe,
            AVPToolItemRegistry.INSTANCE.aluminumShovel,
            AVPToolItemRegistry.INSTANCE.aluminumSword
        );
        addToolSetRecipes(
            recipeOutput,
            AVPItemRegistry.INSTANCE.ingotTitanium,
            AVPToolItemRegistry.INSTANCE.titaniumAxe,
            AVPToolItemRegistry.INSTANCE.titaniumHoe,
            AVPToolItemRegistry.INSTANCE.titaniumPickaxe,
            AVPToolItemRegistry.INSTANCE.titaniumShovel,
            AVPToolItemRegistry.INSTANCE.titaniumSword
        );
        addToolSetRecipes(
            recipeOutput,
            AVPItemRegistry.INSTANCE.ingotOrionite,
            AVPToolItemRegistry.INSTANCE.orioniteAxe,
            AVPToolItemRegistry.INSTANCE.orioniteHoe,
            AVPToolItemRegistry.INSTANCE.orionitePickaxe,
            AVPToolItemRegistry.INSTANCE.orioniteShovel,
            AVPToolItemRegistry.INSTANCE.orioniteSword
        );
    }

    private static void addToolSetRecipes(
        RecipeOutput recipeOutput,
        BLHolder<Item> baseIngredient,
        BLHolder<Item> axeItemHolder,
        BLHolder<Item> hoeItemHolder,
        BLHolder<Item> pickaxeItemHolder,
        BLHolder<Item> shovelItemHolder,
        BLHolder<Item> swordItemHolder
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
