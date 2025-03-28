package com.avp.common.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.avp.AVPResources;
import com.avp.common.block.resin.*;

public class AVPBlocks {

    public static final Map<String, Block> BLOCKS = new HashMap<>();

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

    public static final Block REDSTONE_GENERATOR = register(new RedstoneGeneratorBlock(BlockProperties.STEEL.randomTicks()), "redstone_generator");

    public static final Block DESK_TERMINAL_BLOCK = register(new DeskTerminalBlock(BlockProperties.STEEL.noOcclusion()), "desk_terminal");

    public static final Block TRIP_MINE_BLOCK = register(new TripMineBlock(BlockProperties.TITANIUM.noOcclusion()), "trip_mine");

    public static final Block RESONATOR_BLOCK = register(new ResonatorBlock(BlockProperties.STEEL.noOcclusion()), "resonator");

    public static final Block SENTRY_TURRET = register(new SentryTurretBlock(BlockProperties.STEEL.noOcclusion()), "sentry_turret");

    public static final Block ASH_BLOCK = register(new AshBlock(BlockProperties.ASH_BLOCK), "ash_block");

    public static final Block NUKE_BLOCK = register(new NukeBlock(BlockProperties.NUKE), "nuke");

    public static final Block ROYAL_JELLY_BLOCK = register(BlockProperties.JELLY, "royal_jelly_block");

    public static final Block TRINITITE_BLOCK = registerRadiatedBlock(BlockProperties.TRINITITE, "trinitite_block");

    public static final Block ALUMINUM_BLOCK = register(BlockProperties.ALUMINUM, "aluminum_block");

    public static final Block AUTUNITE_BLOCK = registerRadiatedBlock(BlockProperties.AUTUNITE_ORE, "autunite_block");

    public static final Block AUTUNITE_ORE = registerRadiatedBlock(BlockProperties.AUTUNITE_ORE, "autunite_ore");

    public static final Block BAUXITE_ORE = register(BlockProperties.BAUXITE_ORE, "bauxite_ore");

    public static final Block BRASS_BLOCK = register(BlockProperties.BRASS, "brass_block");

    public static final Block CHISELED_FERROALUMINUM = register(BlockProperties.FERROALUMINUM, "chiseled_ferroaluminum");

    public static final Block CHISELED_STEEL = register(BlockProperties.STEEL, "chiseled_steel");

    public static final Block CHISELED_TITANIUM = register(BlockProperties.TITANIUM, "chiseled_titanium");

    public static final Block CUT_FERROALUMINUM = register(BlockProperties.FERROALUMINUM, "cut_ferroaluminum");

    public static final Block CUT_FERROALUMINUM_SLAB = register(new SlabBlock(BlockProperties.FERROALUMINUM), "cut_ferroaluminum_slab");

    public static final Block CUT_FERROALUMINUM_STAIRS = register(
        new StairBlock(
            CUT_FERROALUMINUM.defaultBlockState(),
            BlockProperties.FERROALUMINUM
        ),
        "cut_ferroaluminum_stairs"
    );

    public static final Block CUT_STEEL = register(BlockProperties.STEEL, "cut_steel");

    public static final Block CUT_STEEL_SLAB = register(new SlabBlock(BlockProperties.STEEL), "cut_steel_slab");

    public static final Block CUT_STEEL_STAIRS = register(
        new StairBlock(
            CUT_STEEL.defaultBlockState(),
            BlockProperties.STEEL
        ),
        "cut_steel_stairs"
    );

    public static final Block CUT_TITANIUM = register(BlockProperties.TITANIUM, "cut_titanium");

    public static final Block CUT_TITANIUM_SLAB = register(new SlabBlock(BlockProperties.TITANIUM), "cut_titanium_slab");

    public static final Block CUT_TITANIUM_STAIRS = register(
        new StairBlock(
            CUT_TITANIUM.defaultBlockState(),
            BlockProperties.TITANIUM
        ),
        "cut_titanium_stairs"
    );

    public static final Block DEEPSLATE_TITANIUM_ORE = register(BlockProperties.DEEPSLATE_TITANIUM_ORE, "deepslate_titanium_ore");

    public static final Block DEEPSLATE_ZINC_ORE = register(BlockProperties.DEEPSLATE_ZINC_ORE, "deepslate_zinc_ore");

    public static final Block FERROALUMINUM_BLOCK = register(BlockProperties.FERROALUMINUM, "ferroaluminum_block");

    public static final Block FERROALUMINUM_CHAIN_FENCE = register(
        new IronBarsBlock(BlockProperties.FERROALUMINUM_BARS.sound(SoundType.CHAIN)),
        "ferroaluminum_chain_fence"
    );

    public static final Block FERROALUMINUM_COLUMN = register(
        new RotatedPillarBlock(BlockProperties.FERROALUMINUM),
        "ferroaluminum_column"
    );

    public static final Block FERROALUMINUM_FASTENED_SIDING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_fastened_siding");

    public static final Block FERROALUMINUM_FASTENED_STANDING = register(
        BlockProperties.FERROALUMINUM,
        "ferroaluminum_fastened_standing"
    );

    public static final Block FERROALUMINUM_GRATE = register(
        new WaterloggedTransparentBlock(BlockProperties.FERROALUMINUM_GRATE),
        "ferroaluminum_grate"
    );

    public static final Block FERROALUMINUM_PLATING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_plating");

    public static final Block FERROALUMINUM_SIDING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_siding");

    public static final Block FERROALUMINUM_STANDING = register(BlockProperties.FERROALUMINUM, "ferroaluminum_standing");

    public static final Block FERROALUMINUM_TREAD = register(BlockProperties.FERROALUMINUM, "ferroaluminum_tread");

    public static final Block GALENA_ORE = register(BlockProperties.GALENA_ORE, "galena_ore");

    public static final Block INDUSTRIAL_GLASS = register(new TransparentBlock(BlockProperties.INDUSTRIAL_GLASS), "industrial_glass");

    public static final Block INDUSTRIAL_GLASS_PANE = register(
        new IronBarsBlock(BlockProperties.INDUSTRIAL_GLASS_PANE),
        "industrial_glass_pane"
    );

    public static final Block INDUSTRIAL_FURNACE = register(
        new IndustrialFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)),
        "industrial_furnace_block"
    );

    public static final Block LEAD_BLOCK = register(BlockProperties.LEAD, "lead_block");

    public static final Block LEAD_CHEST = register(new LeadChestBlock(BlockProperties.LEAD), "lead_chest");

    public static final Block AMMO_CHEST = register(new AmmoChestBlock(BlockProperties.LEAD), "ammo_chest");

    public static final Block LITHIUM_BLOCK = register(new LithiumBlock(BlockProperties.LITHIUM_ORE), "lithium_block");

    public static final Block LITHIUM_ORE = register(new LithiumBlock(BlockProperties.LITHIUM_ORE), "lithium_ore");

    public static final Block MONAZITE_ORE = register(BlockProperties.MONAZITE_ORE, "monazite_ore");

    public static final Block NETHER_RESIN = register(new ResinBlock(BlockProperties.NETHER_RESIN), "nether_resin");

    public static final Block NETHER_RESIN_NODE = register(new ResinNodeBlock(BlockProperties.RESIN), "nether_resin_node");

    public static final Block NETHER_RESIN_VEIN = register(new ResinVeinBlock(BlockProperties.NETHER_RESIN_VEIN), "nether_resin_vein");

    public static final Block NETHER_RESIN_WEB = register(new ResinWebBlock(BlockProperties.NETHER_RESIN_WEB), "nether_resin_web");

    public static final Block ABERRANT_RESIN = register(new ResinBlock(BlockProperties.NETHER_RESIN), "aberrant_resin");

    public static final Block ABERRANT_RESIN_NODE = register(new ResinNodeBlock(BlockProperties.RESIN), "aberrant_resin_node");

    public static final Block ABERRANT_RESIN_VEIN = register(new ResinVeinBlock(BlockProperties.NETHER_RESIN_VEIN), "aberrant_resin_vein");

    public static final Block ABERRANT_RESIN_WEB = register(new ResinWebBlock(BlockProperties.NETHER_RESIN_WEB), "aberrant_resin_web");

    public static final Block IRRADIATED_RESIN = register(new IrradiatedResinBlock(BlockProperties.NETHER_RESIN), "irradiated_resin");

    public static final Block IRRADIATED_RESIN_NODE = register(
        new IrradiatedResinNodeBlock(BlockProperties.RESIN),
        "irradiated_resin_node"
    );

    public static final Block IRRADIATED_RESIN_VEIN = register(
        new IrradiatedResinVeinBlock(BlockProperties.NETHER_RESIN_VEIN),
        "irradiated_resin_vein"
    );

    public static final Block IRRADIATED_RESIN_WEB = register(
        new IrradiatedResinWebBlock(BlockProperties.NETHER_RESIN_WEB),
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

    public static final Block RAZOR_WIRE = register(new RazorWireBlock(BlockProperties.RAZOR_WIRE), "razor_wire");

    public static final Block RESIN = register(new ResinBlock(BlockProperties.RESIN), "resin");

    public static final Block RESIN_NODE = register(new ResinNodeBlock(BlockProperties.RESIN), "resin_node");

    public static final Block RESIN_VEIN = register(new ResinVeinBlock(BlockProperties.RESIN_VEIN), "resin_vein");

    public static final Block RESIN_WEB = register(new ResinWebBlock(BlockProperties.RESIN_WEB), "resin_web");

    public static final Block SILICA_GRAVEL = register(
        new ColoredFallingBlock(new ColorRGBA(-8356741), BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL)),
        "silica_gravel"
    );

    public static final Block STEEL_BARS = register(
        new IronBarsBlock(BlockProperties.STEEL_BARS),
        "steel_bars"
    );

    public static final Block STEEL_BLOCK = register(BlockProperties.STEEL, "steel_block");

    public static final Block STEEL_CHAIN_FENCE = register(
        new IronBarsBlock(BlockProperties.STEEL_BARS.sound(SoundType.CHAIN)),
        "steel_chain_fence"
    );

    public static final Block STEEL_COLUMN = register(new RotatedPillarBlock(BlockProperties.STEEL), "steel_column");

    public static final Block STEEL_FASTENED_SIDING = register(BlockProperties.STEEL, "steel_fastened_siding");

    public static final Block STEEL_FASTENED_STANDING = register(BlockProperties.STEEL, "steel_fastened_standing");

    public static final Block STEEL_GRATE = register(new WaterloggedTransparentBlock(BlockProperties.STEEL_GRATE), "steel_grate");

    public static final Block STEEL_PLATING = register(BlockProperties.STEEL, "steel_plating");

    public static final Block STEEL_SIDING = register(BlockProperties.STEEL, "steel_siding");

    public static final Block STEEL_STANDING = register(BlockProperties.STEEL, "steel_standing");

    public static final Block STEEL_TREAD = register(BlockProperties.STEEL, "steel_tread");

    public static final Block TITANIUM_BLOCK = register(BlockProperties.TITANIUM, "titanium_block");

    public static final Block TITANIUM_CHAIN_FENCE = register(
        new IronBarsBlock(BlockProperties.TITANIUM_BARS.sound(SoundType.CHAIN)),
        "titanium_chain_fence"
    );

    public static final Block TITANIUM_COLUMN = register(new RotatedPillarBlock(BlockProperties.TITANIUM), "titanium_column");

    public static final Block TITANIUM_FASTENED_SIDING = register(BlockProperties.TITANIUM, "titanium_fastened_siding");

    public static final Block TITANIUM_FASTENED_STANDING = register(BlockProperties.TITANIUM, "titanium_fastened_standing");

    public static final Block TITANIUM_GRATE = register(
        new WaterloggedTransparentBlock(BlockProperties.TITANIUM_GRATE),
        "titanium_grate"
    );

    public static final Block TITANIUM_PLATING = register(BlockProperties.TITANIUM, "titanium_plating");

    public static final Block TITANIUM_SIDING = register(BlockProperties.TITANIUM, "titanium_siding");

    public static final Block TITANIUM_STANDING = register(BlockProperties.TITANIUM, "titanium_standing");

    public static final Block TITANIUM_TREAD = register(BlockProperties.TITANIUM, "titanium_tread");

    public static final Block URANIUM_BLOCK = registerRadiatedBlock(BlockProperties.URANIUM, "uranium_block");

    public static final Block ZINC_BLOCK = register(BlockProperties.ZINC, "zinc_block");

    public static final Block ZINC_ORE = register(BlockProperties.ZINC_ORE, "zinc_ore");

    // Doors And Trapdoors
    public static final Block FERROALUMINUM_DOOR = register(
        new DoorBlock(BlockSetType.COPPER, BlockProperties.FERROALUMINUM.noOcclusion()),
        "ferroaluminum_door"
    );

    public static final Block FERROALUMINUM_TRAP_DOOR = register(
        new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.FERROALUMINUM.noOcclusion()),
        "ferroaluminum_trapdoor"
    );

    public static final Block STEEL_DOOR = register(new DoorBlock(BlockSetType.COPPER, BlockProperties.STEEL.noOcclusion()), "steel_door");

    public static final Block STEEL_TRAP_DOOR = register(new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.STEEL), "steel_trapdoor");

    public static final Block TITANIUM_DOOR = register(
        new DoorBlock(BlockSetType.COPPER, BlockProperties.TITANIUM.noOcclusion()),
        "titanium_door"
    );

    public static final Block TITANIUM_TRAP_DOOR = register(
        new TrapDoorBlock(BlockSetType.COPPER, BlockProperties.TITANIUM),
        "titanium_trapdoor"
    );

    // Pressure Plates And Buttons
    public static final Block FERROALUMINUM_BUTTON = register(
        new ButtonBlock(AVPBlockSetTypes.FERROALUMINUM, 20, BlockProperties.FERROALUMINUM),
        "ferroaluminum_button"
    );

    public static final Block STEEL_BUTTON = register(new ButtonBlock(AVPBlockSetTypes.STEEL, 20, BlockProperties.STEEL), "steel_button");

    public static final Block TITANIUM_BUTTON = register(
        new ButtonBlock(AVPBlockSetTypes.TITANIUM, 20, BlockProperties.TITANIUM),
        "titanium_button"
    );

    public static final Block FERROALUMINUM_PRESSURE_PLATE = register(
        new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.FERROALUMINUM),
        "ferroaluminum_pressure_plate"
    );

    public static final Block STEEL_PRESSURE_PLATE = register(
        new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.STEEL),
        "steel_pressure_plate"
    );

    public static final Block TITANIUM_PRESSURE_PLATE = register(
        new PressurePlateBlock(BlockSetType.COPPER, BlockProperties.TITANIUM),
        "titanium_pressure_plate"
    );

    // Slabs And Stairs
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
        new SlabBlock(BlockProperties.FERROALUMINUM),
        "ferroaluminum_siding_slab"
    );

    public static final Block FERROALUMINUM_SIDING_STAIRS = register(
        new StairBlock(FERROALUMINUM_SIDING.defaultBlockState(), BlockProperties.FERROALUMINUM),
        "ferroaluminum_siding_stairs"
    );

    public static final Block STEEL_SIDING_SLAB = register(new SlabBlock(BlockProperties.STEEL), "steel_siding_slab");

    public static final Block STEEL_SIDING_STAIRS = register(
        new StairBlock(STEEL_SIDING.defaultBlockState(), BlockProperties.STEEL),
        "steel_siding_stairs"
    );

    public static final Block TITANIUM_SIDING_SLAB = register(new SlabBlock(BlockProperties.TITANIUM), "titanium_siding_slab");

    public static final Block TITANIUM_SIDING_STAIRS = register(
        new StairBlock(TITANIUM_SIDING.defaultBlockState(), BlockProperties.TITANIUM),
        "titanium_siding_stairs"
    );

    // Standing Blocks - Slabs And Stairs
    public static final Block FERROALUMINUM_STANDING_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM),
        "ferroaluminum_standing_slab"
    );

    public static final Block FERROALUMINUM_STANDING_STAIRS = register(
        new StairBlock(FERROALUMINUM_STANDING.defaultBlockState(), BlockProperties.FERROALUMINUM),
        "ferroaluminum_standing_stairs"
    );

    public static final Block STEEL_STANDING_SLAB = register(new SlabBlock(BlockProperties.STEEL), "steel_standing_slab");

    public static final Block STEEL_STANDING_STAIRS = register(
        new StairBlock(STEEL_STANDING.defaultBlockState(), BlockProperties.STEEL),
        "steel_standing_stairs"
    );

    public static final Block TITANIUM_STANDING_SLAB = register(new SlabBlock(BlockProperties.TITANIUM), "titanium_standing_slab");

    public static final Block TITANIUM_STANDING_STAIRS = register(
        new StairBlock(TITANIUM_STANDING.defaultBlockState(), BlockProperties.TITANIUM),
        "titanium_standing_stairs"
    );

    // Fastened Siding - Slabs and Stairs
    public static final Block FERROALUMINUM_FASTENED_SIDING_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM),
        "ferroaluminum_fastened_siding_slab"
    );

    public static final Block FERROALUMINUM_FASTENED_SIDING_STAIRS = register(
        new StairBlock(FERROALUMINUM_FASTENED_SIDING.defaultBlockState(), BlockProperties.FERROALUMINUM),
        "ferroaluminum_fastened_siding_stairs"
    );

    public static final Block STEEL_FASTENED_SIDING_SLAB = register(new SlabBlock(BlockProperties.STEEL), "steel_fastened_siding_slab");

    public static final Block STEEL_FASTENED_SIDING_STAIRS = register(
        new StairBlock(STEEL_FASTENED_SIDING.defaultBlockState(), BlockProperties.STEEL),
        "steel_fastened_siding_stairs"
    );

    public static final Block TITANIUM_FASTENED_SIDING_SLAB = register(
        new SlabBlock(BlockProperties.TITANIUM),
        "titanium_fastened_siding_slab"
    );

    public static final Block TITANIUM_FASTENED_SIDING_STAIRS = register(
        new StairBlock(TITANIUM_FASTENED_SIDING.defaultBlockState(), BlockProperties.TITANIUM),
        "titanium_fastened_siding_stairs"
    );

    // Fastened Standing - Slabs and Stairs
    public static final Block FERROALUMINUM_FASTENED_STANDING_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM),
        "ferroaluminum_fastened_standing_slab"
    );

    public static final Block FERROALUMINUM_FASTENED_STANDING_STAIRS = register(
        new StairBlock(FERROALUMINUM_FASTENED_STANDING.defaultBlockState(), BlockProperties.FERROALUMINUM),
        "ferroaluminum_fastened_standing_stairs"
    );

    public static final Block STEEL_FASTENED_STANDING_SLAB = register(new SlabBlock(BlockProperties.STEEL), "steel_fastened_standing_slab");

    public static final Block STEEL_FASTENED_STANDING_STAIRS = register(
        new StairBlock(STEEL_FASTENED_STANDING.defaultBlockState(), BlockProperties.STEEL),
        "steel_fastened_standing_stairs"
    );

    public static final Block TITANIUM_FASTENED_STANDING_SLAB = register(
        new SlabBlock(BlockProperties.TITANIUM),
        "titanium_fastened_standing_slab"
    );

    public static final Block TITANIUM_FASTENED_STANDING_STAIRS = register(
        new StairBlock(TITANIUM_FASTENED_STANDING.defaultBlockState(), BlockProperties.TITANIUM),
        "titanium_fastened_standing_stairs"
    );

    // Plating - Slabs and Stairs
    public static final Block FERROALUMINUM_PLATING_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM),
        "ferroaluminum_plating_slab"
    );

    public static final Block FERROALUMINUM_PLATING_STAIRS = register(
        new StairBlock(FERROALUMINUM_PLATING.defaultBlockState(), BlockProperties.FERROALUMINUM),
        "ferroaluminum_plating_stairs"
    );

    public static final Block STEEL_PLATING_SLAB = register(new SlabBlock(BlockProperties.STEEL), "steel_plating_slab");

    public static final Block STEEL_PLATING_STAIRS = register(
        new StairBlock(STEEL_PLATING.defaultBlockState(), BlockProperties.STEEL),
        "steel_plating_stairs"
    );

    public static final Block TITANIUM_PLATING_SLAB = register(new SlabBlock(BlockProperties.TITANIUM), "titanium_plating_slab");

    public static final Block TITANIUM_PLATING_STAIRS = register(
        new StairBlock(TITANIUM_PLATING.defaultBlockState(), BlockProperties.TITANIUM),
        "titanium_plating_stairs"
    );

    // Tread - Slabs and Stairs
    public static final Block FERROALUMINUM_TREAD_SLAB = register(new SlabBlock(BlockProperties.FERROALUMINUM), "ferroaluminum_tread_slab");

    public static final Block FERROALUMINUM_TREAD_STAIRS = register(
        new StairBlock(FERROALUMINUM_TREAD.defaultBlockState(), BlockProperties.FERROALUMINUM),
        "ferroaluminum_tread_stairs"
    );

    public static final Block STEEL_TREAD_SLAB = register(new SlabBlock(BlockProperties.STEEL), "steel_tread_slab");

    public static final Block STEEL_TREAD_STAIRS = register(
        new StairBlock(STEEL_TREAD.defaultBlockState(), BlockProperties.STEEL),
        "steel_tread_stairs"
    );

    public static final Block TITANIUM_TREAD_SLAB = register(new SlabBlock(BlockProperties.TITANIUM), "titanium_tread_slab");

    public static final Block TITANIUM_TREAD_STAIRS = register(
        new StairBlock(TITANIUM_TREAD.defaultBlockState(), BlockProperties.TITANIUM),
        "titanium_tread_stairs"
    );

    // Grate - Slabs and Stairs
    public static final Block FERROALUMINUM_GRATE_SLAB = register(
        new SlabBlock(BlockProperties.FERROALUMINUM.noOcclusion()),
        "ferroaluminum_grate_slab"
    );

    public static final Block FERROALUMINUM_GRATE_STAIRS = register(
        new StairBlock(FERROALUMINUM_GRATE.defaultBlockState(), BlockProperties.FERROALUMINUM.noOcclusion()),
        "ferroaluminum_grate_stairs"
    );

    public static final Block STEEL_GRATE_SLAB = register(new SlabBlock(BlockProperties.STEEL.noOcclusion()), "steel_grate_slab");

    public static final Block STEEL_GRATE_STAIRS = register(
        new StairBlock(STEEL_GRATE.defaultBlockState(), BlockProperties.STEEL.noOcclusion()),
        "steel_grate_stairs"
    );

    public static final Block TITANIUM_GRATE_SLAB = register(new SlabBlock(BlockProperties.TITANIUM.noOcclusion()), "titanium_grate_slab");

    public static final Block TITANIUM_GRATE_STAIRS = register(
        new StairBlock(TITANIUM_GRATE.defaultBlockState(), BlockProperties.TITANIUM.noOcclusion()),
        "titanium_grate_stairs"
    );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new SlabBlock(BlockProperties.DYE_COLOR_TO_CONCRETE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_concrete_slab"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new StairBlock(
                                BlockProperties.DYE_COLOR_TO_CONCRETE_BLOCKS.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_CONCRETE_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_concrete_stairs"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_INDUSTRIAL_CONCRETE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new Block(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_concrete"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new SlabBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_concrete_slab"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new StairBlock(
                                DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_industrial_concrete_stairs"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new WallBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_concrete_wall"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_INDUSTRIAL_GLASS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new CustomTransparentBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_GLASS_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_glass"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new IronBarsBlock(BlockProperties.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_industrial_glass_pane"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_padding"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_padding_slab"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new StairBlock(
                                DYE_COLOR_TO_PADDING.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_padding_stairs"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PANEL_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_panel_padding"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PANEL_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_panel_padding_slab"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PANEL_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new StairBlock(
                                DYE_COLOR_TO_PANEL_PADDING.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_panel_padding_stairs"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PIPE_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new Block(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_pipe_padding"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PIPE_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new SlabBlock(BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_pipe_padding_slab"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PIPE_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new StairBlock(
                                DYE_COLOR_TO_PIPE_PADDING.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PADDING_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_pipe_padding_stairs"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_CUT_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new Block(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_cut_plastic"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_CUT_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new SlabBlock(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_cut_plastic_slab"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_CUT_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new StairBlock(
                                DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_cut_plastic_stairs"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new Block(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_plastic"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new SlabBlock(BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)),
                            dyeColor.getName() + "_plastic_slab"
                        )
                    )
                )
        );

    public static final Map<DyeColor, Block> DYE_COLOR_TO_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            new StairBlock(
                                DYE_COLOR_TO_PLASTIC.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_PLASTIC_PROPERTIES.get(dyeColor)
                            ),
                            dyeColor.getName() + "_plastic_stairs"
                        )
                    )
                )
        );

    public static Block registerRadiatedBlock(BlockBehaviour.Properties properties, String id) {
        return register(new RadiatedBlock(properties), id);
    }

    public static Block register(BlockBehaviour.Properties properties, String id) {
        return register(new Block(properties), id);
    }

    private static void registerBlock(String name, Block block) {
        BLOCKS.put(name, register(block, name));
    }

    public static Block register(Block block, String id) {
        var resourceLocation = AVPResources.location(id);
        return Registry.register(BuiltInRegistries.BLOCK, resourceLocation, block);
    }

    public static void initialize() {}
}
