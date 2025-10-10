package com.alien.client.render.entity;

import com.alien.client.animation.entity.PraetorianAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.alien.common.model.alien.variant.AlienVariant;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.client.render.layer.RadiationGlowLayer;

public class PraetorianRenderer extends AzEntityRenderer<Praetorian> {

    private static final String NAME = "praetorian";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public PraetorianRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(PraetorianRenderer::modelLocation, PraetorianRenderer::textureLocation)
                .setRenderType(PraetorianRenderer::renderType)
                .setAnimatorProvider(PraetorianAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .setShadowRadius(0.5F)
                .build(),
            context
        );
    }

    private static ResourceLocation modelLocation(Praetorian praetorian) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(Praetorian praetorian) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(praetorian.getVariant());
    }

    public static ResourceLocation textureLocation(Praetorian praetorian) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(praetorian.getVariant());
    }
}
