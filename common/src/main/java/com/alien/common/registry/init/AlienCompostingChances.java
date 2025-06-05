package com.alien.common.registry.init;

import com.alien.common.registry.init.block.AlienResinBlocks;

import com.avp.service.Services;

public class AlienCompostingChances {

    public static void initialize() {
        Services.REGISTRY.registerCompostableItem(AlienItems.IRRADIATED_RESIN_BALL, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienItems.ABERRANT_RESIN_BALL, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienItems.NETHER_RESIN_BALL, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienItems.RESIN_BALL, 0.3F, false, false);

        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.IRRADIATED_RESIN, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.IRRADIATED_RESIN_NODE, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.IRRADIATED_RESIN_SLAB, 0.5F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.IRRADIATED_RESIN_STAIRS, 0.66F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.IRRADIATED_RESIN_VEIN, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.IRRADIATED_RESIN_WEB, 0.65F, false, false);

        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.ABERRANT_RESIN, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.ABERRANT_RESIN_NODE, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.ABERRANT_RESIN_SLAB, 0.5F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.ABERRANT_RESIN_STAIRS, 0.66F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.ABERRANT_RESIN_VEIN, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.ABERRANT_RESIN_WEB, 0.65F, false, false);

        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.NETHER_RESIN, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.NETHER_RESIN_NODE, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.NETHER_RESIN_SLAB, 0.5F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.NETHER_RESIN_STAIRS, 0.66F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.NETHER_RESIN_VEIN, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.NETHER_RESIN_WEB, 0.65F, false, false);

        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.RESIN, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.RESIN_NODE, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.RESIN_SLAB, 0.5F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.RESIN_STAIRS, 0.66F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.RESIN_VEIN, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AlienResinBlocks.RESIN_WEB, 0.65F, false, false);
    }
}
