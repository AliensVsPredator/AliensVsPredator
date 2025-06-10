package com.predator.client;

import com.predator.client.render.armor.JunglePredatorArmorRenderer;
import com.predator.client.render.block.TripMineRenderer;
import com.predator.client.render.entity.YautjaRenderer;
import com.predator.common.registry.init.PredatorBlockItems;
import com.predator.common.registry.init.PredatorEntityTypes;
import com.predator.common.registry.init.item.PredatorArmorItems;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import java.util.List;

import com.avp.client.render.item.SpinningItemRenderer;
import com.avp.client.render.item.TripMineItemRenderer;
import com.avp.common.registry.init.AVPBlockEntityTypes;
import com.avp.service.Services;

public class PredatorClient {

    public static void initialize() {
        registerArmorRenderers();
        registerBlockEntityRenderers();
        registerEntityRenderers();
        registerItemRenderers();
    }

    private static void registerArmorRenderers() {
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            JunglePredatorArmorRenderer::new,
            List.of(
                PredatorArmorItems.JUNGLE_PREDATOR_HELMET,
                PredatorArmorItems.JUNGLE_PREDATOR_CHESTPLATE,
                PredatorArmorItems.JUNGLE_PREDATOR_LEGGINGS,
                PredatorArmorItems.JUNGLE_PREDATOR_BOOTS
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

    private static void registerItemRenderers() {
        Services.CLIENT_REGISTRY.registerItemRenderer(PredatorBlockItems.TRIP_MINE_BLOCK, name -> TripMineItemRenderer::new);
    }
}
