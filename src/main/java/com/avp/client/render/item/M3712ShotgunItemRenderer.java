package com.avp.client.render.item;

import com.avp.AVPResources;
import com.avp.client.animation.guns.F903weAnimator;
import com.avp.client.animation.guns.M3712ShotgunAnimator;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

public class M3712ShotgunItemRenderer extends AzItemRenderer {

    public M3712ShotgunItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(M3712ShotgunAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
