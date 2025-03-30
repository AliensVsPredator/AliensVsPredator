package com.avp.client.render.block;

import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.rewrite.render.block.AzBlockEntityRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import com.avp.client.animation.block.SentryTurretAnimator;
import com.avp.common.block.entity.SentryTurretBE;

public class SentryTurretRenderer extends AzBlockEntityRenderer<SentryTurretBE> {

    public static final String NAME = "sentry_turret";

    private static final ResourceLocation GEO = AVPResources.blockGeoModelLocation(NAME);

    private static final ResourceLocation TEX = AVPResources.blockTextureLocation(NAME);

    public SentryTurretRenderer() {
        super(
            AzBlockEntityRendererConfig.<SentryTurretBE>builder(GEO, TEX)
                .setAnimatorProvider(SentryTurretAnimator::new)
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .build()
        );
    }
}
