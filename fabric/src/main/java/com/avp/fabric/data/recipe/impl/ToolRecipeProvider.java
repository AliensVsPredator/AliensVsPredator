package com.avp.fabric.data.recipe.impl;

import com.avp.common.item.TempAVPItems;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class ToolRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createSteelToolsetRecipes(builder);
        createTitaniumToolsetRecipes(builder);
    }

    private static void createSteelToolsetRecipes(RecipeBuilder builder) {
        builder.shaped()
            .apply(RecipeTemplates.AXE.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(1, TempAVPItems.STEEL_AXE);
        builder.shaped()
            .apply(RecipeTemplates.HOE.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(1, TempAVPItems.STEEL_HOE);
        builder.shaped()
            .apply(RecipeTemplates.PICKAXE.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(1, TempAVPItems.STEEL_PICKAXE);
        builder.shaped()
            .apply(RecipeTemplates.SHOVEL.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(1, TempAVPItems.STEEL_SHOVEL);
        builder.shaped()
            .apply(RecipeTemplates.SWORD.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(1, TempAVPItems.STEEL_SWORD);
    }

    private static void createTitaniumToolsetRecipes(RecipeBuilder builder) {
        builder.shaped()
            .apply(RecipeTemplates.AXE.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(1, TempAVPItems.TITANIUM_AXE);
        builder.shaped()
            .apply(RecipeTemplates.HOE.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(1, TempAVPItems.TITANIUM_HOE);
        builder.shaped()
            .apply(RecipeTemplates.PICKAXE.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(1, TempAVPItems.TITANIUM_PICKAXE);
        builder.shaped()
            .apply(RecipeTemplates.SHOVEL.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(1, TempAVPItems.TITANIUM_SHOVEL);
        builder.shaped()
            .apply(RecipeTemplates.SWORD.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(1, TempAVPItems.TITANIUM_SWORD);
    }
}
