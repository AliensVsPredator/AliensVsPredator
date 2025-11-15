package com.alien.client.render.entity;

import com.alien.client.animation.entity.PredalienAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.xenomorph.predalien.Predalien;
import com.alien.common.model.alien.variant.AlienVariant;
import mod.azure.azurelib.common.render.entity.AzEntityRenderer;
import mod.azure.azurelib.common.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.client.render.layer.RadiationGlowLayer;

public class PredalienRenderer extends AzEntityRenderer<Predalien> {

    private static final String NAME = "predalien";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public PredalienRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(PredalienRenderer::modelLocation, PredalienRenderer::textureLocation)
                .setRenderType(PredalienRenderer::renderType)
                .setAnimatorProvider(PredalienAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .setShadowRadius(0.5F)
                .build(),
            context
        );
    }

    private static ResourceLocation modelLocation(Predalien predalien) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(Predalien predalien) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(predalien.getVariant());
    }

    public static ResourceLocation textureLocation(Predalien predalien) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(predalien.getVariant());
    }
}
