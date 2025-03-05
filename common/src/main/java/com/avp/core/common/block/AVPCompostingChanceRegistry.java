package com.avp.core.common.block;

import com.avp.core.common.item.AVPItems;
import com.avp.core.platform.service.Services;

public class AVPCompostingChanceRegistry {

    public static void initialize() {
        Services.REGISTRY.registerCompostingChance(AVPItems.NETHER_RESIN_BALL, 0.3F);
        Services.REGISTRY.registerCompostingChance(AVPItems.RESIN_BALL, 0.3F);

        Services.REGISTRY.registerCompostingChance(AVPBlocks.NETHER_RESIN, 1F);
        Services.REGISTRY.registerCompostingChance(AVPBlocks.NETHER_RESIN_NODE, 1F);
        Services.REGISTRY.registerCompostingChance(AVPBlocks.NETHER_RESIN_VEIN, 0.3F);
        Services.REGISTRY.registerCompostingChance(AVPBlocks.NETHER_RESIN_WEB, 0.65F);

        Services.REGISTRY.registerCompostingChance(AVPBlocks.RESIN, 1F);
        Services.REGISTRY.registerCompostingChance(AVPBlocks.RESIN_NODE, 1F);
        Services.REGISTRY.registerCompostingChance(AVPBlocks.RESIN_VEIN, 0.3F);
        Services.REGISTRY.registerCompostingChance(AVPBlocks.RESIN_WEB, 0.65F);
    }
}
