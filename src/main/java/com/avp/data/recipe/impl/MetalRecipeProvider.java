package com.avp.data.recipe.impl;

import com.avp.common.block_item.AVPBlockItems;
import com.avp.data.recipe.builder.ShapedRecipeBuilder;
import com.avp.data.recipe.builder.ShapelessRecipeBuilder;
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

        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.BUTTON_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT))
                .into(2, AVPBlocks.FERROALUMINUM_BUTTON);
        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.BUTTON_BLOCK.apply(AVPItems.STEEL_INGOT))
                .into(2, AVPBlocks.STEEL_BUTTON);
        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.BUTTON_BLOCK.apply(AVPItems.TITANIUM_INGOT))
                .into(2, AVPBlocks.TITANIUM_BUTTON);
        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.DOOR_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT))
                .into(1, AVPBlocks.FERROALUMINUM_DOOR);
        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.DOOR_BLOCK.apply(AVPItems.STEEL_INGOT))
                .into(1, AVPBlocks.STEEL_DOOR);
        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.DOOR_BLOCK.apply(AVPItems.TITANIUM_INGOT))
                .into(1, AVPBlocks.TITANIUM_DOOR);

        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT))
                .into(1, AVPBlocks.FERROALUMINUM_TRAP_DOOR);
        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(AVPItems.STEEL_INGOT))
                .into(1, AVPBlocks.STEEL_TRAP_DOOR);
        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(AVPItems.TITANIUM_INGOT))
                .into(1, AVPBlocks.TITANIUM_TRAP_DOOR);

        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT))
                .into(1, AVPBlocks.FERROALUMINUM_PRESSURE_PLATE);
        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(AVPItems.STEEL_INGOT))
                .into(1, AVPBlocks.STEEL_PRESSURE_PLATE);
        builder.shaped()
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(AVPItems.TITANIUM_INGOT))
                .into(1, AVPBlocks.TITANIUM_PRESSURE_PLATE);

        // Add standard slab and stair crafting recipes
        createStandardSlabRecipe(builder.shaped(), AVPBlocks.FERROALUMINUM_BLOCK, AVPBlocks.FERROALUMINUM_SLAB);
        createStandardStairRecipe(builder.shaped(), AVPBlocks.FERROALUMINUM_BLOCK, AVPBlocks.FERROALUMINUM_STAIRS);

        createStandardSlabRecipe(builder.shaped(), AVPBlocks.STEEL_BLOCK, AVPBlocks.STEEL_SLAB);
        createStandardStairRecipe(builder.shaped(), AVPBlocks.STEEL_BLOCK, AVPBlocks.STEEL_STAIRS);

        createStandardSlabRecipe(builder.shaped(), AVPBlocks.TITANIUM_BLOCK, AVPBlocks.TITANIUM_SLAB);
        createStandardStairRecipe(builder.shaped(), AVPBlocks.TITANIUM_BLOCK, AVPBlocks.TITANIUM_STAIRS);

        createFerroaluminumBlockVariantRecipes(builder);
        createSteelBlockVariantRecipes(builder);
        createTitaniumBlockVariantRecipes(builder);

        // Add variant slab and stair recipes
        createVariantSlabAndStairRecipes(builder);

        // Nugget to ingot recipes
        nuggetToIngot(builder.shaped(), AVPItems.FERROALUMINUM_NUGGET, AVPItems.FERROALUMINUM_INGOT);
        nuggetToIngot(builder.shaped(), AVPItems.STEEL_NUGGET, AVPItems.STEEL_INGOT);
        nuggetToIngot(builder.shaped(), AVPItems.BRASS_NUGGET, AVPItems.BRASS_INGOT);
        nuggetToIngot(builder.shaped(), AVPItems.TITANIUM_NUGGET, AVPItems.TITANIUM_INGOT);
        nuggetToIngot(builder.shaped(), AVPItems.LEAD_NUGGET, AVPItems.LEAD_INGOT);
        nuggetToIngot(builder.shaped(), AVPItems.URANIUM_NUGGET, AVPItems.URANIUM_INGOT);
        nuggetToIngot(builder.shaped(), AVPItems.ZINC_NUGGET, AVPItems.ZINC_INGOT);
        nuggetToIngot(builder.shaped(), AVPItems.ALUMINUM_NUGGET, AVPItems.ALUMINUM_INGOT);

        // Ingot to nugget recipes
        ingotToNugget(builder.shapeless(), AVPItems.FERROALUMINUM_INGOT, AVPItems.FERROALUMINUM_NUGGET);
        ingotToNugget(builder.shapeless(), AVPItems.STEEL_INGOT, AVPItems.STEEL_NUGGET);
        ingotToNugget(builder.shapeless(), AVPItems.BRASS_INGOT, AVPItems.BRASS_NUGGET);
        ingotToNugget(builder.shapeless(), AVPItems.TITANIUM_INGOT, AVPItems.TITANIUM_NUGGET);
        ingotToNugget(builder.shapeless(), AVPItems.LEAD_INGOT, AVPItems.LEAD_NUGGET);
        ingotToNugget(builder.shapeless(), AVPItems.URANIUM_INGOT, AVPItems.URANIUM_NUGGET);
        ingotToNugget(builder.shapeless(), AVPItems.ZINC_INGOT, AVPItems.ZINC_NUGGET);
        ingotToNugget(builder.shapeless(), AVPItems.ALUMINUM_INGOT, AVPItems.ALUMINUM_NUGGET);
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
        ferroaluminumBaseBuilder.into(2, AVPBlocks.FERROALUMINUM_SLAB);
        ferroaluminumBaseBuilder.into(1, AVPBlocks.FERROALUMINUM_STAIRS);

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
        steelBaseBuilder.into(2, AVPBlocks.STEEL_SLAB);
        steelBaseBuilder.into(1, AVPBlocks.STEEL_STAIRS);

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
        titaniumBaseBuilder.into(2, AVPBlocks.TITANIUM_SLAB);
        titaniumBaseBuilder.into(1, AVPBlocks.TITANIUM_STAIRS);

        var cutTitaniumBuilder = builder.stonecut(AVPBlocks.CUT_TITANIUM)
                .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutTitaniumBuilder.into(2, AVPBlocks.CUT_TITANIUM_SLAB);
        cutTitaniumBuilder.into(1, AVPBlocks.CUT_TITANIUM_STAIRS);
    }

    private static void createVariantSlabAndStairRecipes(RecipeBuilder builder) {
        // Ferroaluminum variant slabs and stairs
        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.FERROALUMINUM_SIDING,
                AVPBlocks.FERROALUMINUM_SIDING_SLAB,
                AVPBlocks.FERROALUMINUM_SIDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.FERROALUMINUM_STANDING,
                AVPBlocks.FERROALUMINUM_STANDING_SLAB,
                AVPBlocks.FERROALUMINUM_STANDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING,
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB,
                AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING,
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB,
                AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.FERROALUMINUM_PLATING,
                AVPBlocks.FERROALUMINUM_PLATING_SLAB,
                AVPBlocks.FERROALUMINUM_PLATING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.FERROALUMINUM_TREAD,
                AVPBlocks.FERROALUMINUM_TREAD_SLAB,
                AVPBlocks.FERROALUMINUM_TREAD_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.FERROALUMINUM_GRATE,
                AVPBlocks.FERROALUMINUM_GRATE_SLAB,
                AVPBlocks.FERROALUMINUM_GRATE_STAIRS);

        // Steel variant slabs and stairs
        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.STEEL_SIDING,
                AVPBlocks.STEEL_SIDING_SLAB,
                AVPBlocks.STEEL_SIDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.STEEL_STANDING,
                AVPBlocks.STEEL_STANDING_SLAB,
                AVPBlocks.STEEL_STANDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.STEEL_FASTENED_SIDING,
                AVPBlocks.STEEL_FASTENED_SIDING_SLAB,
                AVPBlocks.STEEL_FASTENED_SIDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.STEEL_FASTENED_STANDING,
                AVPBlocks.STEEL_FASTENED_STANDING_SLAB,
                AVPBlocks.STEEL_FASTENED_STANDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.STEEL_PLATING,
                AVPBlocks.STEEL_PLATING_SLAB,
                AVPBlocks.STEEL_PLATING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.STEEL_TREAD,
                AVPBlocks.STEEL_TREAD_SLAB,
                AVPBlocks.STEEL_TREAD_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.STEEL_GRATE,
                AVPBlocks.STEEL_GRATE_SLAB,
                AVPBlocks.STEEL_GRATE_STAIRS);

        // Titanium variant slabs and stairs
        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.TITANIUM_SIDING,
                AVPBlocks.TITANIUM_SIDING_SLAB,
                AVPBlocks.TITANIUM_SIDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.TITANIUM_STANDING,
                AVPBlocks.TITANIUM_STANDING_SLAB,
                AVPBlocks.TITANIUM_STANDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.TITANIUM_FASTENED_SIDING,
                AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB,
                AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.TITANIUM_FASTENED_STANDING,
                AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB,
                AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.TITANIUM_PLATING,
                AVPBlocks.TITANIUM_PLATING_SLAB,
                AVPBlocks.TITANIUM_PLATING_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.TITANIUM_TREAD,
                AVPBlocks.TITANIUM_TREAD_SLAB,
                AVPBlocks.TITANIUM_TREAD_STAIRS);

        addVariantSlabAndStairRecipes(builder,
                AVPBlocks.TITANIUM_GRATE,
                AVPBlocks.TITANIUM_GRATE_SLAB,
                AVPBlocks.TITANIUM_GRATE_STAIRS);
    }

    private static void addVariantSlabAndStairRecipes(RecipeBuilder builder, ItemLike baseBlock, ItemLike slab, ItemLike stairs) {
        // Add stonecut recipes for the block variants
        builder.stonecut(baseBlock)
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .into(2, slab);

        builder.stonecut(baseBlock)
                .withCategory(RecipeCategory.BUILDING_BLOCKS)
                .into(1, stairs);

        // Add shaped crafting recipes
        createStandardSlabRecipe(builder.shaped(), baseBlock, slab);
        createStandardStairRecipe(builder.shaped(), baseBlock, stairs);
    }

    private static void createStandardSlabRecipe(ShapedRecipeBuilder builder, ItemLike input, ItemLike output) {
        builder.withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('#', input)
                .pattern("###")
                .into(6, output);
    }

    private static void createStandardStairRecipe(ShapedRecipeBuilder builder, ItemLike input, ItemLike output) {
        builder.withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('#', input)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .into(4, output);
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

    private static void nuggetToIngot(ShapedRecipeBuilder builder, ItemLike input, ItemLike output)
    {
        builder.withCategory(RecipeCategory.BUILDING_BLOCKS)
                .define('N',input)
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .into(1,output);
    }

    private static void ingotToNugget(ShapelessRecipeBuilder builder, ItemLike input, ItemLike output)
    {
        builder.withCategory(RecipeCategory.BUILDING_BLOCKS)
                .requires(1,input)
                .into(9,output);
    }
}