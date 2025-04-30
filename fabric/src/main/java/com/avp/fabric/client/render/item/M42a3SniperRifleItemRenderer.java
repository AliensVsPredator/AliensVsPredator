package com.avp.fabric.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;

import com.avp.common.AVPResources;
import com.avp.fabric.client.animation.item.M42A3SniperRifleAnimator;
import com.avp.fabric.common.item.GunItem;

public class M42a3SniperRifleItemRenderer extends AzItemRenderer {

    public M42a3SniperRifleItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(M42A3SniperRifleAnimator::new)
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
