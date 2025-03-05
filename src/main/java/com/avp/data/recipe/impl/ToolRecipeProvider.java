package com.avp.data.recipe.impl;

import com.avp.common.item.AVPItems;
import com.avp.data.recipe.RecipeTemplates;
import com.avp.data.recipe.builder.RecipeBuilder;

public class ToolRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createSteelToolsetRecipes(builder);
        createTitaniumToolsetRecipes(builder);
    }

    private static void createSteelToolsetRecipes(RecipeBuilder builder) {
        builder.shaped()
            .apply(RecipeTemplates.AXE.apply(AVPItems.STEEL_INGOT))
            .into(1, AVPItems.STEEL_AXE);
        builder.shaped()
            .apply(RecipeTemplates.HOE.apply(AVPItems.STEEL_INGOT))
            .into(1, AVPItems.STEEL_HOE);
        builder.shaped()
            .apply(RecipeTemplates.PICKAXE.apply(AVPItems.STEEL_INGOT))
            .into(1, AVPItems.STEEL_PICKAXE);
        builder.shaped()
            .apply(RecipeTemplates.SHOVEL.apply(AVPItems.STEEL_INGOT))
            .into(1, AVPItems.STEEL_SHOVEL);
        builder.shaped()
            .apply(RecipeTemplates.SWORD.apply(AVPItems.STEEL_INGOT))
            .into(1, AVPItems.STEEL_SWORD);
    }

    private static void createTitaniumToolsetRecipes(RecipeBuilder builder) {
        builder.shaped()
            .apply(RecipeTemplates.AXE.apply(AVPItems.TITANIUM_INGOT))
            .into(1, AVPItems.TITANIUM_AXE);
        builder.shaped()
            .apply(RecipeTemplates.HOE.apply(AVPItems.TITANIUM_INGOT))
            .into(1, AVPItems.TITANIUM_HOE);
        builder.shaped()
            .apply(RecipeTemplates.PICKAXE.apply(AVPItems.TITANIUM_INGOT))
            .into(1, AVPItems.TITANIUM_PICKAXE);
        builder.shaped()
            .apply(RecipeTemplates.SHOVEL.apply(AVPItems.TITANIUM_INGOT))
            .into(1, AVPItems.TITANIUM_SHOVEL);
        builder.shaped()
            .apply(RecipeTemplates.SWORD.apply(AVPItems.TITANIUM_INGOT))
            .into(1, AVPItems.TITANIUM_SWORD);
    }
}
