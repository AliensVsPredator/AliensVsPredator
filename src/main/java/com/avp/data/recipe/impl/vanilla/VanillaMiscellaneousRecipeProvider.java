package com.avp.data.recipe.impl.vanilla;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import com.avp.common.item.AVPItems;
import com.avp.data.recipe.builder.RecipeBuilder;

public class VanillaMiscellaneousRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', AVPItems.CARBON_DUST)
            .define('B', Items.BLAZE_POWDER)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .into(8, Items.GUNPOWDER);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, Items.PAPER)
            .requires(1, Items.STRING)
            .into(1, Items.NAME_TAG);
    }
}
