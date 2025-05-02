package com.avp.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;

import com.avp.client.render.block.DeskTerminalRenderer;
import com.avp.client.render.block.ResonatorRenderer;
import com.avp.client.render.block.TripMineRenderer;
import com.avp.client.render.item.DeskTerminalItemRenderer;
import com.avp.client.render.item.ResonatorItemRenderer;
import com.avp.client.render.item.TripMineItemRenderer;
import com.avp.common.block.entity.AVPBlockEntityTypes;
import com.avp.common.item.TempAVPBlockItems;
import com.avp.service.Services;

public class AVPClient {

    public static void initialize() {
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(AVPBlockEntityTypes.AMMO_CHEST, ChestRenderer::new);
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(
            AVPBlockEntityTypes.DESK_TERMINAL,
            (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new DeskTerminalRenderer()
        );
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(AVPBlockEntityTypes.LEAD_CHEST, ChestRenderer::new);
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(
            AVPBlockEntityTypes.RESONATOR,
            (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new ResonatorRenderer()
        );
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(
            AVPBlockEntityTypes.TRIP_MINE,
            (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new TripMineRenderer()
        );

        Services.CLIENT_REGISTRY.registerItemRenderer(TempAVPBlockItems.DESK_TERMINAL_BLOCK, name -> DeskTerminalItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(TempAVPBlockItems.TRIP_MINE_BLOCK, name -> TripMineItemRenderer::new);
        Services.CLIENT_REGISTRY.registerItemRenderer(TempAVPBlockItems.RESONATOR_BLOCK, name -> ResonatorItemRenderer::new);
    }
}
