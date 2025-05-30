package com.avp.fabric.data.recipe.impl.vanilla;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import com.avp.common.registry.init.item.AVPBlockItems;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class VanillaChestRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        createChestRecipes(builder);
    }

    private static void createChestRecipes(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.DECORATIONS)
            .define('#', AVPItems.LEAD_INGOT)
            .define('C', Items.CHEST)
            .pattern("###")
            .pattern("#C#")
            .pattern("###")
            .into(1, AVPBlockItems.LEAD_CHEST);

        builder.shaped()
            .withCategory(RecipeCategory.DECORATIONS)
            .define('#', AVPItems.STEEL_INGOT)
            .define('C', Items.CHEST)
            .pattern("###")
            .pattern("#C#")
            .pattern("###")
            .into(1, AVPBlockItems.AMMO_CHEST);
    }
}
