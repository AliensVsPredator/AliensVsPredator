package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.item.AVPItems;
import com.avp.fabric.data.recipe.RecipeConstants;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;

public class MiscellaneousRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', ItemTags.COALS)
            .pattern("A")
            .into(2, AVPItems.CARBON_DUST);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', AVPItems.ALUMINUM_INGOT)
            .define('B', AVPItems.POLYMER)
            .define('C', Items.CHEST)
            .pattern("ABA")
            .pattern("BCB")
            .pattern("ABA")
            .into(1, AVPItems.ARMOR_CASE);

        builder.smelt(AVPItems.RESIN_BALL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(Items.SLIME_BALL);

        builder.smelt(AVPItems.NETHER_RESIN_BALL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(Items.SLIME_BALL);

        builder.smelt(Items.SLIME_BALL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.VERY_COMMON_SMELT_EXPERIENCE)
            .into(AVPItems.POLYMER);

        builder.blast(AVPBlocks.SILICA_GRAVEL)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.RARE_SMELT_EXPERIENCE)
            .into(AVPItems.RAW_SILICA);

        builder.shaped()
            .withCategory(RecipeCategory.COMBAT)
            .define('I', Items.IRON_INGOT)
            .define('N', Items.IRON_NUGGET)
            .pattern("INI")
            .pattern("NIN")
            .pattern("INI")
            .into(16, AVPBlocks.RAZOR_WIRE);
    }
}
