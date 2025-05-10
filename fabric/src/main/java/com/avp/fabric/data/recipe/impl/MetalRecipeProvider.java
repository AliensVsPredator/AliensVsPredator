package com.avp.fabric.data.recipe.impl;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.AVPItems;
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

        createSmeltAndBlastRecipes(builder, AVPBlocks.BAUXITE_ORE.get(), AVPItems.ALUMINUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPBlocks.GALENA_ORE.get(), AVPItems.LEAD_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPBlocks.MONAZITE_ORE.get(), AVPItems.NEODYMIUM_MAGNET.get());
        createSmeltAndBlastRecipes(builder, AVPBlocks.DEEPSLATE_TITANIUM_ORE.get(), AVPItems.TITANIUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPBlocks.ZINC_ORE.get(), AVPItems.ZINC_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPBlocks.DEEPSLATE_ZINC_ORE.get(), AVPItems.ZINC_INGOT.get());

        createSmeltAndBlastRecipes(builder, AVPItems.RAW_BAUXITE.get(), AVPItems.ALUMINUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_BRASS.get(), AVPItems.BRASS_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_FERROBAUXITE.get(), AVPItems.FERROALUMINUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_GALENA.get(), AVPItems.LEAD_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_MONAZITE.get(), AVPItems.NEODYMIUM_MAGNET.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_TITANIUM.get(), AVPItems.TITANIUM_INGOT.get());
        createSmeltAndBlastRecipes(builder, AVPItems.RAW_ZINC.get(), AVPItems.ZINC_INGOT.get());

        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.ALUMINUM_INGOT.get(), AVPBlocks.ALUMINUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.BRASS_INGOT.get(), AVPBlocks.BRASS_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(
            builder,
            AVPItems.FERROALUMINUM_INGOT.get(),
            AVPBlocks.FERROALUMINUM_BLOCK.get()
        );
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.LEAD_INGOT.get(), AVPBlocks.LEAD_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.STEEL_INGOT.get(), AVPBlocks.STEEL_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.TITANIUM_INGOT.get(), AVPBlocks.TITANIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.URANIUM_INGOT.get(), AVPBlocks.URANIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.ZINC_INGOT.get(), AVPBlocks.ZINC_BLOCK.get());

        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.AUTUNITE_DUST.get(), AVPBlocks.AUTUNITE_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.LITHIUM_DUST.get(), AVPBlocks.LITHIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_BAUXITE.get(), AVPBlocks.RAW_BAUXITE_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_GALENA.get(), AVPBlocks.RAW_GALENA_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_MONAZITE.get(), AVPBlocks.RAW_MONAZITE_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_TITANIUM.get(), AVPBlocks.RAW_TITANIUM_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.RAW_ZINC.get(), AVPBlocks.RAW_ZINC_BLOCK.get());
        RecipeUtil.createCompressedBlockRecipes3x3(builder, AVPItems.SILICON.get(), AVPBlocks.SILICON_BLOCK.get());

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BARS_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(16, AVPBlocks.STEEL_BARS);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT.get()))
            .into(2, AVPBlocks.FERROALUMINUM_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(2, AVPBlocks.STEEL_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.BUTTON_BLOCK.apply(AVPItems.TITANIUM_INGOT.get()))
            .into(2, AVPBlocks.TITANIUM_BUTTON);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT.get()))
            .into(3, AVPBlocks.FERROALUMINUM_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(3, AVPBlocks.STEEL_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.DOOR_BLOCK.apply(AVPItems.TITANIUM_INGOT.get()))
            .into(3, AVPBlocks.TITANIUM_DOOR);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT.get()))
            .into(2, AVPBlocks.FERROALUMINUM_TRAP_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(2, AVPBlocks.STEEL_TRAP_DOOR);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.TRAP_DOOR_BLOCK.apply(AVPItems.TITANIUM_INGOT.get()))
            .into(2, AVPBlocks.TITANIUM_TRAP_DOOR);

        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(AVPItems.FERROALUMINUM_INGOT.get()))
            .into(1, AVPBlocks.FERROALUMINUM_PRESSURE_PLATE);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(AVPItems.STEEL_INGOT.get()))
            .into(1, AVPBlocks.STEEL_PRESSURE_PLATE);
        builder.shaped()
            .withCategory(RecipeCategory.BUILDING_BLOCKS)
            .apply(RecipeTemplates.PRESSURE_PLATE_BLOCK.apply(AVPItems.TITANIUM_INGOT.get()))
            .into(1, AVPBlocks.TITANIUM_PRESSURE_PLATE);

        // Add standard slab and stair crafting recipes
        createStandardSlabRecipe(builder, AVPBlocks.FERROALUMINUM_BLOCK.get(), AVPBlocks.FERROALUMINUM_SLAB.get());
        createStandardStairRecipe(builder, AVPBlocks.FERROALUMINUM_BLOCK.get(), AVPBlocks.FERROALUMINUM_STAIRS.get());

        createStandardSlabRecipe(builder, AVPBlocks.STEEL_BLOCK.get(), AVPBlocks.STEEL_SLAB.get());
        createStandardStairRecipe(builder, AVPBlocks.STEEL_BLOCK.get(), AVPBlocks.STEEL_STAIRS.get());

        createStandardSlabRecipe(builder, AVPBlocks.TITANIUM_BLOCK.get(), AVPBlocks.TITANIUM_SLAB.get());
        createStandardStairRecipe(builder, AVPBlocks.TITANIUM_BLOCK.get(), AVPBlocks.TITANIUM_STAIRS.get());

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
        var steelBaseBuilder = builder.stonecut(AVPBlocks.STEEL_BLOCK.get())
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
        steelBaseBuilder.into(2, AVPBlocks.STEEL_SLAB.get());
        steelBaseBuilder.into(1, AVPBlocks.STEEL_STAIRS.get());

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
