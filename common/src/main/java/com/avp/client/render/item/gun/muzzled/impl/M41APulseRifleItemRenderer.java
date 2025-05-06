package com.avp.client.render.item.gun.muzzled.impl;

import com.avp.client.animation.item.M41APulseRifleAnimator;
import com.avp.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M41APulseRifleItemRenderer extends MuzzledGunItemRenderer {

    public M41APulseRifleItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M41APulseRifleAnimator::new)
        );
    }
}
