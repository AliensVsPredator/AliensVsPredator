package com.avp.fabric.common.block;

import com.avp.common.block.AVPBlocks;
import com.avp.common.item.TempAVPItems;

public class CompostingChanceRegistry {

    public static void initialize() {
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPItems.IRRADIATED_RESIN_BALL.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPItems.ABERRANT_RESIN_BALL.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPItems.NETHER_RESIN_BALL.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPItems.RESIN_BALL.get(), 0.3F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.IRRADIATED_RESIN.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.IRRADIATED_RESIN_NODE.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.IRRADIATED_RESIN_VEIN.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.IRRADIATED_RESIN_WEB.get(), 0.65F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.ABERRANT_RESIN.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.ABERRANT_RESIN_NODE.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.ABERRANT_RESIN_VEIN.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.ABERRANT_RESIN_WEB.get(), 0.65F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.NETHER_RESIN.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.NETHER_RESIN_NODE.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.NETHER_RESIN_VEIN.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.NETHER_RESIN_WEB.get(), 0.65F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.RESIN.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.RESIN_NODE.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.RESIN_VEIN.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(AVPBlocks.RESIN_WEB.get(), 0.65F);
    }
}
