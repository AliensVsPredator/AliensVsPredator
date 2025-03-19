package com.avp.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.AVPResources;
import com.avp.client.animation.guns.M42A3SniperRifleAnimator;

public class M42a3SniperRifleItemRenderer extends AzItemRenderer {

    public M42a3SniperRifleItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(M42A3SniperRifleAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
