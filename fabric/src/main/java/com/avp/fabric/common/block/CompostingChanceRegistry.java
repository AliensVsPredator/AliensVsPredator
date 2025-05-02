package com.avp.fabric.common.block;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.TempAVPItems;

public class CompostingChanceRegistry {

    public static void initialize() {
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPItems.IRRADIATED_RESIN_BALL.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPItems.ABERRANT_RESIN_BALL.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPItems.NETHER_RESIN_BALL.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPItems.RESIN_BALL.get(), 0.3F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.IRRADIATED_RESIN.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.IRRADIATED_RESIN_NODE.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.IRRADIATED_RESIN_VEIN.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.IRRADIATED_RESIN_WEB.get(), 0.65F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.ABERRANT_RESIN.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.ABERRANT_RESIN_NODE.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.ABERRANT_RESIN_VEIN.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.ABERRANT_RESIN_WEB.get(), 0.65F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.NETHER_RESIN.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.NETHER_RESIN_NODE.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.NETHER_RESIN_VEIN.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.NETHER_RESIN_WEB.get(), 0.65F);

        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.RESIN.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.RESIN_NODE.get(), 1F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.RESIN_VEIN.get(), 0.3F);
        net.fabricmc.fabric.api.registry.CompostingChanceRegistry.INSTANCE.add(TempAVPBlocks.RESIN_WEB.get(), 0.65F);
    }
}
