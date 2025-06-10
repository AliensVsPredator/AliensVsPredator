package com.human.client.render.item.gun.muzzled.impl;

import com.human.client.animation.item.F903weAnimator;
import com.human.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class F903WEItemRenderer extends MuzzledGunItemRenderer {

    public F903WEItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(F903weAnimator::new)
        );
    }
}
