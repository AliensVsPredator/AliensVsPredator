package com.avp.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;

import com.avp.AVPResources;
import com.avp.client.animation.item.F903weAnimator;
import com.avp.common.item.GunItem;

public class F903weItemRenderer extends AzItemRenderer {

    public F903weItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(F903weAnimator::new)
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .useNewOffset(true)
                .setPrerenderEntry(context -> {
                    if (context.bakedModel().getBone("gFlash").isPresent() && context.animatable().getItem() instanceof GunItem gunItem) {
                        context.bakedModel().getBone("gFlash").get().setHidden(!gunItem.isFiring);
                    }
                    return context;
                })
                .build()
        );
    }
}
