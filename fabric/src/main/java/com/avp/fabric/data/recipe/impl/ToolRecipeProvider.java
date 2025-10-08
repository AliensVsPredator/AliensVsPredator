package com.avp.fabric.data.recipe.impl;

import com.avp.common.registry.init.item.AVPItems;
import com.avp.fabric.data.compatibility.common.CommonConstants;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class ToolRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createSteelToolsetRecipes(builder);
        createTitaniumToolsetRecipes(builder);
    }

    private static void createSteelToolsetRecipes(RecipeBuilder builder) {
        builder.shaped()
            .apply(RecipeTemplates.AXE_TAG_FRIENDLY.apply(CommonConstants.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_AXE);
        builder.shaped()
            .apply(RecipeTemplates.HOE_TAG_FRIENDLY.apply(CommonConstants.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_HOE);
        builder.shaped()
            .apply(RecipeTemplates.PICKAXE_TAG_FRIENDLY.apply(CommonConstants.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_PICKAXE);
        builder.shaped()
            .apply(RecipeTemplates.SHOVEL_TAG_FRIENDLY.apply(CommonConstants.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_SHOVEL);
        builder.shaped()
            .apply(RecipeTemplates.SWORD_TAG_FRIENDLY.apply(CommonConstants.INGOTS_STEEL))
            .into(1, AVPItems.STEEL_SWORD);
    }

    private static void createTitaniumToolsetRecipes(RecipeBuilder builder) {
        builder.shaped()
            .apply(RecipeTemplates.AXE_TAG_FRIENDLY.apply(CommonConstants.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_AXE);
        builder.shaped()
            .apply(RecipeTemplates.HOE_TAG_FRIENDLY.apply(CommonConstants.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_HOE);
        builder.shaped()
            .apply(RecipeTemplates.PICKAXE_TAG_FRIENDLY.apply(CommonConstants.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_PICKAXE);
        builder.shaped()
            .apply(RecipeTemplates.SHOVEL_TAG_FRIENDLY.apply(CommonConstants.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_SHOVEL);
        builder.shaped()
            .apply(RecipeTemplates.SWORD_TAG_FRIENDLY.apply(CommonConstants.INGOTS_TITANIUM))
            .into(1, AVPItems.TITANIUM_SWORD);
    }
}
