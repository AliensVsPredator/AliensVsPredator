package com.alien.client;

import com.alien.client.render.armor.AberrantChitinArmorRenderer;
import com.alien.client.render.armor.ChitinArmorRenderer;
import com.alien.client.render.armor.IrradiatedChitinArmorRenderer;
import com.alien.client.render.armor.NetherChitinArmorRenderer;
import com.alien.client.render.armor.PlatedAberrantChitinArmorRenderer;
import com.alien.client.render.armor.PlatedChitinArmorRenderer;
import com.alien.client.render.armor.PlatedIrradiatedChitinArmorRenderer;
import com.alien.client.render.armor.PlatedNetherChitinArmorRenderer;
import com.alien.common.registry.init.AlienArmorItems;
import com.alien.common.registry.init.AlienBlocks;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

import com.avp.service.Services;

public class AlienClient {

    public static void initialize() {
        registerArmorRenderers();
        registerBlockRenderLayers();
    }

    private static void registerArmorRenderers() {
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            AberrantChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.ABERRANT_CHITIN_HELMET,
                AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE,
                AlienArmorItems.ABERRANT_CHITIN_LEGGINGS,
                AlienArmorItems.ABERRANT_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            ChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.CHITIN_HELMET,
                AlienArmorItems.CHITIN_CHESTPLATE,
                AlienArmorItems.CHITIN_LEGGINGS,
                AlienArmorItems.CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            IrradiatedChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.IRRADIATED_CHITIN_HELMET,
                AlienArmorItems.IRRADIATED_CHITIN_CHESTPLATE,
                AlienArmorItems.IRRADIATED_CHITIN_LEGGINGS,
                AlienArmorItems.IRRADIATED_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            NetherChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.NETHER_CHITIN_HELMET,
                AlienArmorItems.NETHER_CHITIN_CHESTPLATE,
                AlienArmorItems.NETHER_CHITIN_LEGGINGS,
                AlienArmorItems.NETHER_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedAberrantChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.PLATED_ABERRANT_CHITIN_HELMET,
                AlienArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE,
                AlienArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS,
                AlienArmorItems.PLATED_ABERRANT_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.PLATED_CHITIN_HELMET,
                AlienArmorItems.PLATED_CHITIN_CHESTPLATE,
                AlienArmorItems.PLATED_CHITIN_LEGGINGS,
                AlienArmorItems.PLATED_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedIrradiatedChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_HELMET,
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE,
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS,
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS
            )
        );
        Services.CLIENT_REGISTRY.registerArmorRenderer(
            PlatedNetherChitinArmorRenderer::new,
            List.of(
                AlienArmorItems.PLATED_NETHER_CHITIN_HELMET,
                AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
                AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS,
                AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS
            )
        );
    }

    private static void registerBlockRenderLayers() {
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.IRRADIATED_RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.IRRADIATED_RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.ABERRANT_RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.ABERRANT_RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.NETHER_RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.NETHER_RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.RESIN_VEIN, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.RESIN_WEB, RenderType.cutout());
        Services.CLIENT_REGISTRY.registerBlockRenderLayer(AlienBlocks.ROYAL_JELLY_BLOCK, RenderType.translucent());
    }
}
