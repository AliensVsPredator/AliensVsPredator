package com.human.client.render.item.gun.muzzled.impl;

import com.human.client.animation.item.M41APulseRifleAnimator;
import com.human.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M41APulseRifleItemRenderer extends MuzzledGunItemRenderer {

    public M41APulseRifleItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M41APulseRifleAnimator::new)
        );
    }
}
