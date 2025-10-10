package com.alien.client.render.entity;

import com.alien.client.animation.entity.ProwlerAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.xenomorph.prowler.Prowler;
import com.alien.common.model.alien.variant.AlienVariant;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.client.render.layer.RadiationGlowLayer;

public class ProwlerRenderer extends AzEntityRenderer<Prowler> {

    private static final String NAME = "prowler";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public ProwlerRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(ProwlerRenderer::modelLocation, ProwlerRenderer::textureLocation)
                .setRenderType(ProwlerRenderer::renderType)
                .setAnimatorProvider(ProwlerAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .setShadowRadius(0.5F)
                .build(),
            context
        );
    }

    private static ResourceLocation modelLocation(Prowler prowler) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(Prowler prowler) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(prowler.getVariant());
    }

    public static ResourceLocation textureLocation(Prowler prowler) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(prowler.getVariant());
    }
}
