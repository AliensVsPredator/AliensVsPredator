package com.alien.client.render.entity;

import com.alien.client.animation.entity.SpitterAnimator;
import com.alien.common.gameplay.entity.living.alien.xenomorph.spitter.Spitter;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.client.render.layer.SpitGlandGlowLayer;

public class SpitterRenderer extends AzEntityRenderer<Spitter> {

    private static final String NAME = "spitter";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public SpitterRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, SpitterRenderer::textureLocation)
                .setAnimatorProvider(SpitterAnimator::new)
                .addRenderLayer(new SpitGlandGlowLayer<>())
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }

    public static ResourceLocation textureLocation(Spitter spitter) {
        if (spitter.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        if (spitter.isAberrant()) {
            return ABERRANT_TEXTURE;
        }

        return TEXTURE;
    }
}
