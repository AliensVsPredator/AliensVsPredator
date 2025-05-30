package com.human.client.render.item.gun.muzzled.impl;

import com.human.client.animation.item.M3712ShotgunAnimator;
import com.human.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M3712ShotgunItemRenderer extends MuzzledGunItemRenderer {

    public M3712ShotgunItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M3712ShotgunAnimator::new)
        );
    }
}
