package com.predator.client;

import com.predator.client.render.armor.JunglePredatorArmorRenderer;
import com.predator.client.render.block.TripMineRenderer;
import com.predator.client.render.entity.YautjaRenderer;
import com.predator.common.registry.init.PredatorEntityTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import java.util.List;

import com.avp.client.render.item.SpinningItemRenderer;
import com.avp.common.registry.init.AVPBlockEntityTypes;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.service.Services;

public class PredatorClient {

    public static void initialize() {
        registerArmorRenderers();
        registerBlockEntityRenderers();
        registerEntityRenderers();
    }

    private static void registerArmorRenderers() {
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            JunglePredatorArmorRenderer::new,
            List.of(
                AVPArmorItems.JUNGLE_PREDATOR_HELMET,
                AVPArmorItems.JUNGLE_PREDATOR_CHESTPLATE,
                AVPArmorItems.JUNGLE_PREDATOR_LEGGINGS,
                AVPArmorItems.JUNGLE_PREDATOR_BOOTS
            )
        );
    }

    private static void registerBlockEntityRenderers() {
        Services.CLIENT_REGISTRY.registerBlockEntityRenderer(
            AVPBlockEntityTypes.TRIP_MINE,
            (BlockEntityRendererProvider.Context rendererDispatcherIn) -> new TripMineRenderer()
        );
    }

    private static void registerEntityRenderers() {
        Services.CLIENT_REGISTRY.registerEntityRenderer(PredatorEntityTypes.SHURIKEN, SpinningItemRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(PredatorEntityTypes.SMART_DISC, SpinningItemRenderer::new);
        Services.CLIENT_REGISTRY.registerEntityRenderer(PredatorEntityTypes.YAUTJA, YautjaRenderer::new);
    }
}
