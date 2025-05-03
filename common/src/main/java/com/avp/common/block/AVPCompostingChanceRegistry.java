package com.avp.common.block;

import com.avp.common.item.TempAVPItems;
import com.avp.service.Services;

public class AVPCompostingChanceRegistry {

    public static void initialize() {
        Services.REGISTRY.registerCompostableItem(TempAVPItems.IRRADIATED_RESIN_BALL, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(TempAVPItems.ABERRANT_RESIN_BALL, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(TempAVPItems.NETHER_RESIN_BALL, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(TempAVPItems.RESIN_BALL, 0.3F, false, false);

        Services.REGISTRY.registerCompostableItem(AVPBlocks.IRRADIATED_RESIN, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.IRRADIATED_RESIN_NODE, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.IRRADIATED_RESIN_VEIN, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.IRRADIATED_RESIN_WEB, 0.65F, false, false);

        Services.REGISTRY.registerCompostableItem(AVPBlocks.ABERRANT_RESIN, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.ABERRANT_RESIN_NODE, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.ABERRANT_RESIN_VEIN, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.ABERRANT_RESIN_WEB, 0.65F, false, false);

        Services.REGISTRY.registerCompostableItem(AVPBlocks.NETHER_RESIN, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.NETHER_RESIN_NODE, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.NETHER_RESIN_VEIN, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.NETHER_RESIN_WEB, 0.65F, false, false);

        Services.REGISTRY.registerCompostableItem(AVPBlocks.RESIN, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.RESIN_NODE, 1F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.RESIN_VEIN, 0.3F, false, false);
        Services.REGISTRY.registerCompostableItem(AVPBlocks.RESIN_WEB, 0.65F, false, false);
    }
}
