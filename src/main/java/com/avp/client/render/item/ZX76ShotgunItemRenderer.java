package com.avp.client.render.item;

import com.avp.AVPResources;
import com.avp.client.animation.guns.M3712ShotgunAnimator;
import com.avp.client.animation.guns.ZX76ShotgunAnimator;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

public class ZX76ShotgunItemRenderer extends AzItemRenderer {

    public ZX76ShotgunItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(ZX76ShotgunAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
