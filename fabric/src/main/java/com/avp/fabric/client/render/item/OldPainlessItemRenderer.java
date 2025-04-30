package com.avp.fabric.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;

import com.avp.AVPResources;
import com.avp.fabric.client.animation.item.OldPainlessAnimator;
import com.avp.fabric.common.item.GunItem;

public class OldPainlessItemRenderer extends AzItemRenderer {

    public OldPainlessItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(OldPainlessAnimator::new)
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .useNewOffset(true)
                .setPrerenderEntry(context -> {
                    if (context.bakedModel().getBone("gFlash").isPresent() && context.animatable().getItem() instanceof GunItem gunItem) {
                        context.bakedModel().getBone("gFlash").get().setHidden(!gunItem.isFiring);
                    }
                    if (context.bakedModel().getBone("gFlash2").isPresent() && context.animatable().getItem() instanceof GunItem gunItem) {
                        context.bakedModel().getBone("gFlash2").get().setHidden(!gunItem.isFiring);
                    }
                    if (context.bakedModel().getBone("gFlash3").isPresent() && context.animatable().getItem() instanceof GunItem gunItem) {
                        context.bakedModel().getBone("gFlash3").get().setHidden(!gunItem.isFiring);
                    }
                    if (context.bakedModel().getBone("gFlash4").isPresent() && context.animatable().getItem() instanceof GunItem gunItem) {
                        context.bakedModel().getBone("gFlash4").get().setHidden(!gunItem.isFiring);
                    }
                    if (context.bakedModel().getBone("gFlash5").isPresent() && context.animatable().getItem() instanceof GunItem gunItem) {
                        context.bakedModel().getBone("gFlash5").get().setHidden(!gunItem.isFiring);
                    }
                    if (context.bakedModel().getBone("gFlash6").isPresent() && context.animatable().getItem() instanceof GunItem gunItem) {
                        context.bakedModel().getBone("gFlash6").get().setHidden(!gunItem.isFiring);
                    }
                    return context;
                })
                .build()
        );
    }
}
