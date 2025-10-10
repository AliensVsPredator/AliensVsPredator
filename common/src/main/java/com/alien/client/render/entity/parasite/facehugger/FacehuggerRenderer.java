package com.alien.client.render.entity.parasite.facehugger;

import com.alien.client.animation.entity.FacehuggerAnimator;
import com.alien.client.render.AlienRenderResourceCache;
import com.alien.common.gameplay.entity.living.alien.parasite.facehugger.Facehugger;
import com.alien.common.model.alien.variant.AlienVariant;
import mod.azure.azurelib.rewrite.render.AzLayerRenderer;
import mod.azure.azurelib.rewrite.render.AzModelRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererPipeline;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FacehuggerRenderer extends AzEntityRenderer<Facehugger> {

    private static final String NAME = "facehugger";

    private static final AlienRenderResourceCache RESOURCE_CACHE = new AlienRenderResourceCache(NAME, RenderType::entityCutoutNoCull);

    public FacehuggerRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder(FacehuggerRenderer::modelLocation, FacehuggerRenderer::textureLocation)
                .setRenderType(FacehuggerRenderer::renderType)
                .setAnimatorProvider(FacehuggerAnimator::new)
                .setDeathMaxRotation(0F)
                .setShadowRadius(0.25F)
                .build(),
            context
        );
    }

    @Override
    public AzEntityRendererPipeline<Facehugger> createPipeline(AzEntityRendererConfig<Facehugger> config) {
        return new AzEntityRendererPipeline<>(config, this) {

            @Override
            protected AzModelRenderer<Facehugger> createModelRenderer(AzLayerRenderer<Facehugger> layerRenderer) {
                return new FacehuggerModelRenderer(this, layerRenderer);
            }
        };
    }

    private static ResourceLocation modelLocation(Facehugger facehugger) {
        return RESOURCE_CACHE.getOrCreateModelLocationForVariant(AlienVariant.NORMAL, facehugger.isRoyal());
    }

    private static RenderType renderType(Facehugger facehugger) {
        return RESOURCE_CACHE.getOrCreateRenderTypeForVariant(facehugger.getVariant(), facehugger.isRoyal());
    }

    private static ResourceLocation textureLocation(Facehugger facehugger) {
        return RESOURCE_CACHE.getOrCreateTextureLocationForVariant(facehugger.getVariant(), facehugger.isRoyal());
    }
}
