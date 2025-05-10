package com.avp.client.render.item.gun.muzzled.impl;

import com.avp.client.animation.item.M56SmartgunAnimator;
import com.avp.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M56SmartgunItemRenderer extends MuzzledGunItemRenderer {

    public M56SmartgunItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M56SmartgunAnimator::new)
        );
    }
}
