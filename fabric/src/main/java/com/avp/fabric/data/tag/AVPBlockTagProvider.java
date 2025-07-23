package com.avp.fabric.data.tag;

import com.alien.common.registry.init.block.AlienChitinBlocks;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.compat.gigeresque.common.registry.tag.GigBlockTags;
import com.human.common.registry.init.block.HumanFerroaluminumBlocks;
import com.human.common.registry.init.block.HumanIndustrialConcreteBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanPaddingBlocks;
import com.human.common.registry.init.block.HumanPlasticBlocks;
import com.human.common.registry.init.block.HumanSteelBlocks;
import com.human.common.registry.init.block.HumanTitaniumBlocks;
import com.predator.common.registry.init.PredatorBlocks;
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

import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.tag.AVPBlockTags;
import com.avp.fabric.data.compatibility.common.CommonConstants;

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
            HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(Supplier::get)
            .forEach(paddingTagBuilder::add);

        var plasticTagBuilder = getOrCreateTagBuilder(AVPBlockTags.PLASTIC);

        Stream.of(
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC,
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB,
            HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_SLAB,
            HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_STAIRS
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(Supplier::get)
            .forEach(plasticTagBuilder::add);

        getOrCreateTagBuilder(AVPBlockTags.IRRADIATED_RESIN)
            .add(
                AlienResinBlocks.IRRADIATED_RESIN.get(),
                AlienResinBlocks.IRRADIATED_RESIN_NODE.get(),
                AlienResinBlocks.IRRADIATED_RESIN_VEIN.get(),
                AlienResinBlocks.IRRADIATED_RESIN_WEB.get(),

                AlienResinBlocks.IRRADIATED_RESIN_BRICKS.get(),
                AlienResinBlocks.IRRADIATED_RESIN_BRICK_SLAB.get(),
                AlienResinBlocks.IRRADIATED_RESIN_BRICK_STAIRS.get(),
                AlienResinBlocks.IRRADIATED_RESIN_BRICK_WALL.get(),
                AlienResinBlocks.IRRADIATED_RESIN_VENT.get(),
                AlienResinBlocks.RIBBED_IRRADIATED_RESIN.get(),
                AlienResinBlocks.SMOOTH_IRRADIATED_RESIN.get(),
                AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_SLAB.get(),
                AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_STAIRS.get(),
                AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_WALL.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.ABERRANT_RESIN)
            .add(
                AlienResinBlocks.ABERRANT_RESIN.get(),
                AlienResinBlocks.ABERRANT_RESIN_SLAB.get(),
                AlienResinBlocks.ABERRANT_RESIN_STAIRS.get(),
                AlienResinBlocks.ABERRANT_RESIN_NODE.get(),
                AlienResinBlocks.ABERRANT_RESIN_VEIN.get(),
                AlienResinBlocks.ABERRANT_RESIN_WEB.get(),

                AlienResinBlocks.ABERRANT_RESIN_BRICKS.get(),
                AlienResinBlocks.ABERRANT_RESIN_BRICK_SLAB.get(),
                AlienResinBlocks.ABERRANT_RESIN_BRICK_STAIRS.get(),
                AlienResinBlocks.ABERRANT_RESIN_BRICK_WALL.get(),
                AlienResinBlocks.ABERRANT_RESIN_VENT.get(),
                AlienResinBlocks.RIBBED_ABERRANT_RESIN.get(),
                AlienResinBlocks.SMOOTH_ABERRANT_RESIN.get(),
                AlienResinBlocks.SMOOTH_ABERRANT_RESIN_SLAB.get(),
                AlienResinBlocks.SMOOTH_ABERRANT_RESIN_STAIRS.get(),
                AlienResinBlocks.SMOOTH_ABERRANT_RESIN_WALL.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.NETHER_RESIN)
            .add(
                AlienResinBlocks.NETHER_RESIN.get(),
                AlienResinBlocks.NETHER_RESIN_SLAB.get(),
                AlienResinBlocks.NETHER_RESIN_STAIRS.get(),
                AlienResinBlocks.NETHER_RESIN_NODE.get(),
                AlienResinBlocks.NETHER_RESIN_VEIN.get(),
                AlienResinBlocks.NETHER_RESIN_WEB.get(),

                AlienResinBlocks.NETHER_RESIN_BRICKS.get(),
                AlienResinBlocks.NETHER_RESIN_BRICK_SLAB.get(),
                AlienResinBlocks.NETHER_RESIN_BRICK_STAIRS.get(),
                AlienResinBlocks.NETHER_RESIN_BRICK_WALL.get(),
                AlienResinBlocks.NETHER_RESIN_VENT.get(),
                AlienResinBlocks.RIBBED_NETHER_RESIN.get(),
                AlienResinBlocks.SMOOTH_NETHER_RESIN.get(),
                AlienResinBlocks.SMOOTH_NETHER_RESIN_SLAB.get(),
                AlienResinBlocks.SMOOTH_NETHER_RESIN_STAIRS.get(),
                AlienResinBlocks.SMOOTH_NETHER_RESIN_WALL.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.NORMAL_RESIN)
            .add(
                AlienResinBlocks.RESIN.get(),
                AlienResinBlocks.RESIN_SLAB.get(),
                AlienResinBlocks.RESIN_STAIRS.get(),
                AlienResinBlocks.RESIN_NODE.get(),
                AlienResinBlocks.RESIN_VEIN.get(),
                AlienResinBlocks.RESIN_WEB.get(),

                AlienResinBlocks.RESIN_BRICKS.get(),
                AlienResinBlocks.RESIN_BRICK_SLAB.get(),
                AlienResinBlocks.RESIN_BRICK_STAIRS.get(),
                AlienResinBlocks.RESIN_BRICK_WALL.get(),
                AlienResinBlocks.RESIN_VENT.get(),
                AlienResinBlocks.RIBBED_RESIN.get(),
                AlienResinBlocks.SMOOTH_RESIN.get(),
                AlienResinBlocks.SMOOTH_RESIN_SLAB.get(),
                AlienResinBlocks.SMOOTH_RESIN_STAIRS.get(),
                AlienResinBlocks.SMOOTH_RESIN_WALL.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.ABERRANT_CHITIN)
            .add(
                AlienChitinBlocks.ABERRANT_CHITIN_BLOCK.get(),
                AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_SLAB.get(),
                AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_STAIRS.get(),
                AlienChitinBlocks.ABERRANT_CHITIN_BLOCK_WALL.get(),
                AlienChitinBlocks.ABERRANT_CHITIN_BRICKS.get(),
                AlienChitinBlocks.ABERRANT_CHITIN_BRICK_SLAB.get(),
                AlienChitinBlocks.ABERRANT_CHITIN_BRICK_STAIRS.get(),
                AlienChitinBlocks.ABERRANT_CHITIN_BRICK_WALL.get(),
                AlienChitinBlocks.CHISELED_ABERRANT_CHITIN_BRICKS.get(),
                AlienChitinBlocks.CHISELED_ABERRANT_CHITIN_BRICKS_EMBRYO.get(),
                AlienChitinBlocks.POLISHED_ABERRANT_CHITIN.get(),
                AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_SLAB.get(),
                AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_STAIRS.get(),
                AlienChitinBlocks.POLISHED_ABERRANT_CHITIN_WALL.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.NETHER_CHITIN)
            .add(

                AlienChitinBlocks.NETHER_CHITIN_BLOCK.get(),
                AlienChitinBlocks.NETHER_CHITIN_BLOCK_SLAB.get(),
                AlienChitinBlocks.NETHER_CHITIN_BLOCK_STAIRS.get(),
                AlienChitinBlocks.NETHER_CHITIN_BLOCK_WALL.get(),
                AlienChitinBlocks.NETHER_CHITIN_BRICKS.get(),
                AlienChitinBlocks.NETHER_CHITIN_BRICK_SLAB.get(),
                AlienChitinBlocks.NETHER_CHITIN_BRICK_STAIRS.get(),
                AlienChitinBlocks.NETHER_CHITIN_BRICK_WALL.get(),
                AlienChitinBlocks.CHISELED_NETHER_CHITIN_BRICKS.get(),
                AlienChitinBlocks.CHISELED_NETHER_CHITIN_BRICKS_EMBRYO.get(),
                AlienChitinBlocks.POLISHED_NETHER_CHITIN.get(),
                AlienChitinBlocks.POLISHED_NETHER_CHITIN_SLAB.get(),
                AlienChitinBlocks.POLISHED_NETHER_CHITIN_STAIRS.get(),
                AlienChitinBlocks.POLISHED_NETHER_CHITIN_WALL.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.NORMAL_CHITIN)
            .add(
                AlienChitinBlocks.CHITIN_BLOCK.get(),
                AlienChitinBlocks.CHITIN_BLOCK_SLAB.get(),
                AlienChitinBlocks.CHITIN_BLOCK_STAIRS.get(),
                AlienChitinBlocks.CHITIN_BLOCK_WALL.get(),
                AlienChitinBlocks.CHITIN_BRICKS.get(),
                AlienChitinBlocks.CHITIN_BRICK_SLAB.get(),
                AlienChitinBlocks.CHITIN_BRICK_STAIRS.get(),
                AlienChitinBlocks.CHITIN_BRICK_WALL.get(),
                AlienChitinBlocks.CHISELED_CHITIN_BRICKS.get(),
                AlienChitinBlocks.CHISELED_CHITIN_BRICKS_EMBRYO.get(),
                AlienChitinBlocks.POLISHED_CHITIN.get(),
                AlienChitinBlocks.POLISHED_CHITIN_SLAB.get(),
                AlienChitinBlocks.POLISHED_CHITIN_STAIRS.get(),
                AlienChitinBlocks.POLISHED_CHITIN_WALL.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.CHITIN)
            .addTag(AVPBlockTags.ABERRANT_CHITIN)
            .addTag(AVPBlockTags.NETHER_CHITIN)
            .addTag(AVPBlockTags.NORMAL_CHITIN);

        getOrCreateTagBuilder(AVPBlockTags.RESIN)
            .addTag(AVPBlockTags.ABERRANT_RESIN)
            .addTag(AVPBlockTags.IRRADIATED_RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.RESIN_BLOCKS)
            .add(
                AlienResinBlocks.ABERRANT_RESIN.get(),
                AlienResinBlocks.IRRADIATED_RESIN.get(),
                AlienResinBlocks.NETHER_RESIN.get(),
                AlienResinBlocks.RESIN.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN_NODES)
            .add(
                AlienResinBlocks.ABERRANT_RESIN_NODE.get(),
                AlienResinBlocks.IRRADIATED_RESIN_NODE.get(),
                AlienResinBlocks.NETHER_RESIN_NODE.get(),
                AlienResinBlocks.RESIN_NODE.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN_REPLACEABLE)
            .addOptionalTag(BlockTags.BASE_STONE_NETHER)
            .addOptionalTag(BlockTags.BASE_STONE_OVERWORLD)
            .addOptionalTag(BlockTags.DIRT)
            .addOptionalTag(BlockTags.NYLIUM)
            .addOptionalTag(BlockTags.TERRACOTTA)
            .add(
                Blocks.CALCITE,
                Blocks.CLAY,
                Blocks.DRIPSTONE_BLOCK,
                Blocks.END_STONE,
                Blocks.GRAVEL,
                Blocks.RED_SAND,
                Blocks.RED_SANDSTONE,
                Blocks.SAND,
                Blocks.SANDSTONE,
                Blocks.SMOOTH_BASALT,
                Blocks.SOUL_SAND,
                Blocks.SOUL_SOIL
            );

        getOrCreateTagBuilder(AVPBlockTags.ABERRANT_RESIN_REPLACEABLE)
            .addTag(AVPBlockTags.RESIN_REPLACEABLE)
            .addTag(AVPBlockTags.IRRADIATED_RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.IRRADIATED_RESIN_REPLACEABLE)
            .addTag(AVPBlockTags.RESIN_REPLACEABLE)
            .addTag(AVPBlockTags.ABERRANT_RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.NETHER_RESIN_REPLACEABLE)
            .addTag(AVPBlockTags.RESIN_REPLACEABLE)
            .addTag(AVPBlockTags.ABERRANT_RESIN)
            .addTag(AVPBlockTags.IRRADIATED_RESIN)
            .addTag(AVPBlockTags.NORMAL_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.NORMAL_RESIN_REPLACEABLE)
            .addTag(AVPBlockTags.RESIN_REPLACEABLE)
            .addTag(AVPBlockTags.ABERRANT_RESIN)
            .addTag(AVPBlockTags.IRRADIATED_RESIN)
            .addTag(AVPBlockTags.NETHER_RESIN);

        getOrCreateTagBuilder(AVPBlockTags.RESIN_VEINS)
            .add(
                AlienResinBlocks.ABERRANT_RESIN_VEIN.get(),
                AlienResinBlocks.IRRADIATED_RESIN_VEIN.get(),
                AlienResinBlocks.NETHER_RESIN_VEIN.get(),
                AlienResinBlocks.RESIN_VEIN.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN_VENTS)
            .add(
                AlienResinBlocks.ABERRANT_RESIN_VENT.get(),
                AlienResinBlocks.IRRADIATED_RESIN_VENT.get(),
                AlienResinBlocks.NETHER_RESIN_VENT.get(),
                AlienResinBlocks.RESIN_VENT.get()
            );

        getOrCreateTagBuilder(AVPBlockTags.RESIN_WEBS)
            .add(
                AlienResinBlocks.ABERRANT_RESIN_WEB.get(),
                AlienResinBlocks.IRRADIATED_RESIN_WEB.get(),
                AlienResinBlocks.NETHER_RESIN_WEB.get(),
                AlienResinBlocks.RESIN_WEB.get()
            );

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

        // Acid-immune blocks
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addTag(AVPBlockTags.CHITIN)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .addTag(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
            .add(Blocks.AIR)
            .add(Blocks.FIRE)
            .add(Blocks.SOUL_FIRE);

        getOrCreateTagBuilder(AVPBlockTags.NETHER_ACID_IMMUNE)
            .addOptionalTag(BlockTags.INFINIBURN_NETHER)
            .addTag(AVPBlockTags.ACID_IMMUNE);

        getOrCreateTagBuilder(AVPBlockTags.IRRADIATED_ACID_IMMUNE)
            .addTag(AVPBlockTags.ACID_IMMUNE)
            .add(
                Blocks.BLUE_ICE
            );

        getOrCreateTagBuilder(AVPBlockTags.XENOMORPH_IMMUNE)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
            .addTag(AVPBlockTags.RESIN_VENTS)
            .addTag(AVPBlockTags.RESIN_WEBS);

        getOrCreateTagBuilder(BlockTags.CLIMBABLE)
            .add(
                HumanFerroaluminumBlocks.FERROALUMINUM_CHAIN_FENCE.get(),
                HumanSteelBlocks.STEEL_CHAIN_FENCE.get(),
                HumanTitaniumBlocks.TITANIUM_CHAIN_FENCE.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .addTag(AVPBlockTags.RESIN_VEINS)
            .addTag(AVPBlockTags.RESIN_WEBS)
            .add(
                AVPBlocks.BLUEPRINT_BLOCK.get()
            );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            .addTag(AVPBlockTags.CHITIN)
            .addTag(AVPBlockTags.CONCRETE)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.INDUSTRIAL_CONCRETE)
            .addTag(AVPBlockTags.INDUSTRIAL_GLASS)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.STEEL)
            .addTag(AVPBlockTags.TITANIUM)
            .add(
                AlienResinBlocks.ABERRANT_RESIN.get(),
                AlienResinBlocks.ABERRANT_RESIN_SLAB.get(),
                AlienResinBlocks.ABERRANT_RESIN_STAIRS.get(),
                AlienResinBlocks.ABERRANT_RESIN_BRICKS.get(),
                AlienResinBlocks.ABERRANT_RESIN_BRICK_SLAB.get(),
                AlienResinBlocks.ABERRANT_RESIN_BRICK_STAIRS.get(),
                AlienResinBlocks.ABERRANT_RESIN_BRICK_WALL.get(),
                AlienResinBlocks.ABERRANT_RESIN_NODE.get(),
                AlienResinBlocks.ABERRANT_RESIN_VENT.get(),
                AlienResinBlocks.RIBBED_ABERRANT_RESIN.get(),
                AlienResinBlocks.SMOOTH_ABERRANT_RESIN.get(),
                AlienResinBlocks.SMOOTH_ABERRANT_RESIN_SLAB.get(),
                AlienResinBlocks.SMOOTH_ABERRANT_RESIN_STAIRS.get(),
                AlienResinBlocks.SMOOTH_ABERRANT_RESIN_WALL.get(),

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

                AlienResinBlocks.IRRADIATED_RESIN.get(),
                AlienResinBlocks.IRRADIATED_RESIN_SLAB.get(),
                AlienResinBlocks.IRRADIATED_RESIN_STAIRS.get(),
                AlienResinBlocks.IRRADIATED_RESIN_BRICKS.get(),
                AlienResinBlocks.IRRADIATED_RESIN_BRICK_SLAB.get(),
                AlienResinBlocks.IRRADIATED_RESIN_BRICK_STAIRS.get(),
                AlienResinBlocks.IRRADIATED_RESIN_BRICK_WALL.get(),
                AlienResinBlocks.IRRADIATED_RESIN_NODE.get(),
                AlienResinBlocks.IRRADIATED_RESIN_VENT.get(),
                AlienResinBlocks.RIBBED_IRRADIATED_RESIN.get(),
                AlienResinBlocks.SMOOTH_IRRADIATED_RESIN.get(),
                AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_SLAB.get(),
                AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_STAIRS.get(),
                AlienResinBlocks.SMOOTH_IRRADIATED_RESIN_WALL.get(),

                CoreBlocks.LEAD_BLOCK.get(),
                AVPBlocks.LEAD_CHEST.get(),
                CoreBlocks.LITHIUM_BLOCK.get(),
                CoreBlocks.LITHIUM_ORE.get(),
                CoreBlocks.MONAZITE_ORE.get(),

                AlienResinBlocks.NETHER_RESIN.get(),
                AlienResinBlocks.NETHER_RESIN_SLAB.get(),
                AlienResinBlocks.NETHER_RESIN_STAIRS.get(),
                AlienResinBlocks.NETHER_RESIN_BRICKS.get(),
                AlienResinBlocks.NETHER_RESIN_BRICK_SLAB.get(),
                AlienResinBlocks.NETHER_RESIN_BRICK_STAIRS.get(),
                AlienResinBlocks.NETHER_RESIN_BRICK_WALL.get(),
                AlienResinBlocks.NETHER_RESIN_NODE.get(),
                AlienResinBlocks.NETHER_RESIN_VENT.get(),
                AlienResinBlocks.RIBBED_NETHER_RESIN.get(),
                AlienResinBlocks.SMOOTH_NETHER_RESIN.get(),
                AlienResinBlocks.SMOOTH_NETHER_RESIN_SLAB.get(),
                AlienResinBlocks.SMOOTH_NETHER_RESIN_STAIRS.get(),
                AlienResinBlocks.SMOOTH_NETHER_RESIN_WALL.get(),

                AVPBlocks.NUKE_BLOCK.get(),
                CoreBlocks.RAW_BAUXITE_BLOCK.get(),
                CoreBlocks.RAW_GALENA_BLOCK.get(),
                CoreBlocks.RAW_MONAZITE_BLOCK.get(),
                CoreBlocks.RAW_ZINC_BLOCK.get(),
                AVPBlocks.REDSTONE_GENERATOR.get(),

                AlienResinBlocks.RESIN.get(),
                AlienResinBlocks.RESIN_SLAB.get(),
                AlienResinBlocks.RESIN_STAIRS.get(),
                AlienResinBlocks.RESIN_BRICKS.get(),
                AlienResinBlocks.RESIN_BRICK_SLAB.get(),
                AlienResinBlocks.RESIN_BRICK_STAIRS.get(),
                AlienResinBlocks.RESIN_BRICK_WALL.get(),
                AlienResinBlocks.RESIN_NODE.get(),
                AlienResinBlocks.RESIN_VENT.get(),
                AlienResinBlocks.RIBBED_RESIN.get(),
                AlienResinBlocks.SMOOTH_RESIN.get(),
                AlienResinBlocks.SMOOTH_RESIN_SLAB.get(),
                AlienResinBlocks.SMOOTH_RESIN_STAIRS.get(),
                AlienResinBlocks.SMOOTH_RESIN_WALL.get(),

                AVPBlocks.RESONATOR_BLOCK.get(),
                AVPBlocks.SENTRY_TURRET.get(),
                CoreBlocks.SILICON_BLOCK.get(),
                CoreBlocks.TRINITITE_BLOCK.get(),
                PredatorBlocks.TRIP_MINE_BLOCK.get(),
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
            .addTag(AVPBlockTags.CHITIN)
            .addTag(AVPBlockTags.FERROALUMINUM)
            .addTag(AVPBlockTags.PADDING)
            .addTag(AVPBlockTags.PLASTIC)
            .addTag(AVPBlockTags.RESIN)
            .add(
                CoreBlocks.BAUXITE_ORE.get(),
                AVPBlocks.BLUEPRINT_BLOCK.get(),
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
                CoreBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlocks.DESK_TERMINAL_BLOCK.get(),
                AVPBlocks.INDUSTRIAL_FURNACE.get(),
                AVPBlocks.LEAD_CHEST.get(),
                AVPBlocks.NUKE_BLOCK.get(),
                CoreBlocks.RAW_TITANIUM_BLOCK.get(),
                AVPBlocks.REDSTONE_GENERATOR.get(),
                AVPBlocks.RESONATOR_BLOCK.get(),
                AVPBlocks.SENTRY_TURRET.get(),
                CoreBlocks.TRINITITE_BLOCK.get(),
                PredatorBlocks.TRIP_MINE_BLOCK.get(),
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
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addOptionalTag(GigBlockTags.ACID_RESISTANT);

        getOrCreateTagBuilder(CommonConstants.CHESTS)
            .setReplace(false)
            .add(
                AVPBlocks.AMMO_CHEST.get(),
                AVPBlocks.LEAD_CHEST.get()
            );

        getOrCreateTagBuilder(CommonConstants.ORES_BLOCKS)
            .setReplace(false)
            .add(
                CoreBlocks.AUTUNITE_ORE.get(),
                CoreBlocks.BAUXITE_ORE.get(),
                CoreBlocks.DEEPSLATE_TITANIUM_ORE.get(),
                CoreBlocks.DEEPSLATE_ZINC_ORE.get(),
                CoreBlocks.GALENA_ORE.get(),
                CoreBlocks.LITHIUM_ORE.get(),
                CoreBlocks.MONAZITE_ORE.get(),
                CoreBlocks.ZINC_ORE.get()
            );
    }
}
