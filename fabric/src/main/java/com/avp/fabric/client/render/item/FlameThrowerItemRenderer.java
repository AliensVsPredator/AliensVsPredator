package com.avp.fabric.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.fabric.AVPResources;
import com.avp.fabric.client.animation.item.FlameThrowerAnimator;

public class FlameThrowerItemRenderer extends AzItemRenderer {

    public FlameThrowerItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(FlameThrowerAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
