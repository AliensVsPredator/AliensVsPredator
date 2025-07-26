package com.alien.client.render.entity;

import com.alien.client.animation.entity.CrusherAnimator;
import com.alien.common.gameplay.entity.living.alien.xenomorph.crusher.Crusher;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.client.render.layer.RadiationGlowLayer;

public class CrusherRenderer extends AzEntityRenderer<Crusher> {

    private static final String NAME = "crusher";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation IRRADIATED_TEXTURE = AVPResources.entityTextureLocation("irradiated_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public CrusherRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, CrusherRenderer::textureLocation)
                .setAnimatorProvider(CrusherAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }

    public static ResourceLocation textureLocation(Crusher crusher) {
        if (crusher.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        if (crusher.isIrradiated()) {
            return IRRADIATED_TEXTURE;
        }

        if (crusher.isAberrant()) {
            return ABERRANT_TEXTURE;
        }

        return TEXTURE;
    }
}
