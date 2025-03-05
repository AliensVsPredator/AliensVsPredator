package com.avp.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.AVPResources;
import com.avp.client.animation.OldPainlessAnimator;

public class OldPainlessItemRenderer extends AzItemRenderer {

    public OldPainlessItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(OldPainlessAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
