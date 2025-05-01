package com.avp.fabric.common.block_item;

import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.common.item.RoyalJellyBlockItem;

public class AVPBlockItems {

    public static final BlockItem BLUEPRINT_BLOCK = register(AVPBlocks.BLUEPRINT_BLOCK);

    public static final BlockItem REDSTONE_GENERATOR = register(AVPBlocks.REDSTONE_GENERATOR);

    public static final BlockItem DESK_TERMINAL_BLOCK = register(AVPBlocks.DESK_TERMINAL_BLOCK);

    public static final BlockItem TRIP_MINE_BLOCK = register(AVPBlocks.TRIP_MINE_BLOCK);

    public static final BlockItem RESONATOR_BLOCK = register(AVPBlocks.RESONATOR_BLOCK);

    public static final BlockItem SENTRY_TURRET = register("sentry_turret", SentryTurretBlockItem::new);

    public static final BlockItem ASH_BLOCK = register(AVPBlocks.ASH_BLOCK);

    public static final BlockItem TRINITITE_BLOCK = register(AVPBlocks.TRINITITE_BLOCK);

    public static final BlockItem NUKE_BLOCK = register(AVPBlocks.NUKE_BLOCK);

    public static final BlockItem ROYAL_JELLY_BLOCK = registerCustomBlockItem(
        new RoyalJellyBlockItem(AVPBlocks.ROYAL_JELLY_BLOCK),
        AVPBlocks.ROYAL_JELLY_BLOCK
    );

    public static final BlockItem ALUMINUM_BLOCK = register(AVPBlocks.ALUMINUM_BLOCK);

    public static final BlockItem AUTUNITE_BLOCK = register(AVPBlocks.AUTUNITE_BLOCK);

    public static final BlockItem BRASS_BLOCK = register(AVPBlocks.BRASS_BLOCK);

    public static final BlockItem CHISELED_FERROALUMINUM = register(AVPBlocks.CHISELED_FERROALUMINUM);

    public static final BlockItem CHISELED_STEEL = register(AVPBlocks.CHISELED_STEEL);

    public static final BlockItem CHISELED_TITANIUM = register(AVPBlocks.CHISELED_TITANIUM);

    public static final BlockItem CUT_FERROALUMINUM = register(AVPBlocks.CUT_FERROALUMINUM);

    public static final BlockItem CUT_FERROALUMINUM_SLAB = register(AVPBlocks.CUT_FERROALUMINUM_SLAB);

    public static final BlockItem CUT_FERROALUMINUM_STAIRS = register(AVPBlocks.CUT_FERROALUMINUM_STAIRS);

    public static final BlockItem CUT_STEEL = register(AVPBlocks.CUT_STEEL);

    public static final BlockItem CUT_STEEL_SLAB = register(AVPBlocks.CUT_STEEL_SLAB);

    public static final BlockItem CUT_STEEL_STAIRS = register(AVPBlocks.CUT_STEEL_STAIRS);

    public static final BlockItem CUT_TITANIUM = register(AVPBlocks.CUT_TITANIUM);

    public static final BlockItem CUT_TITANIUM_SLAB = register(AVPBlocks.CUT_TITANIUM_SLAB);

    public static final BlockItem CUT_TITANIUM_STAIRS = register(AVPBlocks.CUT_TITANIUM_STAIRS);

    public static final BlockItem FERROALUMINUM_BUTTON = register(AVPBlocks.FERROALUMINUM_BUTTON);

    public static final BlockItem FERROALUMINUM_DOOR = register(AVPBlocks.FERROALUMINUM_DOOR);

    public static final BlockItem FERROALUMINUM_PRESSURE_PLATE = register(AVPBlocks.FERROALUMINUM_PRESSURE_PLATE);

    public static final BlockItem FERROALUMINUM_TRAP_DOOR = register(AVPBlocks.FERROALUMINUM_TRAP_DOOR);

    public static final BlockItem INDUSTRIAL_GLASS = register(AVPBlocks.INDUSTRIAL_GLASS);

    public static final BlockItem INDUSTRIAL_GLASS_PANE = register(AVPBlocks.INDUSTRIAL_GLASS_PANE);

    public static final BlockItem INDUSTRIAL_GLASS_DOOR = register(AVPBlocks.INDUSTRIAL_GLASS_DOOR);

    public static final BlockItem INDUSTRIAL_GLASS_TRAP_DOOR = register(AVPBlocks.INDUSTRIAL_GLASS_TRAP_DOOR);

    public static final BlockItem LEAD_BLOCK = register(AVPBlocks.LEAD_BLOCK);

    public static final BlockItem LEAD_CHEST = register("lead_chest", LeadChestBlockItem::new);

    public static final BlockItem AMMO_CHEST = register("ammo_chest", AmmoChestBlockItem::new);

    public static final BlockItem NETHER_RESIN = register(new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN);

    public static final BlockItem NETHER_RESIN_NODE = register(new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN_NODE);

    public static final BlockItem NETHER_RESIN_VEIN = register(new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN_VEIN);

    public static final BlockItem NETHER_RESIN_WEB = register(new Item.Properties().fireResistant(), AVPBlocks.NETHER_RESIN_WEB);

    public static final BlockItem ABERRANT_RESIN = register(new Item.Properties().fireResistant(), AVPBlocks.ABERRANT_RESIN);

    public static final BlockItem ABERRANT_RESIN_NODE = register(new Item.Properties().fireResistant(), AVPBlocks.ABERRANT_RESIN_NODE);

    public static final BlockItem ABERRANT_RESIN_VEIN = register(new Item.Properties().fireResistant(), AVPBlocks.ABERRANT_RESIN_VEIN);

    public static final BlockItem ABERRANT_RESIN_WEB = register(new Item.Properties().fireResistant(), AVPBlocks.ABERRANT_RESIN_WEB);

    public static final BlockItem IRRADIATED_RESIN = register(AVPBlocks.IRRADIATED_RESIN);

    public static final BlockItem IRRADIATED_RESIN_NODE = register(AVPBlocks.IRRADIATED_RESIN_NODE);

    public static final BlockItem IRRADIATED_RESIN_VEIN = register(AVPBlocks.IRRADIATED_RESIN_VEIN);

    public static final BlockItem IRRADIATED_RESIN_WEB = register(AVPBlocks.IRRADIATED_RESIN_WEB);

    public static final BlockItem RAW_BAUXITE_BLOCK = register(AVPBlocks.RAW_BAUXITE_BLOCK);

    public static final BlockItem RAW_GALENA_BLOCK = register(AVPBlocks.RAW_GALENA_BLOCK);

    public static final BlockItem LITHIUM_BLOCK = register(AVPBlocks.LITHIUM_BLOCK);

    public static final BlockItem RAW_MONAZITE_BLOCK = register(AVPBlocks.RAW_MONAZITE_BLOCK);

    public static final BlockItem RAW_SILICA_BLOCK = register(AVPBlocks.RAW_SILICA_BLOCK);

    public static final BlockItem RAW_TITANIUM_BLOCK = register(AVPBlocks.RAW_TITANIUM_BLOCK);

    public static final BlockItem RAW_ZINC_BLOCK = register(AVPBlocks.RAW_ZINC_BLOCK);

    public static final BlockItem RAZOR_WIRE = register(AVPBlocks.RAZOR_WIRE);

    public static final BlockItem RESIN = register(AVPBlocks.RESIN);

    public static final BlockItem RESIN_NODE = register(AVPBlocks.RESIN_NODE);

    public static final BlockItem RESIN_VEIN = register(AVPBlocks.RESIN_VEIN);

    public static final BlockItem RESIN_WEB = register(AVPBlocks.RESIN_WEB);

    public static final BlockItem RESIN_BRICKS = register(AVPBlocks.RESIN_BRICKS);

    public static final BlockItem RESIN_O = register(AVPBlocks.RESIN_O);

    public static final BlockItem RESIN_RIBBED = register(AVPBlocks.RESIN_RIBBED);

    public static final BlockItem RESIN_SMOOTH = register(AVPBlocks.RESIN_SMOOTH);

    public static final BlockItem SILICA_GRAVEL = register(AVPBlocks.SILICA_GRAVEL);

    public static final BlockItem STEEL_BUTTON = register(AVPBlocks.STEEL_BUTTON);

    public static final BlockItem STEEL_DOOR = register(AVPBlocks.STEEL_DOOR);

    // Metal Block - Slabs and Stairs
    public static final BlockItem INDUSTRIAL_GLASS_SLAB = register(AVPBlocks.INDUSTRIAL_GLASS_SLAB);

    public static final BlockItem INDUSTRIAL_GLASS_STAIRS = register(AVPBlocks.INDUSTRIAL_GLASS_STAIRS);

    public static final BlockItem STEEL_TRAP_DOOR = register(AVPBlocks.STEEL_TRAP_DOOR);

    public static final BlockItem TITANIUM_BUTTON = register(AVPBlocks.TITANIUM_BUTTON);

    public static final BlockItem TITANIUM_DOOR = register(AVPBlocks.TITANIUM_DOOR);

    public static final BlockItem TITANIUM_PRESSURE_PLATE = register(AVPBlocks.TITANIUM_PRESSURE_PLATE);

    public static final BlockItem TITANIUM_TRAP_DOOR = register(AVPBlocks.TITANIUM_TRAP_DOOR);

    public static final BlockItem INDUSTRIAL_FURNACE_BLOCK = register(AVPBlocks.INDUSTRIAL_FURNACE);

    // Siding - Slabs and Stairs


    // Fastened Siding - Slabs and Stairs

    // Fastened Standing - Slabs and Stairs

    // Plating - Slabs and Stairs

    // Tread - Slabs and Stairs

    // Grate - Slabs and Stairs



    @Deprecated
    public static BlockItem register(Block block) {
        return register(new Item.Properties(), block);
    }

    @Deprecated
    public static BlockItem register(Item.Properties properties, Block block) {
        return register(properties, block, BuiltInRegistries.BLOCK.getKey(block).getPath());
    }

    @Deprecated
    public static BlockItem register(Item.Properties properties, Block block, String id) {
        var resourceLocation = AVPResources.location(id);
        var blockItem = new BlockItem(block, properties);

        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, blockItem);
    }

    @Deprecated
    public static BlockItem registerCustomBlockItem(BlockItem blockItem, Block block) {
        var resourceLocation = AVPResources.location(BuiltInRegistries.BLOCK.getKey(block).getPath());

        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, blockItem);
    }

    public static BlockItem register(String id, Supplier<BlockItem> blockItemSupplier) {
        var resourceLocation = AVPResources.location(id);

        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, blockItemSupplier.get());
    }

    public static void initialize() {
        AzIdentityRegistry.register(RESONATOR_BLOCK, SENTRY_TURRET);
    }
}
