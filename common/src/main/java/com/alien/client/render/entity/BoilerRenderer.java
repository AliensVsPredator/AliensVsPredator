package com.alien.client.render.entity;

import com.alien.client.animation.entity.BoilerAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.xenomorph.boiler.Boiler;
import com.alien.common.model.alien.variant.AlienVariant;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.client.render.layer.BoilGlowLayer;

public class BoilerRenderer extends AzEntityRenderer<Boiler> {

    private static final String NAME = "boiler";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public BoilerRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(BoilerRenderer::modelLocation, BoilerRenderer::textureLocation)
                .setRenderType(BoilerRenderer::renderType)
                .setAnimatorProvider(BoilerAnimator::new)
                .addRenderLayer(new BoilGlowLayer<>())
                .setShadowRadius(0.5F)
                .build(),
            context
        );
    }

    public static ResourceLocation modelLocation(Boiler boiler) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(Boiler boiler) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(boiler.getVariant());
    }

    public static ResourceLocation textureLocation(Boiler boiler) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(boiler.getVariant());
    }
}
