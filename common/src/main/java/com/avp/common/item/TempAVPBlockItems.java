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

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.block_item.AmmoChestBlockItem;
import com.avp.common.block_item.LeadChestBlockItem;
import com.avp.common.block_item.SentryTurretBlockItem;
import com.avp.service.Services;

// TODO: Rename this once multi-loader migration is finished.
public class TempAVPBlockItems {

    public static final Supplier<BlockItem> ABERRANT_RESIN = register(
        "aberrant_resin",
        TempAVPBlocks.ABERRANT_RESIN,
        new Item.Properties().fireResistant()
    );

    public static final Supplier<BlockItem> ABERRANT_RESIN_NODE = register(
        "aberrant_resin_node",
        TempAVPBlocks.ABERRANT_RESIN_NODE,
        new Item.Properties().fireResistant()
    );

    public static final Supplier<BlockItem> ABERRANT_RESIN_VEIN = register(
        "aberrant_resin_vein",
        TempAVPBlocks.ABERRANT_RESIN_VEIN,
        new Item.Properties().fireResistant()
    );

    public static final Supplier<BlockItem> ABERRANT_RESIN_WEB = register(
        "aberrant_resin_web",
        TempAVPBlocks.ABERRANT_RESIN_WEB,
        new Item.Properties().fireResistant()
    );

    public static final Supplier<BlockItem> ALUMINUM_BLOCK = register("aluminum_block", TempAVPBlocks.ALUMINUM_BLOCK);

    public static final Supplier<BlockItem> AMMO_CHEST = registerDirect("ammo_chest", AmmoChestBlockItem::new);

    public static final Supplier<BlockItem> ASH_BLOCK = register("ash_block", TempAVPBlocks.ASH_BLOCK);

    public static final Supplier<BlockItem> AUTUNITE_BLOCK = register("autunite_block", TempAVPBlocks.AUTUNITE_BLOCK);

    public static final Supplier<BlockItem> AUTUNITE_ORE = register("autunite_ore", TempAVPBlocks.AUTUNITE_ORE);

    public static final Supplier<BlockItem> BAUXITE_ORE = register("bauxite_ore", TempAVPBlocks.BAUXITE_ORE);

    public static final Supplier<BlockItem> BLUEPRINT_BLOCK = register("blueprint_block", TempAVPBlocks.BLUEPRINT_BLOCK);

    public static final Supplier<BlockItem> BRASS_BLOCK = register("brass_block", TempAVPBlocks.BRASS_BLOCK);

    public static final Supplier<BlockItem> CHISELED_FERROALUMINUM = register(
        "chiseled_ferroaluminum",
        TempAVPBlocks.CHISELED_FERROALUMINUM
    );

    public static final Supplier<BlockItem> CHISELED_STEEL = register("chiseled_steel", TempAVPBlocks.CHISELED_STEEL);

    public static final Supplier<BlockItem> CHISELED_TITANIUM = register("chiseled_titanium", TempAVPBlocks.CHISELED_TITANIUM);

    public static final Supplier<BlockItem> CUT_FERROALUMINUM = register("cut_ferroaluminum", TempAVPBlocks.CUT_FERROALUMINUM);

    public static final Supplier<BlockItem> CUT_FERROALUMINUM_SLAB = register(
        "cut_ferroaluminum_slab",
        TempAVPBlocks.CUT_FERROALUMINUM_SLAB
    );

    public static final Supplier<BlockItem> CUT_FERROALUMINUM_STAIRS = register(
        "cut_ferroaluminum_stairs",
        TempAVPBlocks.CUT_FERROALUMINUM_STAIRS
    );

    public static final Supplier<BlockItem> CUT_STEEL = register("cut_steel", TempAVPBlocks.CUT_STEEL);

    public static final Supplier<BlockItem> CUT_STEEL_SLAB = register("cut_steel_slab", TempAVPBlocks.CUT_STEEL_SLAB);

    public static final Supplier<BlockItem> CUT_STEEL_STAIRS = register("cut_steel_stairs", TempAVPBlocks.CUT_STEEL_STAIRS);

    public static final Supplier<BlockItem> CUT_TITANIUM = register("cut_titanium", TempAVPBlocks.CUT_TITANIUM);

    public static final Supplier<BlockItem> CUT_TITANIUM_SLAB = register("cut_titanium_slab", TempAVPBlocks.CUT_TITANIUM_SLAB);

    public static final Supplier<BlockItem> CUT_TITANIUM_STAIRS = register("cut_titanium_stairs", TempAVPBlocks.CUT_TITANIUM_STAIRS);

    public static final Supplier<BlockItem> DEEPSLATE_TITANIUM_ORE = register(
        "deepslate_titanium_ore",
        TempAVPBlocks.DEEPSLATE_TITANIUM_ORE
    );

    public static final Supplier<BlockItem> DEEPSLATE_ZINC_ORE = register("deepslate_zinc_ore", TempAVPBlocks.DEEPSLATE_ZINC_ORE);

    public static final Supplier<BlockItem> DESK_TERMINAL_BLOCK = register("desk_terminal", TempAVPBlocks.DESK_TERMINAL_BLOCK);

    public static final Supplier<BlockItem> FERROALUMINUM_BLOCK = register("ferroaluminum_block", TempAVPBlocks.FERROALUMINUM_BLOCK);

    public static final Supplier<BlockItem> FERROALUMINUM_BUTTON = register("ferroaluminum_button", TempAVPBlocks.FERROALUMINUM_BUTTON);

    public static final Supplier<BlockItem> FERROALUMINUM_CHAIN_FENCE = register(
        "ferroaluminum_chain_fence",
        TempAVPBlocks.FERROALUMINUM_CHAIN_FENCE
    );

    public static final Supplier<BlockItem> FERROALUMINUM_COLUMN = register("ferroaluminum_column", TempAVPBlocks.FERROALUMINUM_COLUMN);

    public static final Supplier<BlockItem> FERROALUMINUM_DOOR = register("ferroaluminum_door", TempAVPBlocks.FERROALUMINUM_DOOR);

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_SIDING = register(
        "ferroaluminum_fastened_siding",
        TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING
    );

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_SIDING_SLAB = register(
        "ferroaluminum_fastened_siding_slab",
        TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB
    );

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_SIDING_STAIRS = register(
        "ferroaluminum_fastened_siding_stairs",
        TempAVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS
    );

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_STANDING = register(
        "ferroaluminum_fastened_standing",
        TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING
    );

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_STANDING_SLAB = register(
        "ferroaluminum_fastened_standing_slab",
        TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB
    );

    public static final Supplier<BlockItem> FERROALUMINUM_FASTENED_STANDING_STAIRS = register(
        "ferroaluminum_fastened_standing_stairs",
        TempAVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS
    );

    public static final Supplier<BlockItem> FERROALUMINUM_GRATE = register("ferroaluminum_grate", TempAVPBlocks.FERROALUMINUM_GRATE);

    public static final Supplier<BlockItem> FERROALUMINUM_GRATE_SLAB = register(
        "ferroaluminum_grate_slab",
        TempAVPBlocks.FERROALUMINUM_GRATE_SLAB
    );

    public static final Supplier<BlockItem> FERROALUMINUM_GRATE_STAIRS = register(
        "ferroaluminum_grate_stairs",
        TempAVPBlocks.FERROALUMINUM_GRATE_STAIRS
    );

    public static final Supplier<BlockItem> FERROALUMINUM_PLATING = register("ferroaluminum_plating", TempAVPBlocks.FERROALUMINUM_PLATING);

    public static final Supplier<BlockItem> FERROALUMINUM_PLATING_SLAB = register(
        "ferroaluminum_plating_slab",
        TempAVPBlocks.FERROALUMINUM_PLATING_SLAB
    );

    public static final Supplier<BlockItem> FERROALUMINUM_PLATING_STAIRS = register(
        "ferroaluminum_plating_stairs",
        TempAVPBlocks.FERROALUMINUM_PLATING_STAIRS
    );

    public static final Supplier<BlockItem> FERROALUMINUM_PRESSURE_PLATE = register(
        "ferroaluminum_pressure_plate",
        TempAVPBlocks.FERROALUMINUM_PRESSURE_PLATE
    );

    public static final Supplier<BlockItem> FERROALUMINUM_SIDING = register("ferroaluminum_siding", TempAVPBlocks.FERROALUMINUM_SIDING);

    public static final Supplier<BlockItem> FERROALUMINUM_SIDING_SLAB = register(
        "ferroaluminum_siding_slab",
        TempAVPBlocks.FERROALUMINUM_SIDING_SLAB
    );

    public static final Supplier<BlockItem> FERROALUMINUM_SIDING_STAIRS = register(
        "ferroaluminum_siding_stairs",
        TempAVPBlocks.FERROALUMINUM_SIDING_STAIRS
    );

    public static final Supplier<BlockItem> FERROALUMINUM_SLAB = register("ferroaluminum_slab", TempAVPBlocks.FERROALUMINUM_SLAB);

    public static final Supplier<BlockItem> FERROALUMINUM_STAIRS = register("ferroaluminum_stairs", TempAVPBlocks.FERROALUMINUM_STAIRS);

    public static final Supplier<BlockItem> FERROALUMINUM_STANDING = register(
        "ferroaluminum_standing",
        TempAVPBlocks.FERROALUMINUM_STANDING
    );

    public static final Supplier<BlockItem> FERROALUMINUM_STANDING_SLAB = register(
        "ferroaluminum_standing_slab",
        TempAVPBlocks.FERROALUMINUM_STANDING_SLAB
    );

    public static final Supplier<BlockItem> FERROALUMINUM_STANDING_STAIRS = register(
        "ferroaluminum_standing_stairs",
        TempAVPBlocks.FERROALUMINUM_STANDING_STAIRS
    );

    public static final Supplier<BlockItem> FERROALUMINUM_TRAP_DOOR = register(
        "ferroaluminum_trapdoor",
        TempAVPBlocks.FERROALUMINUM_TRAP_DOOR
    );

    public static final Supplier<BlockItem> FERROALUMINUM_TREAD = register("ferroaluminum_tread", TempAVPBlocks.FERROALUMINUM_TREAD);

    public static final Supplier<BlockItem> FERROALUMINUM_TREAD_SLAB = register(
        "ferroaluminum_tread_slab",
        TempAVPBlocks.FERROALUMINUM_TREAD_SLAB
    );

    public static final Supplier<BlockItem> FERROALUMINUM_TREAD_STAIRS = register(
        "ferroaluminum_tread_stairs",
        TempAVPBlocks.FERROALUMINUM_TREAD_STAIRS
    );

    public static final Supplier<BlockItem> GALENA_ORE = register("galena_ore", TempAVPBlocks.GALENA_ORE);

    public static final Supplier<BlockItem> INDUSTRIAL_FURNACE_BLOCK = register(
        "industrial_furnace_block",
        TempAVPBlocks.INDUSTRIAL_FURNACE
    );

    public static final Supplier<BlockItem> INDUSTRIAL_GLASS = register("industrial_glass", TempAVPBlocks.INDUSTRIAL_GLASS);

    public static final Supplier<BlockItem> INDUSTRIAL_GLASS_DOOR = register("industrial_glass_door", TempAVPBlocks.INDUSTRIAL_GLASS_DOOR);

    public static final Supplier<BlockItem> INDUSTRIAL_GLASS_PANE = register("industrial_glass_pane", TempAVPBlocks.INDUSTRIAL_GLASS_PANE);

    public static final Supplier<BlockItem> INDUSTRIAL_GLASS_SLAB = register("industrial_glass_slab", TempAVPBlocks.INDUSTRIAL_GLASS_SLAB);

    public static final Supplier<BlockItem> INDUSTRIAL_GLASS_STAIRS = register(
        "industrial_glass_stairs",
        TempAVPBlocks.INDUSTRIAL_GLASS_STAIRS
    );

    public static final Supplier<BlockItem> INDUSTRIAL_GLASS_TRAP_DOOR = register(
        "industrial_glass_trapdoor",
        TempAVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR
    );

    public static final Supplier<BlockItem> IRRADIATED_RESIN = register("irradiated_resin", TempAVPBlocks.IRRADIATED_RESIN);

    public static final Supplier<BlockItem> IRRADIATED_RESIN_NODE = register("irradiated_resin_node", TempAVPBlocks.IRRADIATED_RESIN_NODE);

    public static final Supplier<BlockItem> IRRADIATED_RESIN_VEIN = register("irradiated_resin_vein", TempAVPBlocks.IRRADIATED_RESIN_VEIN);

    public static final Supplier<BlockItem> IRRADIATED_RESIN_WEB = register("irradiated_resin_web", TempAVPBlocks.IRRADIATED_RESIN_WEB);

    public static final Supplier<BlockItem> LEAD_BLOCK = register("lead_block", TempAVPBlocks.LEAD_BLOCK);

    public static final Supplier<BlockItem> LEAD_CHEST = registerDirect("lead_chest", LeadChestBlockItem::new);

    public static final Supplier<BlockItem> LITHIUM_BLOCK = register("lithium_block", TempAVPBlocks.LITHIUM_BLOCK);

    public static final Supplier<BlockItem> LITHIUM_ORE = register("lithium_ore", TempAVPBlocks.LITHIUM_ORE);

    public static final Supplier<BlockItem> MONAZITE_ORE = register("monazite_ore", TempAVPBlocks.MONAZITE_ORE);

    public static final Supplier<BlockItem> NETHER_RESIN = register(
        "nether_resin",
        TempAVPBlocks.NETHER_RESIN,
        new Item.Properties().fireResistant()
    );

    public static final Supplier<BlockItem> NETHER_RESIN_NODE = register(
        "nether_resin_node",
        TempAVPBlocks.NETHER_RESIN_NODE,
        new Item.Properties().fireResistant()
    );

    public static final Supplier<BlockItem> NETHER_RESIN_VEIN = register(
        "nether_resin_vein",
        TempAVPBlocks.NETHER_RESIN_VEIN,
        new Item.Properties().fireResistant()
    );

    public static final Supplier<BlockItem> NETHER_RESIN_WEB = register(
        "nether_resin_web",
        TempAVPBlocks.NETHER_RESIN_WEB,
        new Item.Properties().fireResistant()
    );

    public static final Supplier<BlockItem> RAW_BAUXITE_BLOCK = register("raw_bauxite_block", TempAVPBlocks.RAW_BAUXITE_BLOCK);

    public static final Supplier<BlockItem> RAW_GALENA_BLOCK = register("raw_galena_block", TempAVPBlocks.RAW_GALENA_BLOCK);

    public static final Supplier<BlockItem> RAW_MONAZITE_BLOCK = register("raw_monazite_block", TempAVPBlocks.RAW_MONAZITE_BLOCK);

    public static final Supplier<BlockItem> RAW_SILICA_BLOCK = register("raw_silica_block", TempAVPBlocks.RAW_SILICA_BLOCK);

    public static final Supplier<BlockItem> RAW_TITANIUM_BLOCK = register("raw_titanium_block", TempAVPBlocks.RAW_TITANIUM_BLOCK);

    public static final Supplier<BlockItem> RAW_ZINC_BLOCK = register("raw_zinc_block", TempAVPBlocks.RAW_ZINC_BLOCK);

    public static final Supplier<BlockItem> REDSTONE_GENERATOR = register("redstone_generator", TempAVPBlocks.REDSTONE_GENERATOR);

    public static final Supplier<BlockItem> RESIN = register("resin", TempAVPBlocks.RESIN);

    public static final Supplier<BlockItem> RESIN_BRICKS = register("resin_bricks", TempAVPBlocks.RESIN_BRICKS);

    public static final Supplier<BlockItem> RESIN_NODE = register("resin_node", TempAVPBlocks.RESIN_NODE);

    public static final Supplier<BlockItem> RESIN_O = register("resin_o", TempAVPBlocks.RESIN_O);

    public static final Supplier<BlockItem> RESIN_RIBBED = register("resin_ribbed", TempAVPBlocks.RESIN_RIBBED);

    public static final Supplier<BlockItem> RESIN_SMOOTH = register("resin_smooth", TempAVPBlocks.RESIN_SMOOTH);

    public static final Supplier<BlockItem> RESIN_VEIN = register("resin_vein", TempAVPBlocks.RESIN_VEIN);

    public static final Supplier<BlockItem> RESIN_WEB = register("resin_web", TempAVPBlocks.RESIN_WEB);

    public static final Supplier<BlockItem> RESONATOR_BLOCK = register("resonator", TempAVPBlocks.RESONATOR_BLOCK);

    // TODO: Make RoyalJellyBlockItem.
    public static final Supplier<BlockItem> ROYAL_JELLY_BLOCK = registerDirect(
        "royal_jelly_block",
        () -> new BlockItem(TempAVPBlocks.ROYAL_JELLY_BLOCK.get(), new Item.Properties())
    );

    public static final Supplier<BlockItem> SENTRY_TURRET = registerDirect("sentry_turret", SentryTurretBlockItem::new);

    public static final Supplier<BlockItem> SILICA_GRAVEL = register("silica_gravel", TempAVPBlocks.SILICA_GRAVEL);

    public static final Supplier<BlockItem> STEEL_BARS = register("steel_bars", TempAVPBlocks.STEEL_BARS);

    public static final Supplier<BlockItem> STEEL_BLOCK = register("steel_block", TempAVPBlocks.STEEL_BLOCK);

    public static final Supplier<BlockItem> STEEL_BUTTON = register("steel_button", TempAVPBlocks.STEEL_BUTTON);

    public static final Supplier<BlockItem> STEEL_CHAIN_FENCE = register("steel_chain_fence", TempAVPBlocks.STEEL_CHAIN_FENCE);

    public static final Supplier<BlockItem> STEEL_COLUMN = register("steel_column", TempAVPBlocks.STEEL_COLUMN);

    public static final Supplier<BlockItem> STEEL_DOOR = register("steel_door", TempAVPBlocks.STEEL_DOOR);

    public static final Supplier<BlockItem> STEEL_FASTENED_SIDING = register("steel_fastened_siding", TempAVPBlocks.STEEL_FASTENED_SIDING);

    public static final Supplier<BlockItem> STEEL_FASTENED_SIDING_SLAB = register(
        "steel_fastened_siding_slab",
        TempAVPBlocks.STEEL_FASTENED_SIDING_SLAB
    );

    public static final Supplier<BlockItem> STEEL_FASTENED_SIDING_STAIRS = register(
        "steel_fastened_siding_stairs",
        TempAVPBlocks.STEEL_FASTENED_SIDING_STAIRS
    );

    public static final Supplier<BlockItem> STEEL_FASTENED_STANDING = register(
        "steel_fastened_standing",
        TempAVPBlocks.STEEL_FASTENED_STANDING
    );

    public static final Supplier<BlockItem> STEEL_FASTENED_STANDING_SLAB = register(
        "steel_fastened_standing_slab",
        TempAVPBlocks.STEEL_FASTENED_STANDING_SLAB
    );

    public static final Supplier<BlockItem> STEEL_FASTENED_STANDING_STAIRS = register(
        "steel_fastened_standing_stairs",
        TempAVPBlocks.STEEL_FASTENED_STANDING_STAIRS
    );

    public static final Supplier<BlockItem> STEEL_GRATE = register("steel_grate", TempAVPBlocks.STEEL_GRATE);

    public static final Supplier<BlockItem> STEEL_GRATE_SLAB = register("steel_grate_slab", TempAVPBlocks.STEEL_GRATE_SLAB);

    public static final Supplier<BlockItem> STEEL_GRATE_STAIRS = register("steel_grate_stairs", TempAVPBlocks.STEEL_GRATE_STAIRS);

    public static final Supplier<BlockItem> STEEL_PLATING = register("steel_plating", TempAVPBlocks.STEEL_PLATING);

    public static final Supplier<BlockItem> STEEL_PLATING_SLAB = register("steel_plating_slab", TempAVPBlocks.STEEL_PLATING_SLAB);

    public static final Supplier<BlockItem> STEEL_PLATING_STAIRS = register("steel_plating_stairs", TempAVPBlocks.STEEL_PLATING_STAIRS);

    public static final Supplier<BlockItem> STEEL_PRESSURE_PLATE = register("steel_pressure_plate", TempAVPBlocks.STEEL_PRESSURE_PLATE);

    public static final Supplier<BlockItem> STEEL_SIDING = register("steel_siding", TempAVPBlocks.STEEL_SIDING);

    public static final Supplier<BlockItem> STEEL_SIDING_SLAB = register("steel_siding_slab", TempAVPBlocks.STEEL_SIDING_SLAB);

    public static final Supplier<BlockItem> STEEL_SIDING_STAIRS = register("steel_siding_stairs", TempAVPBlocks.STEEL_SIDING_STAIRS);

    public static final Supplier<BlockItem> STEEL_SLAB = register("steel_slab", TempAVPBlocks.STEEL_SLAB);

    public static final Supplier<BlockItem> STEEL_STAIRS = register("steel_stairs", TempAVPBlocks.STEEL_STAIRS);

    public static final Supplier<BlockItem> STEEL_STANDING = register("steel_standing", TempAVPBlocks.STEEL_STANDING);

    public static final Supplier<BlockItem> STEEL_STANDING_SLAB = register("steel_standing_slab", TempAVPBlocks.STEEL_STANDING_SLAB);

    public static final Supplier<BlockItem> STEEL_STANDING_STAIRS = register("steel_standing_stairs", TempAVPBlocks.STEEL_STANDING_STAIRS);

    public static final Supplier<BlockItem> STEEL_TRAP_DOOR = register("steel_trapdoor", TempAVPBlocks.STEEL_TRAP_DOOR);

    public static final Supplier<BlockItem> STEEL_TREAD = register("steel_tread", TempAVPBlocks.STEEL_TREAD);

    public static final Supplier<BlockItem> STEEL_TREAD_SLAB = register("steel_tread_slab", TempAVPBlocks.STEEL_TREAD_SLAB);

    public static final Supplier<BlockItem> STEEL_TREAD_STAIRS = register("steel_tread_stairs", TempAVPBlocks.STEEL_TREAD_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_BLOCK = register("titanium_block", TempAVPBlocks.TITANIUM_BLOCK);

    public static final Supplier<BlockItem> TITANIUM_BUTTON = register("titanium_button", TempAVPBlocks.TITANIUM_BUTTON);

    public static final Supplier<BlockItem> TITANIUM_CHAIN_FENCE = register("titanium_chain_fence", TempAVPBlocks.TITANIUM_CHAIN_FENCE);

    public static final Supplier<BlockItem> TITANIUM_COLUMN = register("titanium_column", TempAVPBlocks.TITANIUM_COLUMN);

    public static final Supplier<BlockItem> TITANIUM_DOOR = register("titanium_door", TempAVPBlocks.TITANIUM_DOOR);

    public static final Supplier<BlockItem> TITANIUM_FASTENED_SIDING = register(
        "titanium_fastened_siding",
        TempAVPBlocks.TITANIUM_FASTENED_SIDING
    );

    public static final Supplier<BlockItem> TITANIUM_FASTENED_SIDING_SLAB = register(
        "titanium_fastened_siding_slab",
        TempAVPBlocks.TITANIUM_FASTENED_SIDING_SLAB
    );

    public static final Supplier<BlockItem> TITANIUM_FASTENED_SIDING_STAIRS = register(
        "titanium_fastened_siding_stairs",
        TempAVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS
    );

    public static final Supplier<BlockItem> TITANIUM_FASTENED_STANDING = register(
        "titanium_fastened_standing",
        TempAVPBlocks.TITANIUM_FASTENED_STANDING
    );

    public static final Supplier<BlockItem> TITANIUM_FASTENED_STANDING_SLAB = register(
        "titanium_fastened_standing_slab",
        TempAVPBlocks.TITANIUM_FASTENED_STANDING_SLAB
    );

    public static final Supplier<BlockItem> TITANIUM_FASTENED_STANDING_STAIRS = register(
        "titanium_fastened_standing_stairs",
        TempAVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS
    );

    public static final Supplier<BlockItem> TITANIUM_GRATE = register("titanium_grate", TempAVPBlocks.TITANIUM_GRATE);

    public static final Supplier<BlockItem> TITANIUM_GRATE_SLAB = register("titanium_grate_slab", TempAVPBlocks.TITANIUM_GRATE_SLAB);

    public static final Supplier<BlockItem> TITANIUM_GRATE_STAIRS = register("titanium_grate_stairs", TempAVPBlocks.TITANIUM_GRATE_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_PLATING = register("titanium_plating", TempAVPBlocks.TITANIUM_PLATING);

    public static final Supplier<BlockItem> TITANIUM_PLATING_SLAB = register("titanium_plating_slab", TempAVPBlocks.TITANIUM_PLATING_SLAB);

    public static final Supplier<BlockItem> TITANIUM_PLATING_STAIRS = register(
        "titanium_plating_stairs",
        TempAVPBlocks.TITANIUM_PLATING_STAIRS
    );

    public static final Supplier<BlockItem> TITANIUM_PRESSURE_PLATE = register(
        "titanium_pressure_plate",
        TempAVPBlocks.TITANIUM_PRESSURE_PLATE
    );

    public static final Supplier<BlockItem> TITANIUM_SIDING = register("titanium_siding", TempAVPBlocks.TITANIUM_SIDING);

    public static final Supplier<BlockItem> TITANIUM_SIDING_SLAB = register("titanium_siding_slab", TempAVPBlocks.TITANIUM_SIDING_SLAB);

    public static final Supplier<BlockItem> TITANIUM_SIDING_STAIRS = register(
        "titanium_siding_stairs",
        TempAVPBlocks.TITANIUM_SIDING_STAIRS
    );

    public static final Supplier<BlockItem> TITANIUM_SLAB = register("titanium_slab", TempAVPBlocks.TITANIUM_SLAB);

    public static final Supplier<BlockItem> TITANIUM_STAIRS = register("titanium_stairs", TempAVPBlocks.TITANIUM_STAIRS);

    public static final Supplier<BlockItem> TITANIUM_STANDING = register("titanium_standing", TempAVPBlocks.TITANIUM_STANDING);

    public static final Supplier<BlockItem> TITANIUM_STANDING_SLAB = register(
        "titanium_standing_slab",
        TempAVPBlocks.TITANIUM_STANDING_SLAB
    );

    public static final Supplier<BlockItem> TITANIUM_STANDING_STAIRS = register(
        "titanium_standing_stairs",
        TempAVPBlocks.TITANIUM_STANDING_STAIRS
    );

    public static final Supplier<BlockItem> TITANIUM_TRAP_DOOR = register("titanium_trapdoor", TempAVPBlocks.TITANIUM_TRAP_DOOR);

    public static final Supplier<BlockItem> TITANIUM_TREAD = register("titanium_tread", TempAVPBlocks.TITANIUM_TREAD);

    public static final Supplier<BlockItem> TITANIUM_TREAD_SLAB = register("titanium_tread_slab", TempAVPBlocks.TITANIUM_TREAD_SLAB);

    public static final Supplier<BlockItem> TITANIUM_TREAD_STAIRS = register("titanium_tread_stairs", TempAVPBlocks.TITANIUM_TREAD_STAIRS);

    public static final Supplier<BlockItem> TRINITITE_BLOCK = register("trinitite_block", TempAVPBlocks.TRINITITE_BLOCK);

    public static final Supplier<BlockItem> TRIP_MINE_BLOCK = register("trip_mine", TempAVPBlocks.TRIP_MINE_BLOCK);

    public static final Supplier<BlockItem> URANIUM_BLOCK = register("uranium_block", TempAVPBlocks.URANIUM_BLOCK);

    public static final Supplier<BlockItem> ZINC_BLOCK = register("zinc_block", TempAVPBlocks.ZINC_BLOCK);

    public static final Supplier<BlockItem> ZINC_ORE = register("zinc_ore", TempAVPBlocks.ZINC_ORE);

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_slab",
                            TempAVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PADDING.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor)
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
                            TempAVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    private static Supplier<BlockItem> register(String id, Supplier<Block> blockSupplier) {
        return register(id, blockSupplier, new Item.Properties());
    }

    private static Supplier<BlockItem> register(String id, Supplier<Block> blockSupplier, Item.Properties properties) {
        return registerDirect(id, () -> new BlockItem(blockSupplier.get(), properties));
    }

    private static Supplier<BlockItem> registerDirect(String id, Supplier<BlockItem> blockItemSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.ITEM, id, blockItemSupplier);
    }

    public static void initialize() {}
}
