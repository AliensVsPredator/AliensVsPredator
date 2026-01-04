package com.blib.fabric.data.recipe.impl.vanilla;

import com.compatibility.CommonItemTags;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;

import com.blib.fabric.data.recipe.builder.RecipeBuilder;

public class VanillaMiscellaneousRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', CommonItemTags.DUSTS_COAL)
            .define('B', Items.BLAZE_POWDER)
            .pattern("AAA")
            .pattern("ABA")
            .pattern("AAA")
            .into(8, Items.GUNPOWDER);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, Items.PAPER)
            .requires(1, CommonItemTags.STRINGS)
            .into(1, Items.NAME_TAG);
    }
}
