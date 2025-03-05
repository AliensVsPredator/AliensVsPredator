package com.avp.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPItems;
import com.avp.data.recipe.RecipeConstants;
import com.avp.data.recipe.RecipeTemplates;
import com.avp.data.recipe.builder.RecipeBuilder;
import com.avp.data.recipe.util.RecipeUtil;

public class MetalRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, AVPItems.CARBON_DUST)
            .requires(1, Items.RAW_IRON)
            .into(1, AVPItems.RAW_CRUDE_IRON);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, AVPItems.RAW_BAUXITE)
            .requires(1, AVPItems.CARBON_DUST)
            .requires(1, Items.RAW_IRON)
            .into(2, AVPItems.RAW_FERROBAUXITE);

        // Steel can only be blasted.
        builder.blast(AVPItems.RAW_CRUDE_IRON)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.RARE_SMELT_EXPERIENCE)
            .into(AVPItems.STEEL_INGOT);

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, AVPItems.RAW_ZINC)
            .requires(1, Items.RAW_COPPER)
            .into(2, AVPItems.RAW_BRASS);

        builder.shaped()
            .withCategory(RecipeCategory.MISC)
            .define('A', AVPItems.AUTUNITE_DUST)
            .define('T', AVPItems.TITANIUM_INGOT)
            .pattern(" A ")
            .pattern("ATA")
            .pattern(" A ")
            .into(1, AVPItems.URANIUM_INGOT);

        createSmeltAndBlastRecipes(builder, AVPBlocks.BAUXITE_ORE, AVPItems.ALUMINUM_INGOT);
        createSmeltAndBlastRecipes(builder, AVPBlocks.GALENA_ORE, AVPItems.LEAD_INGOT);
        createSmeltAndBlastRecipes(builder, AVPBlocks.MONAZITE_ORE, AVPItems.NEODYMIUM_MAGNET);
        createSmeltAndBlastRecipes(builder, AVPBlocks.DEEPSLATE_TITANIUM_ORE, AVPItems.TITANIUM_INGOT);
        createSmeltAndBlastRecipes(builder, AVPBlocks.ZINC_ORE, AVPItems.ZINC_INGOT);
        createSmeltAndBlastRecipes(builder, AVPBlocks.DEEPSLATE_ZINC_ORE, AVPItems.ZINC_INGOT);

        createSmeltAndBlastRecipes(builder, AVPItems.RAW_BAUXITE, AVPItems.ALUMINUM_INGOT);
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_BRASS, AVPItems.BRASS_INGOT);
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_FERROBAUXITE, AVPItems.FERROALUMINUM_INGOT);
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_GALENA, AVPItems.LEAD_INGOT);
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_MONAZITE, AVPItems.NEODYMIUM_MAGNET);
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_TITANIUM, AVPItems.TITANIUM_INGOT);
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_ZINC, AVPItems.ZINC_INGOT);

        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.ALUMINUM_INGOT, AVPBlocks.ALUMINUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.BRASS_INGOT, AVPBlocks.BRASS_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.FERROALUMINUM_INGOT, AVPBlocks.FERROALUMINUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.LEAD_INGOT, AVPBlocks.LEAD_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.STEEL_INGOT, AVPBlocks.STEEL_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.TITANIUM_INGOT, AVPBlocks.TITANIUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.URANIUM_INGOT, AVPBlocks.URANIUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.ZINC_INGOT, AVPBlocks.ZINC_BLOCK);

        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.AUTUNITE_DUST, AVPBlocks.AUTUNITE_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.LITHIUM_DUST, AVPBlocks.LITHIUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_BAUXITE, AVPBlocks.RAW_BAUXITE_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_GALENA, AVPBlocks.RAW_GALENA_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_MONAZITE, AVPBlocks.RAW_MONAZITE_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_SILICA, AVPBlocks.RAW_SILICA_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_TITANIUM, AVPBlocks.RAW_TITANIUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_ZINC, AVPBlocks.RAW_ZINC_BLOCK);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BARS_BLOCK.apply(AVPItems.STEEL_INGOT))
            .into(16, AVPBlocks.STEEL_BARS);

        createFerroaluminumBlockVariantRecipes(builder);
        createSteelBlockVariantRecipes(builder);
        createTitaniumBlockVariantRecipes(builder);
    }

    private static void createFerroaluminumBlockVariantRecipes(RecipeBuilder builder) {
        var ferroaluminumBaseBuilder = builder.stonecut(AVPBlocks.FERROALUMINUM_BLOCK)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        ferroaluminumBaseBuilder.into(4, AVPBlocks.CHISELED_FERROALUMINUM);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.CUT_FERROALUMINUM);
        ferroaluminumBaseBuilder.into(8, AVPBlocks.CUT_FERROALUMINUM_SLAB);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.CUT_FERROALUMINUM_STAIRS);
        ferroaluminumBaseBuilder.into(16, AVPBlocks.FERROALUMINUM_CHAIN_FENCE);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.FERROALUMINUM_COLUMN);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.FERROALUMINUM_FASTENED_SIDING);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.FERROALUMINUM_FASTENED_STANDING);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.FERROALUMINUM_GRATE);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.FERROALUMINUM_PLATING);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.FERROALUMINUM_SIDING);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.FERROALUMINUM_STANDING);
        ferroaluminumBaseBuilder.into(4, AVPBlocks.FERROALUMINUM_TREAD);

        var cutFerroaluminumBuilder = builder.stonecut(AVPBlocks.CUT_FERROALUMINUM)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutFerroaluminumBuilder.into(2, AVPBlocks.CUT_FERROALUMINUM_SLAB);
        cutFerroaluminumBuilder.into(1, AVPBlocks.CUT_FERROALUMINUM_STAIRS);
    }

    private static void createSteelBlockVariantRecipes(RecipeBuilder builder) {
        var steelBaseBuilder = builder.stonecut(AVPBlocks.STEEL_BLOCK)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        steelBaseBuilder.into(4, AVPBlocks.CHISELED_STEEL);
        steelBaseBuilder.into(4, AVPBlocks.CUT_STEEL);
        steelBaseBuilder.into(8, AVPBlocks.CUT_STEEL_SLAB);
        steelBaseBuilder.into(4, AVPBlocks.CUT_STEEL_STAIRS);
        steelBaseBuilder.into(16, AVPBlocks.STEEL_CHAIN_FENCE);
        steelBaseBuilder.into(4, AVPBlocks.STEEL_COLUMN);
        steelBaseBuilder.into(4, AVPBlocks.STEEL_FASTENED_SIDING);
        steelBaseBuilder.into(4, AVPBlocks.STEEL_FASTENED_STANDING);
        steelBaseBuilder.into(4, AVPBlocks.STEEL_GRATE);
        steelBaseBuilder.into(4, AVPBlocks.STEEL_PLATING);
        steelBaseBuilder.into(4, AVPBlocks.STEEL_SIDING);
        steelBaseBuilder.into(4, AVPBlocks.STEEL_STANDING);
        steelBaseBuilder.into(4, AVPBlocks.STEEL_TREAD);

        var cutSteelBuilder = builder.stonecut(AVPBlocks.CUT_STEEL)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutSteelBuilder.into(2, AVPBlocks.CUT_STEEL_SLAB);
        cutSteelBuilder.into(1, AVPBlocks.CUT_STEEL_STAIRS);
    }

    private static void createTitaniumBlockVariantRecipes(RecipeBuilder builder) {
        var titaniumBaseBuilder = builder.stonecut(AVPBlocks.TITANIUM_BLOCK)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        titaniumBaseBuilder.into(4, AVPBlocks.CHISELED_TITANIUM);
        titaniumBaseBuilder.into(4, AVPBlocks.CUT_TITANIUM);
        titaniumBaseBuilder.into(8, AVPBlocks.CUT_TITANIUM_SLAB);
        titaniumBaseBuilder.into(4, AVPBlocks.CUT_TITANIUM_STAIRS);
        titaniumBaseBuilder.into(16, AVPBlocks.TITANIUM_CHAIN_FENCE);
        titaniumBaseBuilder.into(4, AVPBlocks.TITANIUM_COLUMN);
        titaniumBaseBuilder.into(4, AVPBlocks.TITANIUM_FASTENED_SIDING);
        titaniumBaseBuilder.into(4, AVPBlocks.TITANIUM_FASTENED_STANDING);
        titaniumBaseBuilder.into(4, AVPBlocks.TITANIUM_GRATE);
        titaniumBaseBuilder.into(4, AVPBlocks.TITANIUM_PLATING);
        titaniumBaseBuilder.into(4, AVPBlocks.TITANIUM_SIDING);
        titaniumBaseBuilder.into(4, AVPBlocks.TITANIUM_STANDING);
        titaniumBaseBuilder.into(4, AVPBlocks.TITANIUM_TREAD);

        var cutTitaniumBuilder = builder.stonecut(AVPBlocks.CUT_TITANIUM)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutTitaniumBuilder.into(2, AVPBlocks.CUT_TITANIUM_SLAB);
        cutTitaniumBuilder.into(1, AVPBlocks.CUT_TITANIUM_STAIRS);
    }

    private static void createSmeltAndBlastRecipes(RecipeBuilder builder, ItemLike input, ItemLike output) {
        builder.smelt(input)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.RARE_SMELT_EXPERIENCE)
            .into(output);

        builder.blast(input)
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.RARE_SMELT_EXPERIENCE)
            .into(output);
    }
}
