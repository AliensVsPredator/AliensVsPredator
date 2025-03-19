package com.avp.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.AVPResources;
import com.avp.client.animation.guns.M41APulseRifleAnimator;

public class M41APulseRifleItemRenderer extends AzItemRenderer {

    public M41APulseRifleItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(M41APulseRifleAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
