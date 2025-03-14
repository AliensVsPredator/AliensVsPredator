package com.avp.client.render.item;

import com.avp.AVPResources;
import com.avp.client.animation.guns.M56SmartgunAnimator;
import com.avp.client.animation.guns.M6BRLAnimator;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

public class M56SmartgunItemRenderer extends AzItemRenderer {

    public M56SmartgunItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(M56SmartgunAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
