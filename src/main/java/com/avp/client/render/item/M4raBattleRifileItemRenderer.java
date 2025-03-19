package com.avp.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.AVPResources;
import com.avp.client.animation.guns.M4raBattleRifileAnimator;

public class M4raBattleRifileItemRenderer extends AzItemRenderer {

    public M4raBattleRifileItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(M4raBattleRifileAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
