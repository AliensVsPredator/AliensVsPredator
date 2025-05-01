package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.data.recipe.RecipeConstants;
import com.avp.fabric.data.recipe.RecipeTemplates;
import com.avp.fabric.data.recipe.builder.RecipeBuilder;
import com.avp.fabric.data.recipe.util.RecipeUtil;

public class MetalRecipeProvider {

    public static void provide(RecipeBuilder builder) {
        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, TempAVPItems.CARBON_DUST.get())
            .requires(1, Items.RAW_IRON)
            .into(1, TempAVPItems.RAW_CRUDE_IRON.get());

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, TempAVPItems.RAW_BAUXITE.get())
            .requires(1, TempAVPItems.CARBON_DUST.get())
            .requires(1, Items.RAW_IRON)
            .into(2, TempAVPItems.RAW_FERROBAUXITE.get());

        // Steel can only be blasted.
        builder.blast(TempAVPItems.RAW_CRUDE_IRON.get())
            .withCategory(RecipeCategory.MISC)
            .withExperience(RecipeConstants.RARE_SMELT_EXPERIENCE)
            .into(TempAVPItems.STEEL_INGOT.get());

        builder.shapeless()
            .withCategory(RecipeCategory.MISC)
            .requires(1, TempAVPItems.RAW_ZINC.get())
            .requires(1, Items.RAW_COPPER)
            .into(2, TempAVPItems.RAW_BRASS.get());

        builder.shaped()
            .withCustomName(name -> "uranium_ignot_normal")
            .withCategory(RecipeCategory.MISC)
            .define('A', TempAVPItems.AUTUNITE_DUST.get())
            .define('T', TempAVPItems.TITANIUM_INGOT.get())
            .pattern(" A ")
            .pattern("ATA")
            .pattern(" A ")
            .into(1, TempAVPItems.URANIUM_INGOT.get());

        createSmeltAndBlastRecipes(builder, TempAVPBlocks.BAUXITE_ORE.get(), TempAVPItems.ALUMINUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, TempAVPBlocks.GALENA_ORE.get(), TempAVPItems.LEAD_INGOT.get());
        createSmeltAndBlastRecipes(builder, TempAVPBlocks.MONAZITE_ORE.get(), TempAVPItems.NEODYMIUM_MAGNET.get());
        createSmeltAndBlastRecipes(builder, TempAVPBlocks.DEEPSLATE_TITANIUM_ORE.get(), TempAVPItems.TITANIUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, TempAVPBlocks.ZINC_ORE.get(), TempAVPItems.ZINC_INGOT.get());
        createSmeltAndBlastRecipes(builder, TempAVPBlocks.DEEPSLATE_ZINC_ORE.get(), TempAVPItems.ZINC_INGOT.get());

        createSmeltAndBlastRecipes(builder, TempAVPItems.RAW_BAUXITE.get(), TempAVPItems.ALUMINUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, TempAVPItems.RAW_BRASS.get(), TempAVPItems.BRASS_INGOT.get());
        createSmeltAndBlastRecipes(builder, TempAVPItems.RAW_FERROBAUXITE.get(), TempAVPItems.FERROALUMINUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, TempAVPItems.RAW_GALENA.get(), TempAVPItems.LEAD_INGOT.get());
        createSmeltAndBlastRecipes(builder, TempAVPItems.RAW_MONAZITE.get(), TempAVPItems.NEODYMIUM_MAGNET.get());
        createSmeltAndBlastRecipes(builder, TempAVPItems.RAW_TITANIUM.get(), TempAVPItems.TITANIUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, TempAVPItems.RAW_ZINC.get(), TempAVPItems.ZINC_INGOT.get());

        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.ALUMINUM_INGOT.get(), TempAVPBlocks.ALUMINUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.BRASS_INGOT.get(), TempAVPBlocks.BRASS_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(
            builder,
            TempAVPItems.FERROALUMINUM_INGOT.get(),
            TempAVPBlocks.FERROALUMINUM_BLOCK.get()
        );
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.LEAD_INGOT.get(), TempAVPBlocks.LEAD_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.STEEL_INGOT.get(), TempAVPBlocks.STEEL_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.TITANIUM_INGOT.get(), TempAVPBlocks.TITANIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.URANIUM_INGOT.get(), TempAVPBlocks.URANIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.ZINC_INGOT.get(), TempAVPBlocks.ZINC_BLOCK.get());

        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.AUTUNITE_DUST.get(), AVPBlocks.AUTUNITE_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.LITHIUM_DUST.get(), TempAVPBlocks.LITHIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_BAUXITE.get(), TempAVPBlocks.RAW_BAUXITE_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_GALENA.get(), TempAVPBlocks.RAW_GALENA_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_MONAZITE.get(), TempAVPBlocks.RAW_MONAZITE_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_SILICA.get(), TempAVPBlocks.RAW_SILICA_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_TITANIUM.get(), TempAVPBlocks.RAW_TITANIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_ZINC.get(), TempAVPBlocks.RAW_ZINC_BLOCK.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BARS_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(16, TempAVPBlocks.STEEL_BARS);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(TempAVPItems.FERROALUMINUM_INGOT.get()))
            .into(2, TempAVPBlocks.FERROALUMINUM_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(2, TempAVPBlocks.STEEL_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(2, TempAVPBlocks.TITANIUM_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(TempAVPItems.FERROALUMINUM_INGOT.get()))
            .into(3, TempAVPBlocks.FERROALUMINUM_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(3, TempAVPBlocks.STEEL_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(3, TempAVPBlocks.TITANIUM_DOOR);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(TempAVPItems.FERROALUMINUM_INGOT.get()))
            .into(2, TempAVPBlocks.FERROALUMINUM_TRAP_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(2, TempAVPBlocks.STEEL_TRAP_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(2, TempAVPBlocks.TITANIUM_TRAP_DOOR);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(TempAVPItems.FERROALUMINUM_INGOT.get()))
            .into(1, TempAVPBlocks.FERROALUMINUM_PRESSURE_PLATE);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(1, TempAVPBlocks.STEEL_PRESSURE_PLATE);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(1, TempAVPBlocks.TITANIUM_PRESSURE_PLATE);

        // Add standard slab and stair crafting recipes
        createStandardSlabRecipe(builder, TempAVPBlocks.FERROALUMINUM_BLOCK.get(), TempAVPBlocks.FERROALUMINUM_SLAB.get());
        createStandardStairRecipe(builder, TempAVPBlocks.FERROALUMINUM_BLOCK.get(), TempAVPBlocks.FERROALUMINUM_STAIRS.get());

        createStandardSlabRecipe(builder, TempAVPBlocks.STEEL_BLOCK.get(), TempAVPBlocks.STEEL_SLAB.get());
        createStandardStairRecipe(builder, TempAVPBlocks.STEEL_BLOCK.get(), TempAVPBlocks.STEEL_STAIRS.get());

        createStandardSlabRecipe(builder, TempAVPBlocks.TITANIUM_BLOCK.get(), TempAVPBlocks.TITANIUM_SLAB.get());
        createStandardStairRecipe(builder, TempAVPBlocks.TITANIUM_BLOCK.get(), TempAVPBlocks.TITANIUM_STAIRS.get());

        createFerroaluminumBlockVariantRecipes(builder);
        createSteelBlockVariantRecipes(builder);
        createTitaniumBlockVariantRecipes(builder);

        // Add variant slab and stair recipes
        createVariantSlabAndStairRecipes(builder);

        // Nugget to ingot recipes
        nuggetToIngot(builder, TempAVPItems.FERROALUMINUM_NUGGET.get(), TempAVPItems.FERROALUMINUM_INGOT.get());
        nuggetToIngot(builder, TempAVPItems.STEEL_NUGGET.get(), TempAVPItems.STEEL_INGOT.get());
        nuggetToIngot(builder, TempAVPItems.BRASS_NUGGET.get(), TempAVPItems.BRASS_INGOT.get());
        nuggetToIngot(builder, TempAVPItems.TITANIUM_NUGGET.get(), TempAVPItems.TITANIUM_INGOT.get());
        nuggetToIngot(builder, TempAVPItems.LEAD_NUGGET.get(), TempAVPItems.LEAD_INGOT.get());
        nuggetToIngot(builder, TempAVPItems.URANIUM_NUGGET.get(), TempAVPItems.URANIUM_INGOT.get());
        nuggetToIngot(builder, TempAVPItems.ZINC_NUGGET.get(), TempAVPItems.ZINC_INGOT.get());
        nuggetToIngot(builder, TempAVPItems.ALUMINUM_NUGGET.get(), TempAVPItems.ALUMINUM_INGOT.get());

        // Ingot to nugget recipes
        ingotToNugget(builder, TempAVPItems.FERROALUMINUM_INGOT.get(), TempAVPItems.FERROALUMINUM_NUGGET.get());
        ingotToNugget(builder, TempAVPItems.STEEL_INGOT.get(), TempAVPItems.STEEL_NUGGET.get());
        ingotToNugget(builder, TempAVPItems.BRASS_INGOT.get(), TempAVPItems.BRASS_NUGGET.get());
        ingotToNugget(builder, TempAVPItems.TITANIUM_INGOT.get(), TempAVPItems.TITANIUM_NUGGET.get());
        ingotToNugget(builder, TempAVPItems.LEAD_INGOT.get(), TempAVPItems.LEAD_NUGGET.get());
        ingotToNugget(builder, TempAVPItems.URANIUM_INGOT.get(), TempAVPItems.URANIUM_NUGGET.get());
        ingotToNugget(builder, TempAVPItems.ZINC_INGOT.get(), TempAVPItems.ZINC_NUGGET.get());
        ingotToNugget(builder, TempAVPItems.ALUMINUM_INGOT.get(), TempAVPItems.ALUMINUM_NUGGET.get());
    }

    private static void createFerroaluminumBlockVariantRecipes(RecipeBuilder builder) {
        var ferroaluminumBaseBuilder = builder.stonecut(TempAVPBlocks.FERROALUMINUM_BLOCK)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.CHISELED_FERROALUMINUM);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.CUT_FERROALUMINUM);
        ferroaluminumBaseBuilder.into(8, TempAVPBlocks.CUT_FERROALUMINUM_SLAB);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.CUT_FERROALUMINUM_STAIRS);
        ferroaluminumBaseBuilder.into(16, TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.FERROALUMINUM_COLUMN);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.FERROALUMINUM_GRATE);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.FERROALUMINUM_PLATING);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.FERROALUMINUM_SIDING);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.FERROALUMINUM_STANDING);
        ferroaluminumBaseBuilder.into(4, TempAVPBlocks.FERROALUMINUM_TREAD);
        ferroaluminumBaseBuilder.into(2, TempAVPBlocks.FERROALUMINUM_SLAB);
        ferroaluminumBaseBuilder.into(1, TempAVPBlocks.FERROALUMINUM_STAIRS);

        var cutFerroaluminumBuilder = builder.stonecut(TempAVPBlocks.CUT_FERROALUMINUM)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutFerroaluminumBuilder.into(2, TempAVPBlocks.CUT_FERROALUMINUM_SLAB);
        cutFerroaluminumBuilder.into(1, TempAVPBlocks.CUT_FERROALUMINUM_STAIRS);
    }

    private static void createSteelBlockVariantRecipes(RecipeBuilder builder) {
        var steelBaseBuilder = builder.stonecut(TempAVPBlocks.STEEL_BLOCK.get())
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        steelBaseBuilder.into(4, TempAVPBlocks.CHISELED_STEEL);
        steelBaseBuilder.into(4, TempAVPBlocks.CUT_STEEL);
        steelBaseBuilder.into(8, TempAVPBlocks.CUT_STEEL_SLAB);
        steelBaseBuilder.into(4, TempAVPBlocks.CUT_STEEL_STAIRS);
        steelBaseBuilder.into(16, TempAVPBlocks.STEEL_CHAIN_FENCE);
        steelBaseBuilder.into(4, TempAVPBlocks.STEEL_COLUMN);
        steelBaseBuilder.into(4, TempAVPBlocks.STEEL_FASTENED_SIDING);
        steelBaseBuilder.into(4, TempAVPBlocks.STEEL_FASTENED_STANDING);
        steelBaseBuilder.into(4, TempAVPBlocks.STEEL_GRATE);
        steelBaseBuilder.into(4, TempAVPBlocks.STEEL_PLATING);
        steelBaseBuilder.into(4, TempAVPBlocks.STEEL_SIDING);
        steelBaseBuilder.into(4, TempAVPBlocks.STEEL_STANDING);
        steelBaseBuilder.into(4, TempAVPBlocks.STEEL_TREAD);
        steelBaseBuilder.into(2, TempAVPBlocks.STEEL_SLAB.get());
        steelBaseBuilder.into(1, TempAVPBlocks.STEEL_STAIRS.get());

        var cutSteelBuilder = builder.stonecut(TempAVPBlocks.CUT_STEEL)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutSteelBuilder.into(2, TempAVPBlocks.CUT_STEEL_SLAB);
        cutSteelBuilder.into(1, TempAVPBlocks.CUT_STEEL_STAIRS);
    }

    private static void createTitaniumBlockVariantRecipes(RecipeBuilder builder) {
        var titaniumBaseBuilder = builder.stonecut(TempAVPBlocks.TITANIUM_BLOCK)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        titaniumBaseBuilder.into(4, TempAVPBlocks.CHISELED_TITANIUM);
        titaniumBaseBuilder.into(4, TempAVPBlocks.CUT_TITANIUM);
        titaniumBaseBuilder.into(8, TempAVPBlocks.CUT_TITANIUM_SLAB);
        titaniumBaseBuilder.into(4, TempAVPBlocks.CUT_TITANIUM_STAIRS);
        titaniumBaseBuilder.into(16, TempAVPBlocks.TITANIUM_CHAIN_FENCE);
        titaniumBaseBuilder.into(4, TempAVPBlocks.TITANIUM_COLUMN);
        titaniumBaseBuilder.into(4, TempAVPBlocks.TITANIUM_FASTENED_SIDING);
        titaniumBaseBuilder.into(4, TempAVPBlocks.TITANIUM_FASTENED_STANDING);
        titaniumBaseBuilder.into(4, TempAVPBlocks.TITANIUM_GRATE);
        titaniumBaseBuilder.into(4, TempAVPBlocks.TITANIUM_PLATING);
        titaniumBaseBuilder.into(4, TempAVPBlocks.TITANIUM_SIDING);
        titaniumBaseBuilder.into(4, TempAVPBlocks.TITANIUM_STANDING);
        titaniumBaseBuilder.into(4, TempAVPBlocks.TITANIUM_TREAD);
        titaniumBaseBuilder.into(2, TempAVPBlocks.TITANIUM_SLAB);
        titaniumBaseBuilder.into(1, TempAVPBlocks.TITANIUM_STAIRS);

        var cutTitaniumBuilder = builder.stonecut(TempAVPBlocks.CUT_TITANIUM)
            .withCategory(RecipeCategory.BUILDING_BLOCKS);

        cutTitaniumBuilder.into(2, TempAVPBlocks.CUT_TITANIUM_SLAB);
        cutTitaniumBuilder.into(1, TempAVPBlocks.CUT_TITANIUM_STAIRS);
    }

    private static void createVariantSlabAndStairRecipes(RecipeBuilder builder) {
        // Ferroaluminum variant slabs and stairs
        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.FERROALUMINUM_SIDING,
            TempAVPBlocks.FERROALUMINUM_SIDING_SLAB,
            TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.FERROALUMINUM_STANDING,
            TempAVPBlocks.FERROALUMINUM_STANDING_SLAB,
            TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING,
            TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB,
            TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING,
            TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB,
            TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.FERROALUMINUM_PLATING,
            TempAVPBlocks.FERROALUMINUM_PLATING_SLAB,
            TempAVPBlocks.FERROALUMINUM_PLATING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.FERROALUMINUM_TREAD,
            TempAVPBlocks.FERROALUMINUM_TREAD_SLAB,
            TempAVPBlocks.FERROALUMINUM_TREAD_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.FERROALUMINUM_GRATE,
            TempAVPBlocks.FERROALUMINUM_GRATE_SLAB,
            TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS
        );

        // Steel variant slabs and stairs
        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.STEEL_SIDING,
            TempAVPBlocks.STEEL_SIDING_SLAB,
            TempAVPBlocks.STEEL_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.STEEL_STANDING,
            TempAVPBlocks.STEEL_STANDING_SLAB,
            TempAVPBlocks.STEEL_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.STEEL_FASTENED_SIDING,
            TempAVPBlocks.STEEL_FASTENED_SIDING_SLAB,
            TempAVPBlocks.STEEL_FASTENED_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.STEEL_FASTENED_STANDING,
            TempAVPBlocks.STEEL_FASTENED_STANDING_SLAB,
            TempAVPBlocks.STEEL_FASTENED_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.STEEL_PLATING,
            TempAVPBlocks.STEEL_PLATING_SLAB,
            TempAVPBlocks.STEEL_PLATING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.STEEL_TREAD,
            TempAVPBlocks.STEEL_TREAD_SLAB,
            TempAVPBlocks.STEEL_TREAD_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.STEEL_GRATE,
            TempAVPBlocks.STEEL_GRATE_SLAB,
            TempAVPBlocks.STEEL_GRATE_STAIRS
        );

        // Titanium variant slabs and stairs
        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.TITANIUM_SIDING,
            TempAVPBlocks.TITANIUM_SIDING_SLAB,
            TempAVPBlocks.TITANIUM_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.TITANIUM_STANDING,
            TempAVPBlocks.TITANIUM_STANDING_SLAB,
            TempAVPBlocks.TITANIUM_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.TITANIUM_FASTENED_SIDING,
            TempAVPBlocks.TITANIUM_FASTENED_SIDING_SLAB,
            TempAVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.TITANIUM_FASTENED_STANDING,
            TempAVPBlocks.TITANIUM_FASTENED_STANDING_SLAB,
            TempAVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.TITANIUM_PLATING,
            TempAVPBlocks.TITANIUM_PLATING_SLAB,
            TempAVPBlocks.TITANIUM_PLATING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.TITANIUM_TREAD,
            TempAVPBlocks.TITANIUM_TREAD_SLAB,
            TempAVPBlocks.TITANIUM_TREAD_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            TempAVPBlocks.TITANIUM_GRATE,
            TempAVPBlocks.TITANIUM_GRATE_SLAB,
            TempAVPBlocks.TITANIUM_GRATE_STAIRS
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
