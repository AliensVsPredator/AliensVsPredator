package com.avp.fabric.client.render.entity;

import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.fabric.client.animation.entity.DroneAnimator;
import com.avp.fabric.client.render.layer.RadiationGlowLayer;
import com.avp.fabric.common.entity.living.alien.xenomorph.drone.Drone;

public class DroneRenderer extends AzEntityRenderer<Drone> {

    private static final String NAME = "drone";

    private static final ResourceLocation MODEL = AVPResources.entityGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.entityTextureLocation(NAME);

    private static final ResourceLocation ABERRANT_TEXTURE = AVPResources.entityTextureLocation("aberrant_" + NAME);

    private static final ResourceLocation IRRADIATED_TEXTURE = AVPResources.entityTextureLocation("irradiated_" + NAME);

    private static final ResourceLocation NETHER_TEXTURE = AVPResources.entityTextureLocation("nether_" + NAME);

    public DroneRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<Drone>builder($ -> MODEL, DroneRenderer::textureLocation)
                .setAnimatorProvider(DroneAnimator::new)
                .addRenderLayer(new RadiationGlowLayer<>())
                .build(),
            context
        );
        this.shadowRadius = 0.5F;
    }

    public static ResourceLocation textureLocation(Drone drone) {
        if (drone.isNetherAfflicted()) {
            return NETHER_TEXTURE;
        }

        if (drone.isIrradiated()) {
            return IRRADIATED_TEXTURE;
        }

        if (drone.isAberrant()) {
            return ABERRANT_TEXTURE;
        }

        return TEXTURE;
    }
}
