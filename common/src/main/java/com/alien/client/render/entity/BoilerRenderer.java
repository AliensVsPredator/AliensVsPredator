package com.alien.client.render.entity;

import com.alien.client.animation.entity.BoilerAnimator;
import com.alien.common.gameplay.entity.living.alien.xenomorph.boiler.Boiler;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.client.render.layer.RadiationGlowLayer;

public class BoilerRenderer extends AzEntityRenderer<Boiler> {

    private static final String NAME = "boiler";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public BoilerRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.builder($ -> MODEL, BoilerRenderer::textureLocation)
                .setAnimatorProvider(BoilerAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }

    public static ResourceLocation textureLocation(Boiler boiler) {
        if (boiler.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        if (boiler.isAberrant()) {
            return ABERRANT_TEXTURE;
        }

        return TEXTURE;
    }
}
