package com.human.client.render.item.gun.muzzled.impl;

import com.human.client.animation.item.M4raBattleRifileAnimator;
import com.human.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M4RABattleRifleItemRenderer extends MuzzledGunItemRenderer {

    public M4RABattleRifleItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M4raBattleRifileAnimator::new)
        );
    }
}
