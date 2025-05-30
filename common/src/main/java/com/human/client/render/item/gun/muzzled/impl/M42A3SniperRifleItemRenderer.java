package com.human.client.render.item.gun.muzzled.impl;

import com.human.client.animation.item.M42A3SniperRifleAnimator;
import com.human.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M42A3SniperRifleItemRenderer extends MuzzledGunItemRenderer {

    public M42A3SniperRifleItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M42A3SniperRifleAnimator::new)
        );
    }
}
