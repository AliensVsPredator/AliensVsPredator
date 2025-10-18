package com.alien.client.render.entity;

import com.alien.client.animation.entity.SpitterAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.xenomorph.spitter.Spitter;
import com.alien.common.model.alien.variant.AlienVariant;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.client.render.layer.SpitGlandGlowLayer;

public class SpitterRenderer extends AzEntityRenderer<Spitter> {

    private static final String NAME = "spitter";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public SpitterRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(SpitterRenderer::modelLocation, SpitterRenderer::textureLocation)
                .setRenderType(SpitterRenderer::renderType)
                .setAnimatorProvider(SpitterAnimator::new)
                .addRenderLayer(new SpitGlandGlowLayer<>())
                .setShadowRadius(0.5F)
                .build(),
            context
        );
    }

    private static ResourceLocation modelLocation(Spitter spitter) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(Spitter spitter) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(spitter.getVariant());
    }

    public static ResourceLocation textureLocation(Spitter spitter) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(spitter.getVariant());
    }
}
