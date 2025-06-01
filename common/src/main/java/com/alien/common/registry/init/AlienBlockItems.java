package com.alien.common.registry.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.item.AVPBlockItems;

public class AlienBlockItems {

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN = AVPBlockItems.register(
        "aberrant_resin",
        AlienBlocks.ABERRANT_RESIN
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_NODE = AVPBlockItems.register(
        "aberrant_resin_node",
        AlienBlocks.ABERRANT_RESIN_NODE
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_SLAB = AVPBlockItems.register(
        "aberrant_resin_slab",
        AlienBlocks.ABERRANT_RESIN_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_STAIRS = AVPBlockItems.register(
        "aberrant_resin_stairs",
        AlienBlocks.ABERRANT_RESIN_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_VEIN = AVPBlockItems.register(
        "aberrant_resin_vein",
        AlienBlocks.ABERRANT_RESIN_VEIN
    );

    public static final AVPDeferredHolder<BlockItem> ABERRANT_RESIN_WEB = AVPBlockItems.register(
        "aberrant_resin_web",
        AlienBlocks.ABERRANT_RESIN_WEB
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN = AVPBlockItems.register(
        "irradiated_resin",
        AlienBlocks.IRRADIATED_RESIN
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_NODE = AVPBlockItems.register(
        "irradiated_resin_node",
        AlienBlocks.IRRADIATED_RESIN_NODE
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_SLAB = AVPBlockItems.register(
        "irradiated_resin_slab",
        AlienBlocks.IRRADIATED_RESIN_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_STAIRS = AVPBlockItems.register(
        "irradiated_resin_stairs",
        AlienBlocks.IRRADIATED_RESIN_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_VEIN = AVPBlockItems.register(
        "irradiated_resin_vein",
        AlienBlocks.IRRADIATED_RESIN_VEIN
    );

    public static final AVPDeferredHolder<BlockItem> IRRADIATED_RESIN_WEB = AVPBlockItems.register(
        "irradiated_resin_web",
        AlienBlocks.IRRADIATED_RESIN_WEB
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN = AVPBlockItems.register(
        "nether_resin",
        AlienBlocks.NETHER_RESIN,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_NODE = AVPBlockItems.register(
        "nether_resin_node",
        AlienBlocks.NETHER_RESIN_NODE,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_SLAB = AVPBlockItems.register(
        "nether_resin_slab",
        AlienBlocks.NETHER_RESIN_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_STAIRS = AVPBlockItems.register(
        "nether_resin_stairs",
        AlienBlocks.NETHER_RESIN_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_VEIN = AVPBlockItems.register(
        "nether_resin_vein",
        AlienBlocks.NETHER_RESIN_VEIN,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> NETHER_RESIN_WEB = AVPBlockItems.register(
        "nether_resin_web",
        AlienBlocks.NETHER_RESIN_WEB,
        new Item.Properties().fireResistant()
    );

    public static final AVPDeferredHolder<BlockItem> RESIN = AVPBlockItems.register("resin", AlienBlocks.RESIN);

    public static final AVPDeferredHolder<BlockItem> RESIN_BRICKS = AVPBlockItems.register("resin_bricks", AlienBlocks.RESIN_BRICKS);

    public static final AVPDeferredHolder<BlockItem> RESIN_NODE = AVPBlockItems.register("resin_node", AlienBlocks.RESIN_NODE);

    public static final AVPDeferredHolder<BlockItem> RESIN_O = AVPBlockItems.register("resin_o", AlienBlocks.RESIN_O);

    public static final AVPDeferredHolder<BlockItem> RESIN_RIBBED = AVPBlockItems.register("resin_ribbed", AlienBlocks.RESIN_RIBBED);

    public static final AVPDeferredHolder<BlockItem> RESIN_SMOOTH = AVPBlockItems.register("resin_smooth", AlienBlocks.RESIN_SMOOTH);

    public static final AVPDeferredHolder<BlockItem> RESIN_SLAB = AVPBlockItems.register(
        "resin_slab",
        AlienBlocks.RESIN_SLAB
    );

    public static final AVPDeferredHolder<BlockItem> RESIN_STAIRS = AVPBlockItems.register(
        "resin_stairs",
        AlienBlocks.RESIN_STAIRS
    );

    public static final AVPDeferredHolder<BlockItem> RESIN_VEIN = AVPBlockItems.register("resin_vein", AlienBlocks.RESIN_VEIN);

    public static final AVPDeferredHolder<BlockItem> RESIN_WEB = AVPBlockItems.register("resin_web", AlienBlocks.RESIN_WEB);

    public static void initialize() {}
}
