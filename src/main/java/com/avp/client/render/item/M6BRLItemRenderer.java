package com.avp.client.render.item;

import com.avp.AVPResources;
import com.avp.client.animation.guns.M6BRLAnimator;
import com.avp.client.animation.guns.ZX76ShotgunAnimator;
import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

public class M6BRLItemRenderer extends AzItemRenderer {

    public M6BRLItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(M6BRLAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
