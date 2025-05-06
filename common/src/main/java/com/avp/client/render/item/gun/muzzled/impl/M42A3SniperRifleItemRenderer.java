package com.avp.client.render.item.gun.muzzled.impl;

import com.avp.client.animation.item.M42A3SniperRifleAnimator;
import com.avp.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M42A3SniperRifleItemRenderer extends MuzzledGunItemRenderer {

    public M42A3SniperRifleItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M42A3SniperRifleAnimator::new)
        );
    }
}
