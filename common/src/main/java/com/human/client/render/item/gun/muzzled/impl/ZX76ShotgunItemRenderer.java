package com.human.client.render.item.gun.muzzled.impl;

import com.human.client.animation.item.ZX76ShotgunAnimator;
import com.human.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class ZX76ShotgunItemRenderer extends MuzzledGunItemRenderer {

    public ZX76ShotgunItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(ZX76ShotgunAnimator::new)
        );
    }
}
