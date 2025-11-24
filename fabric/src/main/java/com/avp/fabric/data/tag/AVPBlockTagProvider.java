package com.avp.fabric.data.tag;

import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.tag.AVPBlockTags;
import com.compat.CommonBlockTags;
import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanIndustrialConcreteBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanPaddingBlocks;
import com.human.common.registry.init.block.HumanPlasticBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AVPBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public AVPBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        var concreteTagBuilder = getOrCreateTagBuilder(AVPBlockTags.CONCRETE);

        Stream.of(
            AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB,
            AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(Supplier::get)
            .forEach(concreteTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
            .addOptionalTag(BlockTags.FEATURES_CANNOT_REPLACE)
            .add(
                Blocks.BARRIER,
                Blocks.BEDROCK,
                Blocks.CHAIN_COMMAND_BLOCK,
                Blocks.COMMAND_BLOCK,
                Blocks.END_GATEWAY,
                Blocks.END_PORTAL,
                Blocks.END_PORTAL_FRAME,
                Blocks.JIGSAW,
                Blocks.LIGHT,
                Blocks.MOVING_PISTON,
                Blocks.REINFORCED_DEEPSLATE,
                Blocks.REPEATING_COMMAND_BLOCK,
                Blocks.STRUCTURE_BLOCK
            );

        getOrCreateTagBuilder(AVPBlockTags.FERROALUMINUM)
            .add(
                HumanFerroaluminumBlocks.CHISELED_FERROALUMINUM.get(),
                HumanFerroaluminumBlocks.CUT_FERROALUMINUM.get(),
                HumanFerroaluminumBlocks.CUT_FERROALUMINUM_SLAB.get(),
                HumanFerroaluminumBlocks.CUT_FERROALUMINUM_STAIRS.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_BUTTON.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_COLUMN.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_DOOR.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_GRATE.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_SLAB.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_GRATE_STAIRS.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_PLATING.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_SLAB.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_PLATING_STAIRS.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_PRESSURE_PLATE.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_SIDING.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_SLAB.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_SIDING_STAIRS.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_SLAB.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_STAIRS.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_STANDING.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_SLAB.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_STANDING_STAIRS.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_TRAP_DOOR.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_TREAD.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_SLAB.get(),
                HumanFerroaluminumBlocks.FERROALUMINUM_TREAD_STAIRS.get()
            );

        var industrialConcreteTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_CONCRETE);

        Stream.of(
            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE,
            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB,
            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS,
            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(Supplier::get)
            .forEach(industrialConcreteTagBuilder::add);

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS.get());
        HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.values()
            .stream()
            .map(Supplier::get)
            .forEach(industrialGlassBlockTagBuilder::add);

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_PANE.get());
        HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.values()
            .stream()
            .map(Supplier::get)
            .forEach(industrialGlassPaneTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS_PANE)
            .add(
                HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_DOOR.get(),
                HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_SLAB.get(),
                HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_STAIRS.get(),
                HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_TRAP_DOOR.get()
            );

        var paddingTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PADDING);

        Stream.of(
            HumanPaddingBlocks.DYE_COLOR_TO_PADDING,
            HumanPaddingBlocks.DYE_COLOR_TO_PADDING_SLAB,
            HumanPaddingBlocks.DYE_COLOR_TO_PADDING_STAIRS,
            HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING,
            HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB,
            HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS,
            HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING,
            HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB,
            HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS,
            HumanPaddingBlocks.DYE_COLOR_TO_TILE_PADDING,
            HumanPaddingBlocks.DYE_COLOR_TO_TILE_PADDING_SLAB,
            HumanPaddingBlocks.DYE_COLOR_TO_TILE_PADDING_STAIRS
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(Supplier::get)
            .forEach(paddingTagBuilder::add);

        var plasticTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PLASTIC);

        TagProviderUtil.getPlasticBlockStream()
            .forEach(plasticTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.STEEL)
            .add(
                HumanSteelBlocks.CHISELED_STEEL.get(),
                HumanSteelBlocks.CUT_STEEL.get(),
                HumanSteelBlocks.CUT_STEEL_SLAB.get(),
                HumanSteelBlocks.CUT_STEEL_STAIRS.get(),
                HumanSteelBlocks.STEEL_BARS.get(),
                HumanSteelBlocks.STEEL_BLOCK.get(),
                HumanSteelBlocks.STEEL_BUTTON.get(),
                HumanSteelBlocks.STEEL_CHAIN_FENCE.get(),
                HumanSteelBlocks.STEEL_COLUMN.get(),
                HumanSteelBlocks.STEEL_DOOR.get(),
                HumanSteelBlocks.STEEL_FASTENED_SIDING.get(),
                HumanSteelBlocks.STEEL_FASTENED_SIDING_SLAB.get(),
                HumanSteelBlocks.STEEL_FASTENED_SIDING_STAIRS.get(),
                HumanSteelBlocks.STEEL_FASTENED_STANDING.get(),
                HumanSteelBlocks.STEEL_FASTENED_STANDING_SLAB.get(),
                HumanSteelBlocks.STEEL_FASTENED_STANDING_STAIRS.get(),
                HumanSteelBlocks.STEEL_GRATE.get(),
                HumanSteelBlocks.STEEL_GRATE_SLAB.get(),
                HumanSteelBlocks.STEEL_GRATE_STAIRS.get(),
                HumanSteelBlocks.STEEL_PLATING.get(),
                HumanSteelBlocks.STEEL_PLATING_SLAB.get(),
                HumanSteelBlocks.STEEL_PLATING_STAIRS.get(),
                HumanSteelBlocks.STEEL_PRESSURE_PLATE.get(),
                HumanSteelBlocks.STEEL_SIDING.get(),
                HumanSteelBlocks.STEEL_SIDING_SLAB.get(),
                HumanSteelBlocks.STEEL_SIDING_STAIRS.get(),
                HumanSteelBlocks.STEEL_SLAB.get(),
                HumanSteelBlocks.STEEL_STAIRS.get(),
                HumanSteelBlocks.STEEL_STANDING.get(),
                HumanSteelBlocks.STEEL_STANDING_SLAB.get(),
                HumanSteelBlocks.STEEL_STANDING_STAIRS.get(),
                HumanSteelBlocks.STEEL_TRAP_DOOR.get(),
                HumanSteelBlocks.STEEL_TREAD.get(),
                HumanSteelBlocks.STEEL_TREAD_SLAB.get(),
                HumanSteelBlocks.STEEL_TREAD_STAIRS.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.TITANIUM)
            .add(
                HumanTitaniumBlocks.CHISELED_TITANIUM.get(),
                HumanTitaniumBlocks.CUT_TITANIUM.get(),
                HumanTitaniumBlocks.CUT_TITANIUM_SLAB.get(),
                HumanTitaniumBlocks.CUT_TITANIUM_STAIRS.get(),
                CoreBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                CoreBlocks.RAW_TITANIUM_BLOCK.get(),
                HumanTitaniumBlocks.TITANIUM_BLOCK.get(),
                HumanTitaniumBlocks.TITANIUM_BUTTON.get(),
                HumanTitaniumBlocks.TITANIUM_CHAIN_FENCE.get(),
                HumanTitaniumBlocks.TITANIUM_COLUMN.get(),
                HumanTitaniumBlocks.TITANIUM_DOOR.get(),
                HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING.get(),
                HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_SLAB.get(),
                HumanTitaniumBlocks.TITANIUM_FASTENED_SIDING_STAIRS.get(),
                HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING.get(),
                HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_SLAB.get(),
                HumanTitaniumBlocks.TITANIUM_FASTENED_STANDING_STAIRS.get(),
                HumanTitaniumBlocks.TITANIUM_GRATE.get(),
                HumanTitaniumBlocks.TITANIUM_GRATE_SLAB.get(),
                HumanTitaniumBlocks.TITANIUM_GRATE_STAIRS.get(),
                HumanTitaniumBlocks.TITANIUM_PLATING.get(),
                HumanTitaniumBlocks.TITANIUM_PLATING_SLAB.get(),
                HumanTitaniumBlocks.TITANIUM_PLATING_STAIRS.get(),
                HumanTitaniumBlocks.TITANIUM_PRESSURE_PLATE.get(),
                HumanTitaniumBlocks.TITANIUM_SIDING.get(),
                HumanTitaniumBlocks.TITANIUM_SIDING_SLAB.get(),
                HumanTitaniumBlocks.TITANIUM_SIDING_STAIRS.get(),
                HumanTitaniumBlocks.TITANIUM_SLAB.get(),
                HumanTitaniumBlocks.TITANIUM_STAIRS.get(),
                HumanTitaniumBlocks.TITANIUM_STANDING.get(),
                HumanTitaniumBlocks.TITANIUM_STANDING_SLAB.get(),
                HumanTitaniumBlocks.TITANIUM_STANDING_STAIRS.get(),
                HumanTitaniumBlocks.TITANIUM_TRAP_DOOR.get(),
                HumanTitaniumBlocks.TITANIUM_TREAD.get(),
                HumanTitaniumBlocks.TITANIUM_TREAD_SLAB.get(),
                HumanTitaniumBlocks.TITANIUM_TREAD_STAIRS.get()
            );

        getOrCreateTagBuilder(BlockTags.CLIMBABLE)
            .add(
                HumanFerroaluminumBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                HumanSteelBlocks.STEEL_CHAIN_FENCE.get(),
                HumanTitaniumBlocks.TITANIUM_CHAIN_FENCE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .add(
                AVPBlocks.BLUEPRINT_BLOCK.get(),
                AVPBlocks.CABLE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            .addTag(AVPBlockTags.CONCRETE)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.STEEL)
            .addTag(AVPBlockTags.TITANIUM)
            .add(

                CoreBlocks.ALUMINUM_BLOCK.get(),
                AVPBlocks.AMMO_CHEST.get(),
                CoreBlocks.AUTUNITE_BLOCK.get(),
                CoreBlocks.AUTUNITE_ORE.get(),
                CoreBlocks.BAUXITE_ORE.get(),
                CoreBlocks.BRASS_BLOCK.get(),
                CoreBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                CoreBlocks.DEEPSLATE_ZINC_ORE.get(),
                AVPBlocks.DESK_TERMINAL_BLOCK.get(),
                CoreBlocks.GALENA_ORE.get(),
                AVPBlocks.INDUSTRIAL_FURNACE.get(),
                AVPBlocks.INFINITE_POWER_GENERATOR.get(),

                CoreBlocks.LEAD_BLOCK.get(),
                AVPBlocks.LEAD_CHEST.get(),
                CoreBlocks.LITHIUM_BLOCK.get(),
                CoreBlocks.LITHIUM_ORE.get(),
                CoreBlocks.MONAZITE_ORE.get(),

                AVPBlocks.BATTERY.get(),
                AVPBlocks.SOLAR_PANEL.get(),
                AVPBlocks.THERMAL_GENERATOR.get(),
                AVPBlocks.WIND_TURBINE.get(),

                AVPBlocks.NUKE_BLOCK.get(),
                CoreBlocks.RAW_BAUXITE_BLOCK.get(),
                CoreBlocks.RAW_GALENA_BLOCK.get(),
                CoreBlocks.RAW_MONAZITE_BLOCK.get(),
                CoreBlocks.RAW_ZINC_BLOCK.get(),
                AVPBlocks.REDSTONE_GENERATOR.get(),

                AVPBlocks.RESONATOR_BLOCK.get(),
                AVPBlocks.SENTRY_TURRET.get(),
                CoreBlocks.SILICON_BLOCK.get(),
                CoreBlocks.TRINITITE_BLOCK.get(),
                CoreBlocks.URANIUM_BLOCK.get(),
                CoreBlocks.ZINC_BLOCK.get(),
                CoreBlocks.ZINC_ORE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(
                CoreBlocks.ASH_BLOCK.get(),
                CoreBlocks.SILICA_GRAVEL.get()
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .add(
                CoreBlocks.BAUXITE_ORE.get(),
                AVPBlocks.BLUEPRINT_BLOCK.get(),
                AVPBlocks.CABLE.get(),
                CoreBlocks.GALENA_ORE.get(),
                CoreBlocks.RAW_BAUXITE_BLOCK.get()
            );

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.STEEL)
            .addTag(AVPBlockTags.TITANIUM)
            .add(
                AVPBlocks.AMMO_CHEST.get(),
                AVPBlocks.BATTERY.get(),
                CoreBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.DESK_TERMINAL_BLOCK.get(),
                AVPBlocks.INDUSTRIAL_FURNACE.get(),
                AVPBlocks.INFINITE_POWER_GENERATOR.get(),
                AVPBlocks.LEAD_CHEST.get(),
                AVPBlocks.NUKE_BLOCK.get(),
                CoreBlocks.RAW_TITANIUM_BLOCK.get(),
                AVPBlocks.REDSTONE_GENERATOR.get(),
                AVPBlocks.RESONATOR_BLOCK.get(),
                AVPBlocks.SENTRY_TURRET.get(),
                AVPBlocks.SOLAR_PANEL.get(),
                AVPBlocks.THERMAL_GENERATOR.get(),
                AVPBlocks.WIND_TURBINE.get(),
                CoreBlocks.TRINITITE_BLOCK.get(),
                CoreBlocks.URANIUM_BLOCK.get()
            );

        var buttonTagProvider = getOrCreateTagBuilder(BlockTags.BUTTONS);
        var doorTagProvider = getOrCreateTagBuilder(BlockTags.DOORS);
        var fenceTagProvider = getOrCreateTagBuilder(BlockTags.FENCES);
        var pressurePlateTagProvider = getOrCreateTagBuilder(BlockTags.PRESSURE_PLATES);
        var slabTagProvider = getOrCreateTagBuilder(BlockTags.SLABS);
        var stairsTagProvider = getOrCreateTagBuilder(BlockTags.STAIRS);
        var trapdoorTagProvider = getOrCreateTagBuilder(BlockTags.TRAPDOORS);
        var wallTagBuilder = getOrCreateTagBuilder(BlockTags.WALLS);

        AVPBlocks.getAll().forEach(deferredHolder -> {
            var block = deferredHolder.get();

            if (block instanceof ButtonBlock) {
                buttonTagProvider.add(block);
            }

            if (block instanceof DoorBlock) {
                doorTagProvider.add(block);
            }

            if (block instanceof FenceBlock) {
                fenceTagProvider.add(block);
            }

            if (block instanceof PressurePlateBlock) {
                pressurePlateTagProvider.add(block);
            }

            if (block instanceof SlabBlock) {
                slabTagProvider.add(block);
            }

            if (block instanceof StairBlock) {
                stairsTagProvider.add(block);
            }

            if (block instanceof TrapDoorBlock) {
                trapdoorTagProvider.add(block);
            }

            if (block instanceof WallBlock) {
                wallTagBuilder.add(block);
            }
        });

        getOrCreateTagBuilder(AVPBlockTags.MARINE_SPAWN_BLOCKS).add(
            HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING.get(DyeColor.BLACK).get(),
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(DyeColor.LIGHT_GRAY).get(),
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(DyeColor.GRAY).get(),
            HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(DyeColor.BLACK).get(),
            Blocks.CYAN_TERRACOTTA,
            Blocks.MOSS_BLOCK,
            HumanFerroaluminumBlocks.FERROALUMINUM_TREAD.get(),
            HumanSteelBlocks.STEEL_TREAD.get(),
            Blocks.GRAVEL,
            HumanSteelBlocks.STEEL_GRATE.get(),
            HumanTitaniumBlocks.TITANIUM_TREAD.get()
        );

        getOrCreateTagBuilder(AVPBlockTags.RAZOR_WIRE).add(AVPBlocks.RAZOR_WIRE.get());

        addCompatibilityTags();
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(CommonBlockTags.CHESTS)
            .setReplace(false)
            .add(
                AVPBlocks.AMMO_CHEST.get(),
                AVPBlocks.LEAD_CHEST.get()
            );

        getOrCreateTagBuilder(CommonBlockTags.ORES)
            .setReplace(false)
            .addTag(CommonBlockTags.ORES_ALUMINUM)
            .addTag(CommonBlockTags.ORES_AUTUNITE)
            .addTag(CommonBlockTags.ORES_LEAD)
            .addTag(CommonBlockTags.ORES_LITHIUM)
            .addTag(CommonBlockTags.ORES_MONAZITE)
            .addTag(CommonBlockTags.ORES_TITANIUM)
            .addTag(CommonBlockTags.ORES_ZINC);

        getOrCreateTagBuilder(CommonBlockTags.ORES_ALUMINUM)
            .setReplace(false)
            .addTag(CommonBlockTags.ORES_BAUXITE);

        getOrCreateTagBuilder(CommonBlockTags.ORES_AUTUNITE)
            .setReplace(false)
            .add(CoreBlocks.AUTUNITE_ORE.get());

        getOrCreateTagBuilder(CommonBlockTags.ORES_BAUXITE)
            .setReplace(false)
            .add(CoreBlocks.BAUXITE_ORE.get());

        getOrCreateTagBuilder(CommonBlockTags.ORES_GALENA)
            .setReplace(false)
            .add(CoreBlocks.GALENA_ORE.get());

        getOrCreateTagBuilder(CommonBlockTags.ORES_LEAD)
            .setReplace(false)
            .addTag(CommonBlockTags.ORES_GALENA);

        getOrCreateTagBuilder(CommonBlockTags.ORES_LITHIUM)
            .setReplace(false)
            .add(CoreBlocks.LITHIUM_ORE.get());

        getOrCreateTagBuilder(CommonBlockTags.ORES_MONAZITE)
            .setReplace(false)
            .add(CoreBlocks.MONAZITE_ORE.get());

        getOrCreateTagBuilder(CommonBlockTags.ORES_TITANIUM)
            .setReplace(false)
            .add(CoreBlocks.DEEPSLATE_TITANIUM_ORE.get());

        getOrCreateTagBuilder(CommonBlockTags.ORES_ZINC)
            .setReplace(false)
            .add(CoreBlocks.ZINC_ORE.get())
            .add(CoreBlocks.DEEPSLATE_ZINC_ORE.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS)
            .setReplace(false)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_ALUMINUM)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_BRASS)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_FERROALUMINUM)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_LEAD)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_RAW_ALUMINUM)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_RAW_LEAD)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_RAW_TITANIUM)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_RAW_ZINC)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_STEEL)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_TITANIUM)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_URANIUM)
            .addTag(CommonBlockTags.STORAGE_BLOCKS_ZINC);

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_ALUMINUM)
            .setReplace(false)
            .add(CoreBlocks.ALUMINUM_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_BRASS)
            .setReplace(false)
            .add(CoreBlocks.BRASS_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_FERROALUMINUM)
            .setReplace(false)
            .add(HumanFerroaluminumBlocks.FERROALUMINUM_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_LEAD)
            .setReplace(false)
            .add(CoreBlocks.LEAD_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_RAW_ALUMINUM)
            .setReplace(false)
            .add(CoreBlocks.RAW_BAUXITE_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_RAW_LEAD)
            .setReplace(false)
            .add(CoreBlocks.RAW_GALENA_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_RAW_TITANIUM)
            .setReplace(false)
            .add(CoreBlocks.RAW_TITANIUM_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_RAW_ZINC)
            .setReplace(false)
            .add(CoreBlocks.RAW_ZINC_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_STEEL)
            .setReplace(false)
            .add(HumanSteelBlocks.STEEL_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_TITANIUM)
            .setReplace(false)
            .add(HumanTitaniumBlocks.TITANIUM_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_URANIUM)
            .setReplace(false)
            .add(CoreBlocks.URANIUM_BLOCK.get());

        getOrCreateTagBuilder(CommonBlockTags.STORAGE_BLOCKS_ZINC)
            .setReplace(false)
            .add(CoreBlocks.ZINC_BLOCK.get());
    }
}
