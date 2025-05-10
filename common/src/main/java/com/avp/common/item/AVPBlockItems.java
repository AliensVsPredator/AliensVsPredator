package com.avp.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block_item.AmmoChestBlockItem;
import com.avp.common.block_item.LeadChestBlockItem;
import com.avp.common.block_item.SentryTurretBlockItem;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPBlockItems {

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN = register(
        "aberrant_resin",
        AVPBlocks.ABERRANT_RESIN,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_NODE = register(
        "aberrant_resin_node",
        AVPBlocks.ABERRANT_RESIN_NODE,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_VEIN = register(
        "aberrant_resin_vein",
        AVPBlocks.ABERRANT_RESIN_VEIN,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_WEB = register(
        "aberrant_resin_web",
        AVPBlocks.ABERRANT_RESIN_WEB,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> ALUMINUM_BLOCK = register("aluminum_block", AVPBlocks.ALUMINUM_BLOCK);

    public static final AVPDeferredHolder<BlockItem> AMMO_CHEST = registerWithSupplier("ammo_chest", AmmoChestBlockItem::new);

    public static final AVPDeferredHolder<BlockItem> ASH_BLOCK = register("ash_block", AVPBlocks.ASH_BLOCK);

    public static final AVPDeferredHolder<BlockItem> AUTUNITE_BLOCK = register("autunite_block", AVPBlocks.AUTUNITE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> AUTUNITE_ORE = register("autunite_ore", AVPBlocks.AUTUNITE_ORE);

    public static final AVPDeferredHolder<BlockItem> BAUXITE_ORE = register("bauxite_ore", AVPBlocks.BAUXITE_ORE);

    public static final AVPDeferredHolder<BlockItem> BLUEPRINT_BLOCK = register("blueprint_block", AVPBlocks.BLUEPRINT_BLOCK);

    public static final AVPDeferredHolder<BlockItem> BRASS_BLOCK = register("brass_block", AVPBlocks.BRASS_BLOCK);

    public static final AVPDeferredHolder<BlockItem> CHISELED_FERROALUMINUM = register(
        "chiseled_ferroaluminum",
        AVPBlocks.CHISELED_FERROALUMINUM
    );

    public static final AVPDeferredHolder<BlockItem> CHISELED_STEEL = register("chiseled_steel", AVPBlocks.CHISELED_STEEL);

    public static final AVPDeferredHolder<BlockItem> CHISELED_TITANIUM = register("chiseled_titanium", AVPBlocks.CHISELED_TITANIUM);

    public static final AVPDeferredHolder<BlockItem> CUT_FERROALUMINUM = register("cut_ferroaluminum", AVPBlocks.CUT_FERROALUMINUM);

    public static final AVPDeferredHolder<BlockItem> CUT_FERROALUMINUM_SLAB = register(
        "cut_ferroaluminum_slab",
        AVPBlocks.CUT_FERROALUMINUM_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> CUT_FERROALUMINUM_STAIRS = register(
        "cut_ferroaluminum_stairs",
        AVPBlocks.CUT_FERROALUMINUM_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> CUT_STEEL = register("cut_steel", AVPBlocks.CUT_STEEL);

    public static final AVPDeferredHolder<BlockItem> CUT_STEEL_SLAB = register("cut_steel_slab", AVPBlocks.CUT_STEEL_SLAB);

    public static final AVPDeferredHolder<BlockItem> CUT_STEEL_STAIRS = register("cut_steel_stairs", AVPBlocks.CUT_STEEL_STAIRS);

    public static final AVPDeferredHolder<BlockItem> CUT_TITANIUM = register("cut_titanium", AVPBlocks.CUT_TITANIUM);

    public static final AVPDeferredHolder<BlockItem> CUT_TITANIUM_SLAB = register("cut_titanium_slab", AVPBlocks.CUT_TITANIUM_SLAB);

    public static final AVPDeferredHolder<BlockItem> CUT_TITANIUM_STAIRS = register(
        "cut_titanium_stairs",
        AVPBlocks.CUT_TITANIUM_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> DEEPSLATE_TITANIUM_ORE = register(
        "deepslate_titanium_ore",
        AVPBlocks.DEEPSLATE_TITANIUM_ORE
    );

    public static final AVPDeferredHolder<BlockItem> DEEPSLATE_ZINC_ORE = register("deepslate_zinc_ore", AVPBlocks.DEEPSLATE_ZINC_ORE);

    public static final AVPDeferredHolder<BlockItem> DESK_TERMINAL_BLOCK = register("desk_terminal", AVPBlocks.DESK_TERMINAL_BLOCK);

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_BLOCK = register(
        "ferroaluminum_block",
        AVPBlocks.FERROALUMINUM_BLOCK
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_BUTTON = register(
        "ferroaluminum_button",
        AVPBlocks.FERROALUMINUM_BUTTON
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_CHAIN_FENCE = register(
        "ferroaluminum_chain_fence",
        AVPBlocks.FERROALUMINUM_CHAIN_FENCE
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_COLUMN = register(
        "ferroaluminum_column",
        AVPBlocks.FERROALUMINUM_COLUMN
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_DOOR = register("ferroaluminum_door", AVPBlocks.FERROALUMINUM_DOOR);

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_FASTENED_SIDING = register(
        "ferroaluminum_fastened_siding",
        AVPBlocks.FERROALUMINUM_FASTENED_SIDING
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_FASTENED_SIDING_SLAB = register(
        "ferroaluminum_fastened_siding_slab",
        AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_FASTENED_SIDING_STAIRS = register(
        "ferroaluminum_fastened_siding_stairs",
        AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_FASTENED_STANDING = register(
        "ferroaluminum_fastened_standing",
        AVPBlocks.FERROALUMINUM_FASTENED_STANDING
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_FASTENED_STANDING_SLAB = register(
        "ferroaluminum_fastened_standing_slab",
        AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_FASTENED_STANDING_STAIRS = register(
        "ferroaluminum_fastened_standing_stairs",
        AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_GRATE = register(
        "ferroaluminum_grate",
        AVPBlocks.FERROALUMINUM_GRATE
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_GRATE_SLAB = register(
        "ferroaluminum_grate_slab",
        AVPBlocks.FERROALUMINUM_GRATE_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_GRATE_STAIRS = register(
        "ferroaluminum_grate_stairs",
        AVPBlocks.FERROALUMINUM_GRATE_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_PLATING = register(
        "ferroaluminum_plating",
        AVPBlocks.FERROALUMINUM_PLATING
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_PLATING_SLAB = register(
        "ferroaluminum_plating_slab",
        AVPBlocks.FERROALUMINUM_PLATING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_PLATING_STAIRS = register(
        "ferroaluminum_plating_stairs",
        AVPBlocks.FERROALUMINUM_PLATING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_PRESSURE_PLATE = register(
        "ferroaluminum_pressure_plate",
        AVPBlocks.FERROALUMINUM_PRESSURE_PLATE
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_SIDING = register(
        "ferroaluminum_siding",
        AVPBlocks.FERROALUMINUM_SIDING
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_SIDING_SLAB = register(
        "ferroaluminum_siding_slab",
        AVPBlocks.FERROALUMINUM_SIDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_SIDING_STAIRS = register(
        "ferroaluminum_siding_stairs",
        AVPBlocks.FERROALUMINUM_SIDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_SLAB = register("ferroaluminum_slab", AVPBlocks.FERROALUMINUM_SLAB);

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_STAIRS = register(
        "ferroaluminum_stairs",
        AVPBlocks.FERROALUMINUM_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_STANDING = register(
        "ferroaluminum_standing",
        AVPBlocks.FERROALUMINUM_STANDING
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_STANDING_SLAB = register(
        "ferroaluminum_standing_slab",
        AVPBlocks.FERROALUMINUM_STANDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_STANDING_STAIRS = register(
        "ferroaluminum_standing_stairs",
        AVPBlocks.FERROALUMINUM_STANDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_TRAP_DOOR = register(
        "ferroaluminum_trapdoor",
        AVPBlocks.FERROALUMINUM_TRAP_DOOR
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_TREAD = register(
        "ferroaluminum_tread",
        AVPBlocks.FERROALUMINUM_TREAD
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_TREAD_SLAB = register(
        "ferroaluminum_tread_slab",
        AVPBlocks.FERROALUMINUM_TREAD_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> FERROALUMINUM_TREAD_STAIRS = register(
        "ferroaluminum_tread_stairs",
        AVPBlocks.FERROALUMINUM_TREAD_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> GALENA_ORE = register("galena_ore", AVPBlocks.GALENA_ORE);

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_FURNACE_BLOCK = register(
        "industrial_furnace_block",
        AVPBlocks.INDUSTRIAL_FURNACE
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS = register("industrial_glass", AVPBlocks.INDUSTRIAL_GLASS);

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_DOOR = register(
        "industrial_glass_door",
        AVPBlocks.INDUSTRIAL_GLASS_DOOR
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_PANE = register(
        "industrial_glass_pane",
        AVPBlocks.INDUSTRIAL_GLASS_PANE
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_SLAB = register(
        "industrial_glass_slab",
        AVPBlocks.INDUSTRIAL_GLASS_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_STAIRS = register(
        "industrial_glass_stairs",
        AVPBlocks.INDUSTRIAL_GLASS_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_GLASS_TRAP_DOOR = register(
        "industrial_glass_trapdoor",
        AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN = register("irradiated_resin", AVPBlocks.IRRADIATED_RESIN);

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_NODE = register(
        "irradiated_resin_node",
        AVPBlocks.IRRADIATED_RESIN_NODE
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_VEIN = register(
        "irradiated_resin_vein",
        AVPBlocks.IRRADIATED_RESIN_VEIN
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_WEB = register(
        "irradiated_resin_web",
        AVPBlocks.IRRADIATED_RESIN_WEB
    );

    public static final AVPDeferredHolder<BlockItem> LEAD_BLOCK = register("lead_block", AVPBlocks.LEAD_BLOCK);

    public static final AVPDeferredHolder<BlockItem> LEAD_CHEST = registerWithSupplier("lead_chest", LeadChestBlockItem::new);

    public static final AVPDeferredHolder<BlockItem> LITHIUM_BLOCK = register("lithium_block", AVPBlocks.LITHIUM_BLOCK);

    public static final AVPDeferredHolder<BlockItem> LITHIUM_ORE = register("lithium_ore", AVPBlocks.LITHIUM_ORE);

    public static final AVPDeferredHolder<BlockItem> MONAZITE_ORE = register("monazite_ore", AVPBlocks.MONAZITE_ORE);

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN = register(
        "nether_resin",
        AVPBlocks.NETHER_RESIN,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_NODE = register(
        "nether_resin_node",
        AVPBlocks.NETHER_RESIN_NODE,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_VEIN = register(
        "nether_resin_vein",
        AVPBlocks.NETHER_RESIN_VEIN,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_WEB = register(
        "nether_resin_web",
        AVPBlocks.NETHER_RESIN_WEB,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NUKE_BLOCK = register("nuke", AVPBlocks.NUKE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_BAUXITE_BLOCK = register("raw_bauxite_block", AVPBlocks.RAW_BAUXITE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_GALENA_BLOCK = register("raw_galena_block", AVPBlocks.RAW_GALENA_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_MONAZITE_BLOCK = register("raw_monazite_block", AVPBlocks.RAW_MONAZITE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_TITANIUM_BLOCK = register("raw_titanium_block", AVPBlocks.RAW_TITANIUM_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_ZINC_BLOCK = register("raw_zinc_block", AVPBlocks.RAW_ZINC_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAZOR_WIRE = register("razor_wire", AVPBlocks.RAZOR_WIRE);

    public static final AVPDeferredHolder<BlockItem> REDSTONE_GENERATOR = register("redstone_generator", AVPBlocks.REDSTONE_GENERATOR);

    public static final AVPDeferredHolder<BlockItem> RESIN = register("resin", AVPBlocks.RESIN);

    public static final AVPDeferredHolder<BlockItem> RESIN_BRICKS = register("resin_bricks", AVPBlocks.RESIN_BRICKS);

    public static final AVPDeferredHolder<BlockItem> RESIN_NODE = register("resin_node", AVPBlocks.RESIN_NODE);

    public static final AVPDeferredHolder<BlockItem> RESIN_O = register("resin_o", AVPBlocks.RESIN_O);

    public static final AVPDeferredHolder<BlockItem> RESIN_RIBBED = register("resin_ribbed", AVPBlocks.RESIN_RIBBED);

    public static final AVPDeferredHolder<BlockItem> RESIN_SMOOTH = register("resin_smooth", AVPBlocks.RESIN_SMOOTH);

    public static final AVPDeferredHolder<BlockItem> RESIN_VEIN = register("resin_vein", AVPBlocks.RESIN_VEIN);

    public static final AVPDeferredHolder<BlockItem> RESIN_WEB = register("resin_web", AVPBlocks.RESIN_WEB);

    public static final AVPDeferredHolder<BlockItem> RESONATOR_BLOCK = register("resonator", AVPBlocks.RESONATOR_BLOCK);

    public static final AVPDeferredHolder<BlockItem> ROYAL_JELLY_BLOCK = registerWithSupplier(
        "royal_jelly_block",
        RoyalJellyBlockItem::new
    );

    public static final AVPDeferredHolder<BlockItem> SENTRY_TURRET = registerWithSupplier("sentry_turret", SentryTurretBlockItem::new);

    public static final AVPDeferredHolder<BlockItem> SILICA_GRAVEL = register("silica_gravel", AVPBlocks.SILICA_GRAVEL);

    // TODO: Change this to "silicon_block" with 0.2.0.
    public static final AVPDeferredHolder<BlockItem> SILICON_BLOCK = register("raw_silica_block", AVPBlocks.SILICON_BLOCK);

    public static final AVPDeferredHolder<BlockItem> STEEL_BARS = register("steel_bars", AVPBlocks.STEEL_BARS);

    public static final AVPDeferredHolder<BlockItem> STEEL_BLOCK = register("steel_block", AVPBlocks.STEEL_BLOCK);

    public static final AVPDeferredHolder<BlockItem> STEEL_BUTTON = register("steel_button", AVPBlocks.STEEL_BUTTON);

    public static final AVPDeferredHolder<BlockItem> STEEL_CHAIN_FENCE = register("steel_chain_fence", AVPBlocks.STEEL_CHAIN_FENCE);

    public static final AVPDeferredHolder<BlockItem> STEEL_COLUMN = register("steel_column", AVPBlocks.STEEL_COLUMN);

    public static final AVPDeferredHolder<BlockItem> STEEL_DOOR = register("steel_door", AVPBlocks.STEEL_DOOR);

    public static final AVPDeferredHolder<BlockItem> STEEL_FASTENED_SIDING = register(
        "steel_fastened_siding",
        AVPBlocks.STEEL_FASTENED_SIDING
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_FASTENED_SIDING_SLAB = register(
        "steel_fastened_siding_slab",
        AVPBlocks.STEEL_FASTENED_SIDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_FASTENED_SIDING_STAIRS = register(
        "steel_fastened_siding_stairs",
        AVPBlocks.STEEL_FASTENED_SIDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_FASTENED_STANDING = register(
        "steel_fastened_standing",
        AVPBlocks.STEEL_FASTENED_STANDING
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_FASTENED_STANDING_SLAB = register(
        "steel_fastened_standing_slab",
        AVPBlocks.STEEL_FASTENED_STANDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_FASTENED_STANDING_STAIRS = register(
        "steel_fastened_standing_stairs",
        AVPBlocks.STEEL_FASTENED_STANDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_GRATE = register("steel_grate", AVPBlocks.STEEL_GRATE);

    public static final AVPDeferredHolder<BlockItem> STEEL_GRATE_SLAB = register("steel_grate_slab", AVPBlocks.STEEL_GRATE_SLAB);

    public static final AVPDeferredHolder<BlockItem> STEEL_GRATE_STAIRS = register("steel_grate_stairs", AVPBlocks.STEEL_GRATE_STAIRS);

    public static final AVPDeferredHolder<BlockItem> STEEL_PLATING = register("steel_plating", AVPBlocks.STEEL_PLATING);

    public static final AVPDeferredHolder<BlockItem> STEEL_PLATING_SLAB = register("steel_plating_slab", AVPBlocks.STEEL_PLATING_SLAB);

    public static final AVPDeferredHolder<BlockItem> STEEL_PLATING_STAIRS = register(
        "steel_plating_stairs",
        AVPBlocks.STEEL_PLATING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_PRESSURE_PLATE = register(
        "steel_pressure_plate",
        AVPBlocks.STEEL_PRESSURE_PLATE
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_SIDING = register("steel_siding", AVPBlocks.STEEL_SIDING);

    public static final AVPDeferredHolder<BlockItem> STEEL_SIDING_SLAB = register("steel_siding_slab", AVPBlocks.STEEL_SIDING_SLAB);

    public static final AVPDeferredHolder<BlockItem> STEEL_SIDING_STAIRS = register(
        "steel_siding_stairs",
        AVPBlocks.STEEL_SIDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_SLAB = register("steel_slab", AVPBlocks.STEEL_SLAB);

    public static final AVPDeferredHolder<BlockItem> STEEL_STAIRS = register("steel_stairs", AVPBlocks.STEEL_STAIRS);

    public static final AVPDeferredHolder<BlockItem> STEEL_STANDING = register("steel_standing", AVPBlocks.STEEL_STANDING);

    public static final AVPDeferredHolder<BlockItem> STEEL_STANDING_SLAB = register(
        "steel_standing_slab",
        AVPBlocks.STEEL_STANDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_STANDING_STAIRS = register(
        "steel_standing_stairs",
        AVPBlocks.STEEL_STANDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> STEEL_TRAP_DOOR = register("steel_trapdoor", AVPBlocks.STEEL_TRAP_DOOR);

    public static final AVPDeferredHolder<BlockItem> STEEL_TREAD = register("steel_tread", AVPBlocks.STEEL_TREAD);

    public static final AVPDeferredHolder<BlockItem> STEEL_TREAD_SLAB = register("steel_tread_slab", AVPBlocks.STEEL_TREAD_SLAB);

    public static final AVPDeferredHolder<BlockItem> STEEL_TREAD_STAIRS = register("steel_tread_stairs", AVPBlocks.STEEL_TREAD_STAIRS);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_BLOCK = register("titanium_block", AVPBlocks.TITANIUM_BLOCK);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_BUTTON = register("titanium_button", AVPBlocks.TITANIUM_BUTTON);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_CHAIN_FENCE = register(
        "titanium_chain_fence",
        AVPBlocks.TITANIUM_CHAIN_FENCE
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_COLUMN = register("titanium_column", AVPBlocks.TITANIUM_COLUMN);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_DOOR = register("titanium_door", AVPBlocks.TITANIUM_DOOR);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_FASTENED_SIDING = register(
        "titanium_fastened_siding",
        AVPBlocks.TITANIUM_FASTENED_SIDING
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_FASTENED_SIDING_SLAB = register(
        "titanium_fastened_siding_slab",
        AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_FASTENED_SIDING_STAIRS = register(
        "titanium_fastened_siding_stairs",
        AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_FASTENED_STANDING = register(
        "titanium_fastened_standing",
        AVPBlocks.TITANIUM_FASTENED_STANDING
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_FASTENED_STANDING_SLAB = register(
        "titanium_fastened_standing_slab",
        AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_FASTENED_STANDING_STAIRS = register(
        "titanium_fastened_standing_stairs",
        AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_GRATE = register("titanium_grate", AVPBlocks.TITANIUM_GRATE);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_GRATE_SLAB = register(
        "titanium_grate_slab",
        AVPBlocks.TITANIUM_GRATE_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_GRATE_STAIRS = register(
        "titanium_grate_stairs",
        AVPBlocks.TITANIUM_GRATE_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_PLATING = register("titanium_plating", AVPBlocks.TITANIUM_PLATING);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_PLATING_SLAB = register(
        "titanium_plating_slab",
        AVPBlocks.TITANIUM_PLATING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_PLATING_STAIRS = register(
        "titanium_plating_stairs",
        AVPBlocks.TITANIUM_PLATING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_PRESSURE_PLATE = register(
        "titanium_pressure_plate",
        AVPBlocks.TITANIUM_PRESSURE_PLATE
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_SIDING = register("titanium_siding", AVPBlocks.TITANIUM_SIDING);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_SIDING_SLAB = register(
        "titanium_siding_slab",
        AVPBlocks.TITANIUM_SIDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_SIDING_STAIRS = register(
        "titanium_siding_stairs",
        AVPBlocks.TITANIUM_SIDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_SLAB = register("titanium_slab", AVPBlocks.TITANIUM_SLAB);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_STAIRS = register("titanium_stairs", AVPBlocks.TITANIUM_STAIRS);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_STANDING = register("titanium_standing", AVPBlocks.TITANIUM_STANDING);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_STANDING_SLAB = register(
        "titanium_standing_slab",
        AVPBlocks.TITANIUM_STANDING_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_STANDING_STAIRS = register(
        "titanium_standing_stairs",
        AVPBlocks.TITANIUM_STANDING_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_TRAP_DOOR = register("titanium_trapdoor", AVPBlocks.TITANIUM_TRAP_DOOR);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_TREAD = register("titanium_tread", AVPBlocks.TITANIUM_TREAD);

    public static final AVPDeferredHolder<BlockItem> TITANIUM_TREAD_SLAB = register(
        "titanium_tread_slab",
        AVPBlocks.TITANIUM_TREAD_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> TITANIUM_TREAD_STAIRS = register(
        "titanium_tread_stairs",
        AVPBlocks.TITANIUM_TREAD_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> TRINITITE_BLOCK = register("trinitite_block", AVPBlocks.TRINITITE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> TRIP_MINE_BLOCK = register("trip_mine", AVPBlocks.TRIP_MINE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> URANIUM_BLOCK = register("uranium_block", AVPBlocks.URANIUM_BLOCK);

    public static final AVPDeferredHolder<BlockItem> ZINC_BLOCK = register("zinc_block", AVPBlocks.ZINC_BLOCK);

    public static final AVPDeferredHolder<BlockItem> ZINC_ORE = register("zinc_ore", AVPBlocks.ZINC_ORE);

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_slab",
                            AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_stairs",
                            AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete",
                            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete_slab",
                            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete_stairs",
                            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_concrete_wall",
                            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_GLASS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_glass",
                            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_industrial_glass_pane",
                            AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_padding",
                            AVPBlocks.DYE_COLOR_TO_PADDING.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_padding_slab",
                            AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_padding_stairs",
                            AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PANEL_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_panel_padding",
                            AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PANEL_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_panel_padding_slab",
                            AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PANEL_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_panel_padding_stairs",
                            AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PIPE_PADDING =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_pipe_padding",
                            AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PIPE_PADDING_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_pipe_padding_slab",
                            AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PIPE_PADDING_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_pipe_padding_stairs",
                            AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_cut_plastic",
                            AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_cut_plastic_slab",
                            AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CUT_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_cut_plastic_stairs",
                            AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PLASTIC =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_plastic",
                            AVPBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PLASTIC_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_plastic_slab",
                            AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_PLASTIC_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_plastic_stairs",
                            AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    private static AVPDeferredHolder<BlockItem> register(String id, Supplier<Block> blockSupplier) {
        return register(id, blockSupplier, new Item.Properties());
    }

    private static AVPDeferredHolder<BlockItem> register(String id, Supplier<Block> blockSupplier, Item.Properties properties) {
        return registerWithSupplier(id, () -> new BlockItem(blockSupplier.get(), properties));
    }

    private static AVPDeferredHolder<BlockItem> registerWithSupplier(String id, Supplier<BlockItem> blockItemSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.ITEM, id, blockItemSupplier);
    }

    public static void initialize() {
        Services.REGISTRY.registerAzureLibIdentity(RESONATOR_BLOCK);
        Services.REGISTRY.registerAzureLibIdentity(SENTRY_TURRET);
    }
}
