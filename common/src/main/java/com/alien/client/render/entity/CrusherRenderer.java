package com.alien.client.render.entity;

import com.alien.client.animation.entity.CrusherAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.xenomorph.crusher.Crusher;
import com.alien.common.model.alien.variant.AlienVariant;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.client.render.layer.RadiationGlowLayer;

public class CrusherRenderer extends AzEntityRenderer<Crusher> {

    private static final String NAME = "crusher";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public CrusherRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(CrusherRenderer::modelLocation, CrusherRenderer::textureLocation)
                .setRenderType(CrusherRenderer::renderType)
                .setAnimatorProvider(CrusherAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .setShadowRadius(0.5F)
                .build(),
            context
        );
    }

    public static ResourceLocation modelLocation(Crusher crusher) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(Crusher crusher) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(crusher.getVariant());
    }

    public static ResourceLocation textureLocation(Crusher crusher) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(crusher.getVariant());
    }
}
