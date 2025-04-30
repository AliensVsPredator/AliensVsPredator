package com.avp.fabric.client.render.block;

import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.fabric.AVPResources;
import com.avp.fabric.client.animation.entity.SentryTurretAnimator;
import com.avp.fabric.common.entity.machine.SentryTurret;

public class SentryTurretRenderer extends AzEntityRenderer<SentryTurret> {

    public static final String NAME = "sentry_turret";

    private static final ResourceLocation GEO = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEX = AVPResources.blockTextureLocation(NAME);

    public SentryTurretRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<SentryTurret>builder(GEO, TEX)
                .setAnimatorProvider(SentryTurretAnimator::new)
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .build(),
            context
        );
    }
}
