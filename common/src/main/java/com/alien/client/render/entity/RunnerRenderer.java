package com.alien.client.render.entity;

import com.alien.client.animation.entity.RunnerAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.xenomorph.runner.Runner;
import com.alien.common.model.alien.variant.AlienVariant;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.client.render.layer.RadiationGlowLayer;

public class RunnerRenderer extends AzEntityRenderer<Runner> {

    private static final String NAME = "runner";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public RunnerRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(RunnerRenderer::modelLocation, RunnerRenderer::textureLocation)
                .setRenderType(RunnerRenderer::renderType)
                .setAnimatorProvider(RunnerAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .setShadowRadius(0.5F)
                .build(),
            context
        );
    }

    private static ResourceLocation modelLocation(Runner runner) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL);
    }

    private static RenderType renderType(Runner runner) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(runner.getVariant());
    }

    public static ResourceLocation textureLocation(Runner runner) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(runner.getVariant());
    }
}
