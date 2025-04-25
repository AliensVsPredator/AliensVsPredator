package com.avp.client.render.entity;

import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.client.animation.entity.QueenAnimator;
import com.avp.client.render.layer.RadiationGlowLayer;
import com.avp.common.entity.living.alien.xenomorph.queen.Queen;

public class QueenRenderer extends AzEntityRenderer<Queen> {

    private static final String NAME = "queen";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation IRRADIATED_TEXTURE = AVPResources.entityTextureLocation("irradiated_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public QueenRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, QueenRenderer::textureLocation)
                .setAnimatorProvider(QueenAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .build(),
            context
        );
        this.shadowRadius = 1F;
    }

    private static ResourceLocation textureLocation(Queen queen) {
        if (queen.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        if (queen.isIrradiated()) {
            return IRRADIATED_TEXTURE;
        }

        if (queen.isAberrant()) {
            return ABERRANT_TEXTURE;
        }

        return TEXTURE;
    }
}
