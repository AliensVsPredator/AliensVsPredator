package com.avp.fabric.common.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.avp.AVPResources;
import com.avp.common.block.BlockProperties;
import com.avp.common.block.BlockPropertyBuilder;
import com.avp.common.block.LithiumBlock;
import com.avp.fabric.common.block.resin.IrradiatedResinBlock;
import com.avp.fabric.common.block.resin.IrradiatedResinNodeBlock;
import com.avp.fabric.common.block.resin.IrradiatedResinVeinBlock;
import com.avp.fabric.common.block.resin.IrradiatedResinWebBlock;
import com.avp.fabric.common.block.resin.ResinBlock;
import com.avp.fabric.common.block.resin.ResinNodeBlock;
import com.avp.fabric.common.block.resin.ResinVeinBlock;
import com.avp.fabric.common.block.resin.ResinWebBlock;

public class AVPBlocks {

    private static final String[] MATERIALS = {
        "ferroaluminum",
        "steel",
        "titanium"
    };

    private static final String[] BLOCK_TYPES = {
        "siding",
        "fastened_siding",
        "fastened_standing",
        "plating",
        "tread",
        "grate"
    };

    public static final Block BLUEPRINT_BLOCK = register(BlockProperties.STEEL, "blueprint_block");

    public static final Block REDSTONE_GENERATOR = register(
        new RedstoneGeneratorBlock(BlockProperties.STEEL.build().randomTicks()),
        "redstone_generator"
    );

    public static final Block DESK_TERMINAL_BLOCK = register(
        new DeskTerminalBlock(BlockProperties.STEEL.build().noOcclusion()),
        "desk_terminal"
    );

    public static final Block TRIP_MINE_BLOCK = register(new TripMineBlock(BlockProperties.TITANIUM.build().noOcclusion()), "trip_mine");

    public static final Block RESONATOR_BLOCK = register(new ResonatorBlock(BlockProperties.STEEL.build().noOcclusion()), "resonator");

    public static final Block SENTRY_TURRET = register(new SentryTurretBlock(BlockProperties.STEEL.build().noOcclusion()), "sentry_turret");

    public static final Block ASH_BLOCK = register(new AshBlock(BlockProperties.ASH_BLOCK.build()), "ash_block");

    public static final Block NUKE_BLOCK = register(new NukeBlock(BlockProperties.NUKE.build()), "nuke");

    public static final Block ROYAL_JELLY_BLOCK = register(BlockProperties.JELLY, "royal_jelly_block");

    public static final Block TRINITITE_BLOCK = registerRadiatedBlock(BlockProperties.TRINITITE.build(), "trinitite_block");

    public static final Block ALUMINUM_BLOCK = register(BlockProperties.ALUMINUM, "aluminum_block");

    public static final Block AUTUNITE_BLOCK = registerRadiatedBlock(BlockProperties.AUTUNITE_ORE.build(), "autunite_block");

    public static final Block BRASS_BLOCK = register(BlockProperties.BRASS, "brass_block");

    public static final Block CHISELED_FERROALUMINUM = register(BlockProperties.FERROALUMINUM, "chiseled_ferroaluminum");

    public static final Block CHISELED_STEEL = register(BlockProperties.STEEL, "chiseled_steel");

    public static final Block CHISELED_TITANIUM = register(BlockProperties.TITANIUM, "chiseled_titanium");

    public static final Block CUT_FERROALUMINUM = register(BlockProperties.FERROALUMINUM, "cut_ferroaluminum");

    public static final Block CUT_FERROALUMINUM_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM.build()),
        "cut_ferroaluminum_slab"
    );

    public static final Block CUT_FERROALUMINUM_STAIRS = register(
        new StairBlock(
            CUT_FERROALUMINUM.defaultBlockState(),
            BlockProperties.FERROALUMINUM.build()
        ),
        "cut_ferroaluminum_stairs"
    );

    public static final Block CUT_STEEL = register(BlockProperties.STEEL, "cut_steel");

    public static final Block CUT_STEEL_SLAB = register(new SlabBlock(BlockProperties.STEEL.build()), "cut_steel_slab");

    public static final Block CUT_STEEL_STAIRS = register(
        new StairBlock(
            CUT_STEEL.defaultBlockState(),
            BlockProperties.STEEL.build()
        ),
        "cut_steel_stairs"
    );

    public static final Block CUT_TITANIUM = register(BlockProperties.TITANIUM, "cut_titanium");

    public static final Block CUT_TITANIUM_SLAB = register(new SlabBlock(BlockProperties.TITANIUM.build()), "cut_titanium_slab");

    public static final Block CUT_TITANIUM_STAIRS = register(
        new StairBlock(
            CUT_TITANIUM.defaultBlockState(),
            BlockProperties.TITANIUM.build()
        ),
        "cut_titanium_stairs"
    );

    public static final Block FERROALUMINUM_BLOCK = register(BlockProperties.FERROALUMINUM, "ferroaluminum_block");

    public static final Block FERROALUMINUM_CHAIN_FENCE = register(
        new IronBarsBlock(BlockProperties.FERROALUMINUM_BARS.build().sound(SoundType.CHAIN)),
        "ferroaluminum_chain_fence"
    );

    public static final Block FERROALUMINUM_COLUMN = register(
        new RotatedPillarBlock(BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_column"
    );

    public static final Block FERROALUMINUM_FASTENED_SIDING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_fastened_siding");

    public static final Block FERROALUMINUM_FASTENED_STANDING = register(
        BlockProperties.FERROALUMINUM,
        "ferroaluminum_fastened_standing"
    );

    public static final Block FERROALUMINUM_GRATE = register(
        new WaterloggedTransparentBlock(BlockProperties.FERROALUMINUM_GRATE.build()),
        "ferroaluminum_grate"
    );

    public static final Block FERROALUMINUM_PLATING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_plating");

    public static final Block FERROALUMINUM_SIDING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_siding");

    public static final Block FERROALUMINUM_STANDING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_standing");

    public static final Block FERROALUMINUM_TREAD = register(BlockProperties.FERROALUMINUM, "ferroaluminum_tread");

    public static final Block INDUSTRIAL_GLASS = register(
        new TransparentBlock(BlockProperties.INDUSTRIAL_GLASS.build()),
        "industrial_glass"
    );

    public static final Block INDUSTRIAL_GLASS_PANE = register(
        new IronBarsBlock(BlockProperties.INDUSTRIAL_GLASS_PANE.build()),
        "industrial_glass_pane"
    );

    public static final Block INDUSTRIAL_FURNACE = register(
        new IndustrialFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)),
        "industrial_furnace_block"
    );

    public static final Block LEAD_BLOCK = register(BlockProperties.LEAD, "lead_block");

    public static final Block LEAD_CHEST = register(new LeadChestBlock(BlockProperties.LEAD.build()), "lead_chest");

    public static final Block AMMO_CHEST = register(new AmmoChestBlock(BlockProperties.LEAD.build()), "ammo_chest");

    public static final Block LITHIUM_BLOCK = register(new LithiumBlock(BlockProperties.LITHIUM_ORE.build()), "lithium_block");

    public static final Block NETHER_RESIN = register(new ResinBlock(BlockProperties.NETHER_RESIN.build()), "nether_resin");

    public static final Block NETHER_RESIN_NODE = register(new ResinNodeBlock(BlockProperties.RESIN.build()), "nether_resin_node");

    public static final Block NETHER_RESIN_VEIN = register(
        new ResinVeinBlock(BlockProperties.NETHER_RESIN_VEIN.build()),
        "nether_resin_vein"
    );

    public static final Block NETHER_RESIN_WEB = register(new ResinWebBlock(BlockProperties.NETHER_RESIN_WEB.build()), "nether_resin_web");

    public static final Block ABERRANT_RESIN = register(new ResinBlock(BlockProperties.NETHER_RESIN.build()), "aberrant_resin");

    public static final Block ABERRANT_RESIN_NODE = register(new ResinNodeBlock(BlockProperties.RESIN.build()), "aberrant_resin_node");

    public static final Block ABERRANT_RESIN_VEIN = register(
        new ResinVeinBlock(BlockProperties.NETHER_RESIN_VEIN.build()),
        "aberrant_resin_vein"
    );

    public static final Block ABERRANT_RESIN_WEB = register(
        new ResinWebBlock(BlockProperties.NETHER_RESIN_WEB.build()),
        "aberrant_resin_web"
    );

    public static final Block IRRADIATED_RESIN = register(
        new IrradiatedResinBlock(BlockProperties.NETHER_RESIN.build()),
        "irradiated_resin"
    );

    public static final Block IRRADIATED_RESIN_NODE = register(
        new IrradiatedResinNodeBlock(BlockProperties.RESIN.build()),
        "irradiated_resin_node"
    );

    public static final Block IRRADIATED_RESIN_VEIN = register(
        new IrradiatedResinVeinBlock(BlockProperties.NETHER_RESIN_VEIN.build()),
        "irradiated_resin_vein"
    );

    public static final Block IRRADIATED_RESIN_WEB = register(
        new IrradiatedResinWebBlock(BlockProperties.NETHER_RESIN_WEB.build()),
        "irradiated_resin_web"
    );

    public static final Block RAW_BAUXITE_BLOCK = register(BlockProperties.BAUXITE_ORE, "raw_bauxite_block");

    public static final Block RAW_GALENA_BLOCK = register(BlockProperties.GALENA_ORE, "raw_galena_block");

    public static final Block RAW_MONAZITE_BLOCK = register(BlockProperties.MONAZITE_ORE, "raw_monazite_block");

    public static final Block RAW_SILICA_BLOCK = register(
        BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL).strength(0.9F),
        "raw_silica_block"
    );

    public static final Block RAW_TITANIUM_BLOCK = register(BlockProperties.TITANIUM_ORE, "raw_titanium_block");

    public static final Block RAW_ZINC_BLOCK = register(BlockProperties.ZINC_ORE, "raw_zinc_block");

    public static final Block RAZOR_WIRE = register(new RazorWireBlock(BlockProperties.RAZOR_WIRE.build()), "razor_wire");

    public static final Block RESIN = register(new ResinBlock(BlockProperties.RESIN.build()), "resin");

    public static final Block RESIN_NODE = register(new ResinNodeBlock(BlockProperties.RESIN.build()), "resin_node");

    public static final Block RESIN_VEIN = register(new ResinVeinBlock(BlockProperties.RESIN_VEIN.build()), "resin_vein");

    public static final Block RESIN_WEB = register(new ResinWebBlock(BlockProperties.RESIN_WEB.build()), "resin_web");

    public static final Block RESIN_BRICKS = register(BlockProperties.BRASS, "resin_bricks");

    public static final Block RESIN_O = register(BlockProperties.BRASS, "resin_o");

    public static final Block RESIN_RIBBED = register(BlockProperties.BRASS, "resin_ribbed");

    public static final Block RESIN_SMOOTH = register(BlockProperties.BRASS, "resin_smooth");

    public static final Block SILICA_GRAVEL = register(
        new ColoredFallingBlock(new ColorRGBA(-8356741), BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL)),
        "silica_gravel"
    );

    public static final Block STEEL_BARS = register(
        new IronBarsBlock(BlockProperties.STEEL_BARS.build()),
        "steel_bars"
    );

    public static final Block STEEL_BLOCK = register(BlockProperties.STEEL, "steel_block");

    public static final Block STEEL_CHAIN_FENCE = register(
        new IronBarsBlock(BlockProperties.STEEL_BARS.build().sound(SoundType.CHAIN)),
        "steel_chain_fence"
    );

    public static final Block STEEL_COLUMN = register(new RotatedPillarBlock(BlockProperties.STEEL.build()), "steel_column");

    public static final Block STEEL_FASTENED_SIDING = register(BlockProperties.STEEL, "steel_fastened_siding");

    public static final Block STEEL_FASTENED_STANDING = register(BlockProperties.STEEL, "steel_fastened_standing");

    public static final Block STEEL_GRATE = register(new WaterloggedTransparentBlock(BlockProperties.STEEL_GRATE.build()), "steel_grate");

    public static final Block STEEL_PLATING = register(BlockProperties.STEEL, "steel_plating");

    public static final Block STEEL_SIDING = register(BlockProperties.STEEL, "steel_siding");

    public static final Block STEEL_STANDING = register(BlockProperties.STEEL, "steel_standing");

    public static final Block STEEL_TREAD = register(BlockProperties.STEEL, "steel_tread");

    public static final Block TITANIUM_BLOCK = register(BlockProperties.TITANIUM, "titanium_block");

    public static final Block TITANIUM_CHAIN_FENCE = register(
        new IronBarsBlock(BlockProperties.TITANIUM_BARS.build().sound(SoundType.CHAIN)),
        "titanium_chain_fence"
    );

    public static final Block TITANIUM_COLUMN = register(new RotatedPillarBlock(BlockProperties.TITANIUM.build()), "titanium_column");

    public static final Block TITANIUM_FASTENED_SIDING = register(BlockProperties.TITANIUM, "titanium_fastened_siding");

    public static final Block TITANIUM_FASTENED_STANDING = register(BlockProperties.TITANIUM, "titanium_fastened_standing");

    public static final Block TITANIUM_GRATE = register(
        new WaterloggedTransparentBlock(BlockProperties.TITANIUM_GRATE.build()),
        "titanium_grate"
    );

    public static final Block TITANIUM_PLATING = register(BlockProperties.TITANIUM, "titanium_plating");

    public static final Block TITANIUM_SIDING = register(BlockProperties.TITANIUM, "titanium_siding");

    public static final Block TITANIUM_STANDING = register(BlockProperties.TITANIUM, "titanium_standing");

    public static final Block TITANIUM_TREAD = register(BlockProperties.TITANIUM, "titanium_tread");

    public static final Block URANIUM_BLOCK = registerRadiatedBlock(BlockProperties.URANIUM.build(), "uranium_block");

    public static final Block ZINC_BLOCK = register(BlockProperties.ZINC, "zinc_block");

    // Doors And Trapdoors
    public static final Block INDUSTRIAL_GLASS_DOOR = register(
        new DoorBlock(BlockSetType.COPPER, BlockProperties.INDUSTRIAL_GLASS.build().noOcclusion()),
        "industrial_glass_door"
    );

    public static final Block INDUSTRIAL_GLASS_TRAP_DOOR = register(
        new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.INDUSTRIAL_GLASS.build().noOcclusion()),
        "industrial_glass_trapdoor"
    );

    public static final Block FERROALUMINUM_DOOR = register(
        new DoorBlock(BlockSetType.COPPER, BlockProperties.FERROALUMINUM.build().noOcclusion()),
        "ferroaluminum_door"
    );

    public static final Block FERROALUMINUM_TRAP_DOOR = register(
        new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.FERROALUMINUM.build().noOcclusion()),
        "ferroaluminum_trapdoor"
    );

    public static final Block STEEL_DOOR = register(
        new DoorBlock(BlockSetType.COPPER, BlockProperties.STEEL.build().noOcclusion()),
        "steel_door"
    );

    public static final Block STEEL_TRAP_DOOR = register(
        new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.STEEL.build()),
        "steel_trapdoor"
    );

    public static final Block TITANIUM_DOOR = register(
        new DoorBlock(BlockSetType.COPPER, BlockProperties.TITANIUM.build().noOcclusion()),
        "titanium_door"
    );

    public static final Block TITANIUM_TRAP_DOOR = register(
        new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.TITANIUM.build()),
        "titanium_trapdoor"
    );

    // Pressure Plates And Buttons
    public static final Block FERROALUMINUM_BUTTON = register(
        new ButtonBlock(AVPBlockSetTypes.FERROALUMINUM, 20, BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_button"
    );

    public static final Block STEEL_BUTTON = register(
        new ButtonBlock(AVPBlockSetTypes.STEEL, 20, BlockProperties.STEEL.build()),
        "steel_button"
    );

    public static final Block TITANIUM_BUTTON = register(
        new ButtonBlock(AVPBlockSetTypes.TITANIUM, 20, BlockProperties.TITANIUM.build()),
        "titanium_button"
    );

    public static final Block FERROALUMINUM_PRESSURE_PLATE = register(
        new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_pressure_plate"
    );

    public static final Block STEEL_PRESSURE_PLATE = register(
        new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.STEEL.build()),
        "steel_pressure_plate"
    );

    public static final Block TITANIUM_PRESSURE_PLATE = register(
        new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.TITANIUM.build()),
        "titanium_pressure_plate"
    );

    // Slabs And Stairs
    public static final Block INDUSTRIAL_GLASS_SLAB = register(
        new SlabBlock(BlockProperties.INDUSTRIAL_GLASS.build()),
        "industrial_glass_slab"
    );

    public static final Block INDUSTRIAL_GLASS_STAIRS = register(
        new StairBlock(INDUSTRIAL_GLASS.defaultBlockState(), BlockProperties.INDUSTRIAL_GLASS.build()),
        "industrial_glass_stairs"
    );

    public static final Block FERROALUMINUM_SLAB = register(
        new SlabBlock(BlockBehaviour.Properties.ofFullCopy(FERROALUMINUM_BLOCK)),
        "ferroaluminum_slab"
    );

    public static final Block FERROALUMINUM_STAIRS = register(
        new StairBlock(FERROALUMINUM_BLOCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(FERROALUMINUM_BLOCK)),
        "ferroaluminum_stairs"
    );

    public static final Block STEEL_SLAB = register(new SlabBlock(BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK)), "steel_slab");

    public static final Block STEEL_STAIRS = register(
        new StairBlock(STEEL_BLOCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(STEEL_BLOCK)),
        "steel_stairs"
    );

    public static final Block TITANIUM_SLAB = register(
        new SlabBlock(BlockBehaviour.Properties.ofFullCopy(TITANIUM_BLOCK)),
        "titanium_slab"
    );

    public static final Block TITANIUM_STAIRS = register(
        new StairBlock(TITANIUM_BLOCK.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(TITANIUM_BLOCK)),
        "titanium_stairs"
    );

    // Siding Blocks - Slabs and Stairs
    public static final Block FERROALUMINUM_SIDING_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_siding_slab"
    );

    public static final Block FERROALUMINUM_SIDING_STAIRS = register(
        new StairBlock(FERROALUMINUM_SIDING.defaultBlockState(), BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_siding_stairs"
    );

    public static final Block STEEL_SIDING_SLAB = register(new SlabBlock(BlockProperties.STEEL.build()), "steel_siding_slab");

    public static final Block STEEL_SIDING_STAIRS = register(
        new StairBlock(STEEL_SIDING.defaultBlockState(), BlockProperties.STEEL.build()),
        "steel_siding_stairs"
    );

    public static final Block TITANIUM_SIDING_SLAB = register(new SlabBlock(BlockProperties.TITANIUM.build()), "titanium_siding_slab");

    public static final Block TITANIUM_SIDING_STAIRS = register(
        new StairBlock(TITANIUM_SIDING.defaultBlockState(), BlockProperties.TITANIUM.build()),
        "titanium_siding_stairs"
    );

    // Standing Blocks - Slabs And Stairs
    public static final Block FERROALUMINUM_STANDING_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_standing_slab"
    );

    public static final Block FERROALUMINUM_STANDING_STAIRS = register(
        new StairBlock(FERROALUMINUM_STANDING.defaultBlockState(), BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_standing_stairs"
    );

    public static final Block STEEL_STANDING_SLAB = register(new SlabBlock(BlockProperties.STEEL.build()), "steel_standing_slab");

    public static final Block STEEL_STANDING_STAIRS = register(
        new StairBlock(STEEL_STANDING.defaultBlockState(), BlockProperties.STEEL.build()),
        "steel_standing_stairs"
    );

    public static final Block TITANIUM_STANDING_SLAB = register(new SlabBlock(BlockProperties.TITANIUM.build()), "titanium_standing_slab");

    public static final Block TITANIUM_STANDING_STAIRS = register(
        new StairBlock(TITANIUM_STANDING.defaultBlockState(), BlockProperties.TITANIUM.build()),
        "titanium_standing_stairs"
    );

    // Fastened Siding - Slabs and Stairs
    public static final Block FERROALUMINUM_FASTENED_SIDING_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_fastened_siding_slab"
    );

    public static final Block FERROALUMINUM_FASTENED_SIDING_STAIRS = register(
        new StairBlock(FERROALUMINUM_FASTENED_SIDING.defaultBlockState(), BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_fastened_siding_stairs"
    );

    public static final Block STEEL_FASTENED_SIDING_SLAB = register(
        new SlabBlock(BlockProperties.STEEL.build()),
        "steel_fastened_siding_slab"
    );

    public static final Block STEEL_FASTENED_SIDING_STAIRS = register(
        new StairBlock(STEEL_FASTENED_SIDING.defaultBlockState(), BlockProperties.STEEL.build()),
        "steel_fastened_siding_stairs"
    );

    public static final Block TITANIUM_FASTENED_SIDING_SLAB = register(
        new SlabBlock(BlockProperties.TITANIUM.build()),
        "titanium_fastened_siding_slab"
    );

    public static final Block TITANIUM_FASTENED_SIDING_STAIRS = register(
        new StairBlock(TITANIUM_FASTENED_SIDING.defaultBlockState(), BlockProperties.TITANIUM.build()),
        "titanium_fastened_siding_stairs"
    );

    // Fastened Standing - Slabs and Stairs
    public static final Block FERROALUMINUM_FASTENED_STANDING_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_fastened_standing_slab"
    );

    public static final Block FERROALUMINUM_FASTENED_STANDING_STAIRS = register(
        new StairBlock(FERROALUMINUM_FASTENED_STANDING.defaultBlockState(), BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_fastened_standing_stairs"
    );

    public static final Block STEEL_FASTENED_STANDING_SLAB = register(
        new SlabBlock(BlockProperties.STEEL.build()),
        "steel_fastened_standing_slab"
    );

    public static final Block STEEL_FASTENED_STANDING_STAIRS = register(
        new StairBlock(STEEL_FASTENED_STANDING.defaultBlockState(), BlockProperties.STEEL.build()),
        "steel_fastened_standing_stairs"
    );

    public static final Block TITANIUM_FASTENED_STANDING_SLAB = register(
        new SlabBlock(BlockProperties.TITANIUM.build()),
        "titanium_fastened_standing_slab"
    );

    public static final Block TITANIUM_FASTENED_STANDING_STAIRS = register(
        new StairBlock(TITANIUM_FASTENED_STANDING.defaultBlockState(), BlockProperties.TITANIUM.build()),
        "titanium_fastened_standing_stairs"
    );

    // Plating - Slabs and Stairs
    public static final Block FERROALUMINUM_PLATING_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_plating_slab"
    );

    public static final Block FERROALUMINUM_PLATING_STAIRS = register(
        new StairBlock(FERROALUMINUM_PLATING.defaultBlockState(), BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_plating_stairs"
    );

    public static final Block STEEL_PLATING_SLAB = register(new SlabBlock(BlockProperties.STEEL.build()), "steel_plating_slab");

    public static final Block STEEL_PLATING_STAIRS = register(
        new StairBlock(STEEL_PLATING.defaultBlockState(), BlockProperties.STEEL.build()),
        "steel_plating_stairs"
    );

    public static final Block TITANIUM_PLATING_SLAB = register(new SlabBlock(BlockProperties.TITANIUM.build()), "titanium_plating_slab");

    public static final Block TITANIUM_PLATING_STAIRS = register(
        new StairBlock(TITANIUM_PLATING.defaultBlockState(), BlockProperties.TITANIUM.build()),
        "titanium_plating_stairs"
    );

    // Tread - Slabs and Stairs
    public static final Block FERROALUMINUM_TREAD_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_tread_slab"
    );

    public static final Block FERROALUMINUM_TREAD_STAIRS = register(
        new StairBlock(FERROALUMINUM_TREAD.defaultBlockState(), BlockProperties.FERROALUMINUM.build()),
        "ferroaluminum_tread_stairs"
    );

    public static final Block STEEL_TREAD_SLAB = register(new SlabBlock(BlockProperties.STEEL.build()), "steel_tread_slab");

    public static final Block STEEL_TREAD_STAIRS = register(
        new StairBlock(STEEL_TREAD.defaultBlockState(), BlockProperties.STEEL.build()),
        "steel_tread_stairs"
    );

    public static final Block TITANIUM_TREAD_SLAB = register(new SlabBlock(BlockProperties.TITANIUM.build()), "titanium_tread_slab");

    public static final Block TITANIUM_TREAD_STAIRS = register(
        new StairBlock(TITANIUM_TREAD.defaultBlockState(), BlockProperties.TITANIUM.build()),
        "titanium_tread_stairs"
    );

    // Grate - Slabs and Stairs
    public static final Block FERROALUMINUM_GRATE_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM.build().noOcclusion()),
        "ferroaluminum_grate_slab"
    );

    public static final Block FERROALUMINUM_GRATE_STAIRS = register(
        new StairBlock(FERROALUMINUM_GRATE.defaultBlockState(), BlockProperties.FERROALUMINUM.build().noOcclusion()),
        "ferroaluminum_grate_stairs"
    );

    public static final Block STEEL_GRATE_SLAB = register(new SlabBlock(BlockProperties.STEEL.build().noOcclusion()), "steel_grate_slab");

    public static final Block STEEL_GRATE_STAIRS = register(
        new StairBlock(STEEL_GRATE.defaultBlockState(), BlockProperties.STEEL.build().noOcclusion()),
        "steel_grate_stairs"
    );

    public static final Block TITANIUM_GRATE_SLAB = register(
        new SlabBlock(BlockProperties.TITANIUM.build().noOcclusion()),
        "titanium_grate_slab"
    );

    public static final Block TITANIUM_GRATE_STAIRS = register(
        new StairBlock(TITANIUM_GRATE.defaultBlockState(), BlockProperties.TITANIUM.build().noOcclusion()),
        "titanium_grate_stairs"
    );

    public static Block registerRadiatedBlock(BlockBehaviour.Properties properties, String id) {
        return register(new RadiatedBlock(properties), id);
    }

    public static Block register(BlockPropertyBuilder builder, String id) {
        return register(builder.build(), id);
    }

    public static Block register(BlockBehaviour.Properties properties, String id) {
        return register(new Block(properties), id);
    }

    public static Block register(Block block, String id) {
        return Registry.register(BuiltInRegistries.BLOCK, AVPResources.location(id), block);
    }

    public static void initialize() {}
}
