package com.avp.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPItems;
import com.avp.data.recipe.RecipeConstants;
import com.avp.data.recipe.builder.RecipeBuilder;

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

        builder.shaped()
                .withCategory(RecipeCategory.MISC)
                .define('I', AVPItems.RAW_ROYAL_JELLY)
                .pattern("III")
                .pattern("III")
                .pattern("III")
                .into(1, AVPBlocks.ROYAL_JELLY_BLOCK);

        builder.shapeless()
                .withCategory(RecipeCategory.MISC)
                .requires(1, AVPBlocks.ROYAL_JELLY_BLOCK)
                .into(9, AVPItems.RAW_ROYAL_JELLY);

        builder.shapeless()
                .withCategory(RecipeCategory.MISC)
                .requires(1, Items.POISONOUS_POTATO)
                .requires(1, AVPItems.RAW_ROYAL_JELLY)
                .into(1, AVPItems.POISON_JELLY);

        builder.shaped()
                .withCategory(RecipeCategory.MISC)
                .define('C', Items.CLOCK)
                .define('S', AVPItems.LED_DISPLAY)
                .define('P', AVPItems.CPU)
                .define('L', AVPItems.LEAD_INGOT)
                .define('N', AVPItems.NEODYMIUM_MAGNET)
                .define('T', Items.TNT)
                .pattern("CSP")
                .pattern("LNL")
                .pattern("TTT")
                .into(1, AVPBlocks.NUKE_BLOCK);

        builder.shaped()
                .withCategory(RecipeCategory.MISC)
                .define('S', Items.SMOOTH_STONE)
                .define('F', Items.FURNACE)
                .define('I', Items.IRON_INGOT)
                .pattern("SSS")
                .pattern("SFS")
                .pattern("III")
                .into(1, AVPBlocks.INDUSTRIAL_FURNACE)

    }
}
