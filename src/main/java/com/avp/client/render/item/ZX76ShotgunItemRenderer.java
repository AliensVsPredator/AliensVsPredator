package com.avp.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.AVPResources;
import com.avp.client.animation.guns.ZX76ShotgunAnimator;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;

public class ZX76ShotgunItemRenderer extends AzItemRenderer {

    public ZX76ShotgunItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(ZX76ShotgunAnimator::new)
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .useNewOffset(true)
                .build()
        );
    }
}
