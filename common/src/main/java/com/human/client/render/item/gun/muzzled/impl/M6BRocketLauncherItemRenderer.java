package com.human.client.render.item.gun.muzzled.impl;

import com.human.client.animation.item.M6BRLAnimator;
import com.human.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M6BRocketLauncherItemRenderer extends MuzzledGunItemRenderer {

    public M6BRocketLauncherItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M6BRLAnimator::new)
        );
    }
}
