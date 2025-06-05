package com.alien.common.registry.init.item;

import com.alien.common.registry.init.block.AlienResinBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPBlockItems;

public class AlienBlockItems {

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN = AVPBlockItems.register(
        "aberrant_resin",
        AlienResinBlocks.ABERRANT_RESIN
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_NODE = AVPBlockItems.register(
        "aberrant_resin_node",
        AlienResinBlocks.ABERRANT_RESIN_NODE
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_SLAB = AVPBlockItems.register(
        "aberrant_resin_slab",
        AlienResinBlocks.ABERRANT_RESIN_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_STAIRS = AVPBlockItems.register(
        "aberrant_resin_stairs",
        AlienResinBlocks.ABERRANT_RESIN_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_VEIN = AVPBlockItems.register(
        "aberrant_resin_vein",
        AlienResinBlocks.ABERRANT_RESIN_VEIN
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_WEB = AVPBlockItems.register(
        "aberrant_resin_web",
        AlienResinBlocks.ABERRANT_RESIN_WEB
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN = AVPBlockItems.register(
        "irradiated_resin",
        AlienResinBlocks.IRRADIATED_RESIN
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_NODE = AVPBlockItems.register(
        "irradiated_resin_node",
        AlienResinBlocks.IRRADIATED_RESIN_NODE
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_SLAB = AVPBlockItems.register(
        "irradiated_resin_slab",
        AlienResinBlocks.IRRADIATED_RESIN_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_STAIRS = AVPBlockItems.register(
        "irradiated_resin_stairs",
        AlienResinBlocks.IRRADIATED_RESIN_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_VEIN = AVPBlockItems.register(
        "irradiated_resin_vein",
        AlienResinBlocks.IRRADIATED_RESIN_VEIN
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_WEB = AVPBlockItems.register(
        "irradiated_resin_web",
        AlienResinBlocks.IRRADIATED_RESIN_WEB
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN = AVPBlockItems.register(
        "nether_resin",
        AlienResinBlocks.NETHER_RESIN,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_NODE = AVPBlockItems.register(
        "nether_resin_node",
        AlienResinBlocks.NETHER_RESIN_NODE,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_SLAB = AVPBlockItems.register(
        "nether_resin_slab",
        AlienResinBlocks.NETHER_RESIN_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_STAIRS = AVPBlockItems.register(
        "nether_resin_stairs",
        AlienResinBlocks.NETHER_RESIN_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_VEIN = AVPBlockItems.register(
        "nether_resin_vein",
        AlienResinBlocks.NETHER_RESIN_VEIN,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_WEB = AVPBlockItems.register(
        "nether_resin_web",
        AlienResinBlocks.NETHER_RESIN_WEB,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> RESIN = AVPBlockItems.register("resin", AlienResinBlocks.RESIN);

    public static final AVPDeferredHolder<BlockItem> RESIN_BRICKS = AVPBlockItems.register("resin_bricks", AlienResinBlocks.RESIN_BRICKS);

    public static final AVPDeferredHolder<BlockItem> RESIN_BRICK_SLAB = AVPBlockItems.register(
        "resin_brick_slab",
        AlienResinBlocks.RESIN_BRICK_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> RESIN_BRICK_STAIRS = AVPBlockItems.register(
        "resin_brick_stairs",
        AlienResinBlocks.RESIN_BRICK_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> RESIN_NODE = AVPBlockItems.register("resin_node", AlienResinBlocks.RESIN_NODE);

    public static final AVPDeferredHolder<BlockItem> RESIN_SLAB = AVPBlockItems.register(
        "resin_slab",
        AlienResinBlocks.RESIN_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> RESIN_STAIRS = AVPBlockItems.register(
        "resin_stairs",
        AlienResinBlocks.RESIN_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> RESIN_VEIN = AVPBlockItems.register("resin_vein", AlienResinBlocks.RESIN_VEIN);

    public static final AVPDeferredHolder<BlockItem> RESIN_VENT = AVPBlockItems.register("resin_vent", AlienResinBlocks.RESIN_VENT);

    public static final AVPDeferredHolder<BlockItem> RESIN_WEB = AVPBlockItems.register("resin_web", AlienResinBlocks.RESIN_WEB);

    public static final AVPDeferredHolder<BlockItem> RIBBED_RESIN = AVPBlockItems.register("ribbed_resin", AlienResinBlocks.RIBBED_RESIN);

    public static final AVPDeferredHolder<BlockItem> SMOOTH_RESIN = AVPBlockItems.register("smooth_resin", AlienResinBlocks.SMOOTH_RESIN);

    public static void initialize() {}
}
