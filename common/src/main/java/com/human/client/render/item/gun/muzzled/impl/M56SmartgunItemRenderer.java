package com.human.client.render.item.gun.muzzled.impl;

import com.human.client.animation.item.M56SmartgunAnimator;
import com.human.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M56SmartgunItemRenderer extends MuzzledGunItemRenderer {

    public M56SmartgunItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M56SmartgunAnimator::new)
        );
    }
}
