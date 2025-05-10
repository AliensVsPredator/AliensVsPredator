package com.avp.client.render.item.gun.muzzled.impl;

import com.avp.client.animation.item.M6BRLAnimator;
import com.avp.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M6BRocketLauncherItemRenderer extends MuzzledGunItemRenderer {

    public M6BRocketLauncherItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M6BRLAnimator::new)
        );
    }
}
