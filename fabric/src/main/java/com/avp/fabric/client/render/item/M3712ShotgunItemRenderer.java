package com.avp.fabric.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;

import com.avp.common.AVPResources;
import com.avp.fabric.client.animation.item.M3712ShotgunAnimator;
import com.avp.fabric.common.item.GunItem;

public class M3712ShotgunItemRenderer extends AzItemRenderer {

    public M3712ShotgunItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(M3712ShotgunAnimator::new)
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
