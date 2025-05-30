package com.human.client.render.entity;

import com.human.client.animation.entity.SentryTurretAnimator;
import com.human.common.gameplay.entity.machine.SentryTurret;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRenderer;
import mod.azure.azurelib.rewrite.render.entity.AzEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;

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
