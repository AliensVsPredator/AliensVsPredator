package com.human.client.render.item.gun;

import com.human.client.animation.item.FlameThrowerAnimator;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.AVPResources;

public class FlamethrowerItemRenderer extends AzItemRenderer {

    public FlamethrowerItemRenderer(String name) {
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
