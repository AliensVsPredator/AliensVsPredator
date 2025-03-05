package com.avp.common.block;

import com.avp.common.item.AVPItems;

public class CompostingChanceRegistry {

    public static void initialize() {
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPItems.NETHER_RESIN_BALL, 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPItems.RESIN_BALL, 0.3F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.NETHER_RESIN, 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.NETHER_RESIN_NODE, 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.NETHER_RESIN_VEIN, 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.NETHER_RESIN_WEB, 0.65F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.RESIN, 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.RESIN_NODE, 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.RESIN_VEIN, 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.RESIN_WEB, 0.65F);
    }
}
