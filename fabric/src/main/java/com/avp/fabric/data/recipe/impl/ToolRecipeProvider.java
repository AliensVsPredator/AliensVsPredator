package com.avp.fabric.data.recipe.impl;

import com.compat.CommonItemTags;

import com.avp.common.registry.init.item.AVPItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class ToolRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createSteelToolsetRecipes(builder);
        createTitaniumToolsetRecipes(builder);
    }

    private static void createSteelToolsetRecipes(RecipeBuilder builder) {
        builder.shaped()
            .apply(RecipeTemplates.AXE.apply(CommonItemTags.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_AXE);
        builder.shaped()
            .apply(RecipeTemplates.HOE.apply(CommonItemTags.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_HOE);
        builder.shaped()
            .apply(RecipeTemplates.PICKAXE.apply(CommonItemTags.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_PICKAXE);
        builder.shaped()
            .apply(RecipeTemplates.SHOVEL.apply(CommonItemTags.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_SHOVEL);
        builder.shaped()
            .apply(RecipeTemplates.SWORD.apply(CommonItemTags.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_SWORD);
    }

    private static void createTitaniumToolsetRecipes(RecipeBuilder builder) {
        builder.shaped()
            .apply(RecipeTemplates.AXE.apply(CommonItemTags.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_AXE);
        builder.shaped()
            .apply(RecipeTemplates.HOE.apply(CommonItemTags.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_HOE);
        builder.shaped()
            .apply(RecipeTemplates.PICKAXE.apply(CommonItemTags.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_PICKAXE);
        builder.shaped()
            .apply(RecipeTemplates.SHOVEL.apply(CommonItemTags.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_SHOVEL);
        builder.shaped()
            .apply(RecipeTemplates.SWORD.apply(CommonItemTags.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_SWORD);
    }
}
