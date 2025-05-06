package com.avp.client.render.item.gun.muzzled.impl;

import com.avp.client.animation.item.M3712ShotgunAnimator;
import com.avp.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M3712ShotgunItemRenderer extends MuzzledGunItemRenderer {

    public M3712ShotgunItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M3712ShotgunAnimator::new)
        );
    }
}
