package com.avp.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.AVPResources;
import com.avp.client.animation.item.FlameThrowerAnimator;

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
