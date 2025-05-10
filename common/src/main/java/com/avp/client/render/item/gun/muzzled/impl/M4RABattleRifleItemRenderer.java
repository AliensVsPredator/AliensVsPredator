package com.avp.client.render.item.gun.muzzled.impl;

import com.avp.client.animation.item.M4raBattleRifileAnimator;
import com.avp.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M4RABattleRifleItemRenderer extends MuzzledGunItemRenderer {

    public M4RABattleRifleItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M4raBattleRifileAnimator::new)
        );
    }
}
