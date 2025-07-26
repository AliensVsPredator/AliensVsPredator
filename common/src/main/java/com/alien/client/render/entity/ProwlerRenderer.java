package com.alien.client.render.entity;

import com.alien.client.animation.entity.ProwlerAnimator;
import com.alien.common.gameplay.entity.living.alien.xenomorph.prowler.Prowler;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.client.render.layer.RadiationGlowLayer;

public class ProwlerRenderer extends AzEntityRenderer<Prowler> {

    private static final String NAME = "prowler";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation IRRADIATED_TEXTURE = AVPResources.entityTextureLocation("irradiated_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public ProwlerRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, ProwlerRenderer::textureLocation)
                .setAnimatorProvider(ProwlerAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }

    public static ResourceLocation textureLocation(Prowler prowler) {
        if (prowler.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        if (prowler.isIrradiated()) {
            return IRRADIATED_TEXTURE;
        }

        if (prowler.isAberrant()) {
            return ABERRANT_TEXTURE;
        }

        return TEXTURE;
    }
}
