package com.avp.client.render.item;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;

import com.avp.AVPResources;
import com.avp.client.animation.guns.M88Mod4CombatPistolAnimator;

public class M88Mod4CombatPistolItemRenderer extends AzItemRenderer {

    public M88Mod4CombatPistolItemRenderer(String name) {
        super(
            AzItemRendererConfig.builder(
                AVPResources.itemGeoModelLocation(name),
                AVPResources.itemTextureLocation(name)
            )
                .setAnimatorProvider(M88Mod4CombatPistolAnimator::new)
                .useNewOffset(true)
                .build()
        );
    }
}
