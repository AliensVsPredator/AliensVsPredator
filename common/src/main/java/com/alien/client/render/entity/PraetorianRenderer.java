package com.alien.client.render.entity;

import com.alien.client.animation.entity.PraetorianAnimator;
import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.Praetorian;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.client.render.layer.RadiationGlowLayer;

public class PraetorianRenderer extends AzEntityRenderer<Praetorian> {

    private static final String NAME = "praetorian";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation IRRADIATED_TEXTURE = AVPResources.entityTextureLocation("irradiated_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public PraetorianRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, PraetorianRenderer::textureLocation)
                .setAnimatorProvider(PraetorianAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }

    public static ResourceLocation textureLocation(Praetorian praetorian) {
        if (praetorian.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        if (praetorian.isIrradiated()) {
            return IRRADIATED_TEXTURE;
        }

        if (praetorian.isAberrant()) {
            return ABERRANT_TEXTURE;
        }

        return TEXTURE;
    }
}
