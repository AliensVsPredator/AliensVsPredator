package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

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

        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.ALUMINUM_INGOT.get(), AVPBlocks.ALUMINUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.BRASS_INGOT.get(), AVPBlocks.BRASS_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.FERROALUMINUM_INGOT.get(), AVPBlocks.FERROALUMINUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.LEAD_INGOT.get(), AVPBlocks.LEAD_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.STEEL_INGOT.get(), AVPBlocks.STEEL_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.TITANIUM_INGOT.get(), AVPBlocks.TITANIUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.URANIUM_INGOT.get(), AVPBlocks.URANIUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.ZINC_INGOT.get(), AVPBlocks.ZINC_BLOCK);

        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.AUTUNITE_DUST.get(), AVPBlocks.AUTUNITE_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.LITHIUM_DUST.get(), AVPBlocks.LITHIUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_BAUXITE.get(), AVPBlocks.RAW_BAUXITE_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_GALENA.get(), AVPBlocks.RAW_GALENA_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_MONAZITE.get(), AVPBlocks.RAW_MONAZITE_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_SILICA.get(), AVPBlocks.RAW_SILICA_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_TITANIUM.get(), AVPBlocks.RAW_TITANIUM_BLOCK);
        RecipeUtil.createCompressedBlockRecipes3x3(builder, TempAVPItems.RAW_ZINC.get(), AVPBlocks.RAW_ZINC_BLOCK);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BARS_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(16, AVPBlocks.STEEL_BARS);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(TempAVPItems.FERROALUMINUM_INGOT.get()))
            .into(2, AVPBlocks.FERROALUMINUM_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(2, AVPBlocks.STEEL_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(2, AVPBlocks.TITANIUM_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(TempAVPItems.FERROALUMINUM_INGOT.get()))
            .into(3, AVPBlocks.FERROALUMINUM_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(3, AVPBlocks.STEEL_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(3, AVPBlocks.TITANIUM_DOOR);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(TempAVPItems.FERROALUMINUM_INGOT.get()))
            .into(2, AVPBlocks.FERROALUMINUM_TRAP_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(2, AVPBlocks.STEEL_TRAP_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(2, AVPBlocks.TITANIUM_TRAP_DOOR);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(TempAVPItems.FERROALUMINUM_INGOT.get()))
            .into(1, AVPBlocks.FERROALUMINUM_PRESSURE_PLATE);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(TempAVPItems.STEEL_INGOT.get()))
            .into(1, AVPBlocks.STEEL_PRESSURE_PLATE);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(TempAVPItems.TITANIUM_INGOT.get()))
            .into(1, AVPBlocks.TITANIUM_PRESSURE_PLATE);

        // Add standard slab and stair crafting recipes
        createStandardSlabRecipe(builder, AVPBlocks.FERROALUMINUM_BLOCK, AVPBlocks.FERROALUMINUM_SLAB);
        createStandardStairRecipe(builder, AVPBlocks.FERROALUMINUM_BLOCK, AVPBlocks.FERROALUMINUM_STAIRS);

        createStandardSlabRecipe(builder, AVPBlocks.STEEL_BLOCK, AVPBlocks.STEEL_SLAB);
        createStandardStairRecipe(builder, AVPBlocks.STEEL_BLOCK, AVPBlocks.STEEL_STAIRS);

        createStandardSlabRecipe(builder, AVPBlocks.TITANIUM_BLOCK, AVPBlocks.TITANIUM_SLAB);
        createStandardStairRecipe(builder, AVPBlocks.TITANIUM_BLOCK, AVPBlocks.TITANIUM_STAIRS);

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
        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.FERROALUMINUM_SIDING,
            AVPBlocks.FERROALUMINUM_SIDING_SLAB,
            AVPBlocks.FERROALUMINUM_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.FERROALUMINUM_STANDING,
            AVPBlocks.FERROALUMINUM_STANDING_SLAB,
            AVPBlocks.FERROALUMINUM_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.FERROALUMINUM_FASTENED_SIDING,
            AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB,
            AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.FERROALUMINUM_FASTENED_STANDING,
            AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB,
            AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.FERROALUMINUM_PLATING,
            AVPBlocks.FERROALUMINUM_PLATING_SLAB,
            AVPBlocks.FERROALUMINUM_PLATING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.FERROALUMINUM_TREAD,
            AVPBlocks.FERROALUMINUM_TREAD_SLAB,
            AVPBlocks.FERROALUMINUM_TREAD_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.FERROALUMINUM_GRATE,
            AVPBlocks.FERROALUMINUM_GRATE_SLAB,
            AVPBlocks.FERROALUMINUM_GRATE_STAIRS
        );

        // Steel variant slabs and stairs
        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.STEEL_SIDING,
            AVPBlocks.STEEL_SIDING_SLAB,
            AVPBlocks.STEEL_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.STEEL_STANDING,
            AVPBlocks.STEEL_STANDING_SLAB,
            AVPBlocks.STEEL_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.STEEL_FASTENED_SIDING,
            AVPBlocks.STEEL_FASTENED_SIDING_SLAB,
            AVPBlocks.STEEL_FASTENED_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.STEEL_FASTENED_STANDING,
            AVPBlocks.STEEL_FASTENED_STANDING_SLAB,
            AVPBlocks.STEEL_FASTENED_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.STEEL_PLATING,
            AVPBlocks.STEEL_PLATING_SLAB,
            AVPBlocks.STEEL_PLATING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.STEEL_TREAD,
            AVPBlocks.STEEL_TREAD_SLAB,
            AVPBlocks.STEEL_TREAD_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.STEEL_GRATE,
            AVPBlocks.STEEL_GRATE_SLAB,
            AVPBlocks.STEEL_GRATE_STAIRS
        );

        // Titanium variant slabs and stairs
        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.TITANIUM_SIDING,
            AVPBlocks.TITANIUM_SIDING_SLAB,
            AVPBlocks.TITANIUM_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.TITANIUM_STANDING,
            AVPBlocks.TITANIUM_STANDING_SLAB,
            AVPBlocks.TITANIUM_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.TITANIUM_FASTENED_SIDING,
            AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB,
            AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.TITANIUM_FASTENED_STANDING,
            AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB,
            AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.TITANIUM_PLATING,
            AVPBlocks.TITANIUM_PLATING_SLAB,
            AVPBlocks.TITANIUM_PLATING_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.TITANIUM_TREAD,
            AVPBlocks.TITANIUM_TREAD_SLAB,
            AVPBlocks.TITANIUM_TREAD_STAIRS
        );

        addVariantSlabAndStairRecipes(
            builder,
            AVPBlocks.TITANIUM_GRATE,
            AVPBlocks.TITANIUM_GRATE_SLAB,
            AVPBlocks.TITANIUM_GRATE_STAIRS
        );
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
