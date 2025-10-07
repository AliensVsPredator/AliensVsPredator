package com.avp.fabric.data.recipe.impl;

import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.fabric.data.compatibility.common.CommonConstants;
import com.avp.fabric.data.recipe.RecipeConstants;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class MetalRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, AVPItems.CARBON_DUST.get())
            .requires(1, Items.RAW_IRON)
            .into(1, AVPItems.RAW_CRUDE_IRON.get());

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, AVPItems.RAW_BAUXITE.get())
            .requires(1, AVPItems.CARBON_DUST.get())
            .requires(1, Items.RAW_IRON)
            .into(2, AVPItems.RAW_FERROBAUXITE.get());

        // Steel can only be blasted.
        builder.blast(AVPItems.RAW_CRUDE_IRON.get())
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.RARE_SMELT_EXPERIENCE)
            .into(AVPItems.STEEL_INGOT.get());

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, AVPItems.RAW_ZINC.get())
            .requires(1, Items.RAW_COPPER)
            .into(2, AVPItems.RAW_BRASS.get());

        builder.shaped()
            .withCustomName(name -> "uranium_ignot_normal")
            .withCategory(RecipeCategory.MISC)
            .define('A', AVPItems.AUTUNITE_DUST.get())
            .define('T', AVPItems.TITANIUM_INGOT.get())
            .pattern(" A ")
            .pattern("ATA")
            .pattern(" A ")
            .into(1, AVPItems.URANIUM_INGOT.get());

        createSmeltAndBlastRecipes(builder, CoreBlocks.BAUXITE_ORE.get(), AVPItems.ALUMINUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, CoreBlocks.GALENA_ORE.get(), AVPItems.LEAD_INGOT.get());
        createSmeltAndBlastRecipes(builder, CoreBlocks.MONAZITE_ORE.get(), AVPItems.NEODYMIUM_MAGNET.get());
        createSmeltAndBlastRecipes(builder, CoreBlocks.DEEPSLATE_TITANIUM_ORE.get(), AVPItems.TITANIUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, CoreBlocks.ZINC_ORE.get(), AVPItems.ZINC_INGOT.get());
        createSmeltAndBlastRecipes(builder, CoreBlocks.DEEPSLATE_ZINC_ORE.get(), AVPItems.ZINC_INGOT.get());

        createSmeltAndBlastRecipes(builder, AVPItems.RAW_BAUXITE.get(), AVPItems.ALUMINUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_BRASS.get(), AVPItems.BRASS_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_FERROBAUXITE.get(), AVPItems.FERROALUMINUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_GALENA.get(), AVPItems.LEAD_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_MONAZITE.get(), AVPItems.NEODYMIUM_MAGNET.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_TITANIUM.get(), AVPItems.TITANIUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_ZINC.get(), AVPItems.ZINC_INGOT.get());

        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.ALUMINUM_INGOT.get(), CoreBlocks.ALUMINUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.BRASS_INGOT.get(), CoreBlocks.BRASS_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(
            builder,
            AVPItems.FERROALUMINUM_INGOT.get(),
            HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK.get()
        );
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.LEAD_INGOT.get(), CoreBlocks.LEAD_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.STEEL_INGOT.get(), HumanSteelBlocks.STEEL_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.TITANIUM_INGOT.get(), HumanTitaniumBlocks.TITANIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.URANIUM_INGOT.get(), CoreBlocks.URANIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.ZINC_INGOT.get(), CoreBlocks.ZINC_BLOCK.get());

        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.AUTUNITE_DUST.get(), CoreBlocks.AUTUNITE_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.LITHIUM_DUST.get(), CoreBlocks.LITHIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3TagFriendly(
            builder,
            AVPItems.RAW_BAUXITE.get(),
            CommonConstants.RAW_MATERIALS_ALUMINUM,
            CoreBlocks.RAW_BAUXITE_BLOCK.get()
        );
        RecipeUtil.createCompressedBlockRecipes3x3TagFriendly(
            builder,
            AVPItems.RAW_GALENA.get(),
            CommonConstants.RAW_MATERIALS_LEAD,
            CoreBlocks.RAW_GALENA_BLOCK.get()
        );
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_MONAZITE.get(), CoreBlocks.RAW_MONAZITE_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_TITANIUM.get(), CoreBlocks.RAW_TITANIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_ZINC.get(), CoreBlocks.RAW_ZINC_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.SILICON.get(), CoreBlocks.SILICON_BLOCK.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BARS_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(16, HumanSteelBlocks.STEEL_BARS);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT.get()))
            .into(2, HumanFerroaluminumBlocks.FERROALUMINUM_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(2, HumanSteelBlocks.STEEL_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(AVPItems.TITANIUM_INGOT.get()))
            .into(2, HumanTitaniumBlocks.TITANIUM_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT.get()))
            .into(3, HumanFerroaluminumBlocks.FERROALUMINUM_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(3, HumanSteelBlocks.STEEL_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(AVPItems.TITANIUM_INGOT.get()))
            .into(3, HumanTitaniumBlocks.TITANIUM_DOOR);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT.get()))
            .into(2, HumanFerroaluminumBlocks.FERROALUMINUM_TRAP_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(2, HumanSteelBlocks.STEEL_TRAP_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(AVPItems.TITANIUM_INGOT.get()))
            .into(2, HumanTitaniumBlocks.TITANIUM_TRAP_DOOR);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT.get()))
            .into(1, HumanFerroaluminumBlocks.FERROALUMINUM_PRESSURE_PLATE);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(1, HumanSteelBlocks.STEEL_PRESSURE_PLATE);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(AVPItems.TITANIUM_INGOT.get()))
            .into(1, HumanTitaniumBlocks.TITANIUM_PRESSURE_PLATE);

        // Add standard slab and stair crafting recipes
        createStandardSlabRecipe(
            builder,
            HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK.get(),
            HumanFerroaluminumBlocks.FERROALUMINUM_SLAB.get()
        );
        createStandardStairRecipe(
            builder,
            HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK.get(),
            HumanFerroaluminumBlocks.FERROALUMINUM_STAIRS.get()
        );

        createStandardSlabRecipe(builder, HumanSteelBlocks.STEEL_BLOCK.get(), HumanSteelBlocks.STEEL_SLAB.get());
        createStandardStairRecipe(builder, HumanSteelBlocks.STEEL_BLOCK.get(), HumanSteelBlocks.STEEL_STAIRS.get());

        createStandardSlabRecipe(builder, HumanTitaniumBlocks.TITANIUM_BLOCK.get(), HumanTitaniumBlocks.TITANIUM_SLAB.get());
        createStandardStairRecipe(builder, HumanTitaniumBlocks.TITANIUM_BLOCK.get(), HumanTitaniumBlocks.TITANIUM_STAIRS.get());

        createFerroaluminumBlockVariantRecipes(builder);
        createSteelBlockVariantRecipes(builder);
        createTitaniumBlockVariantRecipes(builder);

        // Add variant slab and stair recipes
        createVariantSlabAndStairRecipes(builder);

        // Nugget to ingot recipes
        nuggetToIngot(builder, AVPItems.FERROALUMINUM_NUGGET.get(), AVPItems.FERROALUMINUM_INGOT.get());
        nuggetToIngot(builder, AVPItems.STEEL_NUGGET.get(), AVPItems.STEEL_INGOT.get());
        nuggetToIngot(builder, AVPItems.BRASS_NUGGET.get(), AVPItems.BRASS_INGOT.get());
        nuggetToIngot(builder, AVPItems.TITANIUM_NUGGET.get(), AVPItems.TITANIUM_INGOT.get());
        nuggetToIngot(builder, AVPItems.LEAD_NUGGET.get(), AVPItems.LEAD_INGOT.get());
        nuggetToIngot(builder, AVPItems.URANIUM_NUGGET.get(), AVPItems.URANIUM_INGOT.get());
        nuggetToIngot(builder, AVPItems.ZINC_NUGGET.get(), AVPItems.ZINC_INGOT.get());
        nuggetToIngot(builder, AVPItems.ALUMINUM_NUGGET.get(), AVPItems.ALUMINUM_INGOT.get());

        // Ingot to nugget recipes
        ingotToNugget(builder, AVPItems.FERROALUMINUM_INGOT.get(), AVPItems.FERROALUMINUM_NUGGET.get());
        ingotToNugget(builder, AVPItems.STEEL_INGOT.get(), AVPItems.STEEL_NUGGET.get());
        ingotToNugget(builder, AVPItems.BRASS_INGOT.get(), AVPItems.BRASS_NUGGET.get());
        ingotToNugget(builder, AVPItems.TITANIUM_INGOT.get(), AVPItems.TITANIUM_NUGGET.get());
        ingotToNugget(builder, AVPItems.LEAD_INGOT.get(), AVPItems.LEAD_NUGGET.get());
        ingotToNugget(builder, AVPItems.URANIUM_INGOT.get(), AVPItems.URANIUM_NUGGET.get());
        ingotToNugget(builder, AVPItems.ZINC_INGOT.get(), AVPItems.ZINC_NUGGET.get());
        ingotToNugget(builder, AVPItems.ALUMINUM_INGOT.get(), AVPItems.ALUMINUM_NUGGET.get());
    }

    private static void createFerroaluminumBlockVariantRecipes(RecipeBuilder builder) {
        var ferroaluminumBaseBuilder = builder.stonecut(HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.CHISELED_FERROALUMINUM);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.CUT_FERROALUMINUM);
        ferroaluminumBaseBuilder.into(8, HumanFerroaluminumBlocks.CUT_FERROALUMINUM_SLAB);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.CUT_FERROALUMINUM_STAIRS);
        ferroaluminumBaseBuilder.into(16, HumanFerroaluminumBlocks.FERROALUMINUM_CHAIN_FENCE);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.FERROALUMINUM_COLUMN);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.FERROALUMINUM_GRATE);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.FERROALUMINUM_PLATING);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.FERROALUMINUM_SIDING);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.FERROALUMINUM_STANDING);
        ferroaluminumBaseBuilder.into(4, HumanFerroaluminumBlocks.FERROALUMINUM_TREAD);
        ferroaluminumBaseBuilder.into(2, HumanFerroaluminumBlocks.FERROALUMINUM_SLAB);
        ferroaluminumBaseBuilder.into(1, HumanFerroaluminumBlocks.FERROALUMINUM_STAIRS);

        var cutFerroaluminumBuilder = builder.stonecut(HumanFerroaluminumBlocks.CUT_FERROALUMINUM)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutFerroaluminumBuilder.into(2, HumanFerroaluminumBlocks.CUT_FERROALUMINUM_SLAB);
        cutFerroaluminumBuilder.into(1, HumanFerroaluminumBlocks.CUT_FERROALUMINUM_STAIRS);
    }

    private static void createSteelBlockVariantRecipes(RecipeBuilder builder) {
        var steelBaseBuilder = builder.stonecut(HumanSteelBlocks.STEEL_BLOCK.get())
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        steelBaseBuilder.into(4, HumanSteelBlocks.CHISELED_STEEL);
        steelBaseBuilder.into(4, HumanSteelBlocks.CUT_STEEL);
        steelBaseBuilder.into(8, HumanSteelBlocks.CUT_STEEL_SLAB);
        steelBaseBuilder.into(4, HumanSteelBlocks.CUT_STEEL_STAIRS);
        steelBaseBuilder.into(16, HumanSteelBlocks.STEEL_CHAIN_FENCE);
        steelBaseBuilder.into(4, HumanSteelBlocks.STEEL_COLUMN);
        steelBaseBuilder.into(4, HumanSteelBlocks.STEEL_FASTENED_SIDING);
        steelBaseBuilder.into(4, HumanSteelBlocks.STEEL_FASTENED_STANDING);
        steelBaseBuilder.into(4, HumanSteelBlocks.STEEL_GRATE);
        steelBaseBuilder.into(4, HumanSteelBlocks.STEEL_PLATING);
        steelBaseBuilder.into(4, HumanSteelBlocks.STEEL_SIDING);
        steelBaseBuilder.into(4, HumanSteelBlocks.STEEL_STANDING);
        steelBaseBuilder.into(4, HumanSteelBlocks.STEEL_TREAD);
        steelBaseBuilder.into(2, HumanSteelBlocks.STEEL_SLAB.get());
        steelBaseBuilder.into(1, HumanSteelBlocks.STEEL_STAIRS.get());

        var cutSteelBuilder = builder.stonecut(HumanSteelBlocks.CUT_STEEL)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutSteelBuilder.into(2, HumanSteelBlocks.CUT_STEEL_SLAB);
        cutSteelBuilder.into(1, HumanSteelBlocks.CUT_STEEL_STAIRS);
    }

    private static void createTitaniumBlockVariantRecipes(RecipeBuilder builder) {
        var titaniumBaseBuilder = builder.stonecut(HumanTitaniumBlocks.TITANIUM_BLOCK)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.CHISELED_TITANIUM);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.CUT_TITANIUM);
        titaniumBaseBuilder.into(8, HumanTitaniumBlocks.CUT_TITANIUM_SLAB);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.CUT_TITANIUM_STAIRS);
        titaniumBaseBuilder.into(16, HumanTitaniumBlocks.TITANIUM_CHAIN_FENCE);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.TITANIUM_COLUMN);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.TITANIUM_GRATE);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.TITANIUM_PLATING);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.TITANIUM_SIDING);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.TITANIUM_STANDING);
        titaniumBaseBuilder.into(4, HumanTitaniumBlocks.TITANIUM_TREAD);
        titaniumBaseBuilder.into(2, HumanTitaniumBlocks.TITANIUM_SLAB);
        titaniumBaseBuilder.into(1, HumanTitaniumBlocks.TITANIUM_STAIRS);

        var cutTitaniumBuilder = builder.stonecut(HumanTitaniumBlocks.CUT_TITANIUM)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutTitaniumBuilder.into(2, HumanTitaniumBlocks.CUT_TITANIUM_SLAB);
        cutTitaniumBuilder.into(1, HumanTitaniumBlocks.CUT_TITANIUM_STAIRS);
    }

    private static void createVariantSlabAndStairRecipes(RecipeBuilder builder) {
        // Ferroaluminum variant slabs and stairs
        addVariantSlabAndStairRecipes(
            builder,
            HumanFerroaluminumBlocks.FERROALUMINUM_SIDING,
            HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_SLAB,
            HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanFerroaluminumBlocks.FERROALUMINUM_STANDING,
            HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_SLAB,
            HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING,
            HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB,
            HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING,
            HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB,
            HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanFerroaluminumBlocks.FERROALUMINUM_PLATING,
            HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_SLAB,
            HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanFerroaluminumBlocks.FERROALUMINUM_TREAD,
            HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_SLAB,
            HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanFerroaluminumBlocks.FERROALUMINUM_GRATE,
            HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_SLAB,
            HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_STAIRS
        );

        // Steel variant slabs and stairs
        addVariantSlabAndStairRecipes(
            builder,
            HumanSteelBlocks.STEEL_SIDING,
            HumanSteelBlocks.STEEL_SIDING_SLAB,
            HumanSteelBlocks.STEEL_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanSteelBlocks.STEEL_STANDING,
            HumanSteelBlocks.STEEL_STANDING_SLAB,
            HumanSteelBlocks.STEEL_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanSteelBlocks.STEEL_FASTENED_SIDING,
            HumanSteelBlocks.STEEL_FASTENED_SIDING_SLAB,
            HumanSteelBlocks.STEEL_FASTENED_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanSteelBlocks.STEEL_FASTENED_STANDING,
            HumanSteelBlocks.STEEL_FASTENED_STANDING_SLAB,
            HumanSteelBlocks.STEEL_FASTENED_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanSteelBlocks.STEEL_PLATING,
            HumanSteelBlocks.STEEL_PLATING_SLAB,
            HumanSteelBlocks.STEEL_PLATING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanSteelBlocks.STEEL_TREAD,
            HumanSteelBlocks.STEEL_TREAD_SLAB,
            HumanSteelBlocks.STEEL_TREAD_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanSteelBlocks.STEEL_GRATE,
            HumanSteelBlocks.STEEL_GRATE_SLAB,
            HumanSteelBlocks.STEEL_GRATE_STAIRS
        );

        // Titanium variant slabs and stairs
        addVariantSlabAndStairRecipes(
            builder,
            HumanTitaniumBlocks.TITANIUM_SIDING,
            HumanTitaniumBlocks.TITANIUM_SIDING_SLAB,
            HumanTitaniumBlocks.TITANIUM_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanTitaniumBlocks.TITANIUM_STANDING,
            HumanTitaniumBlocks.TITANIUM_STANDING_SLAB,
            HumanTitaniumBlocks.TITANIUM_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING,
            HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_SLAB,
            HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING,
            HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_SLAB,
            HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanTitaniumBlocks.TITANIUM_PLATING,
            HumanTitaniumBlocks.TITANIUM_PLATING_SLAB,
            HumanTitaniumBlocks.TITANIUM_PLATING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanTitaniumBlocks.TITANIUM_TREAD,
            HumanTitaniumBlocks.TITANIUM_TREAD_SLAB,
            HumanTitaniumBlocks.TITANIUM_TREAD_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            HumanTitaniumBlocks.TITANIUM_GRATE,
            HumanTitaniumBlocks.TITANIUM_GRATE_SLAB,
            HumanTitaniumBlocks.TITANIUM_GRATE_STAIRS
        );
    }

    private static void addVariantSlabAndStairRecipes(
        RecipeBuilder builder,
        Supplier<? extends ItemLike> baseBlockSupplier,
        Supplier<? extends ItemLike> slabSupplier,
        Supplier<? extends ItemLike> stairsSupplier
    ) {
        // Add stonecut recipes for the block variants
        builder.stonecut(baseBlockSupplier)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(2, slabSupplier);

        builder.stonecut(baseBlockSupplier)
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .into(1, stairsSupplier);

        // Add shaped crafting recipes
        var baseBlock = baseBlockSupplier.get();
        var slab = slabSupplier.get();
        var stairs = stairsSupplier.get();
        createStandardSlabRecipe(builder, baseBlock, slab);
        createStandardStairRecipe(builder, baseBlock, stairs);
    }

    private static void createStandardSlabRecipe(RecipeBuilder builder, ItemLike input, ItemLike output) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.SLAB_BLOCK.apply(input))
            .into(6, output);
    }

    private static void createStandardStairRecipe(RecipeBuilder builder, ItemLike input, ItemLike output) {
        builder
            .shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.STAIR_BLOCK.apply(input))
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

    private static void nuggetToIngot(RecipeBuilder builder, ItemLike input, ItemLike output) {
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .define('N', input)
            .pattern("NNN")
            .pattern("NNN")
            .pattern("NNN")
            .into(1, output);
    }

    private static void ingotToNugget(RecipeBuilder builder, ItemLike input, ItemLike output) {
        builder.shapeless()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .requires(1, input)
            .into(9, output);
    }
}
