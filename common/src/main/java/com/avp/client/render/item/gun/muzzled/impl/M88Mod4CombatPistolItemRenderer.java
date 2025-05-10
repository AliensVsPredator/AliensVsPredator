package com.avp.client.render.item.gun.muzzled.impl;

import com.avp.client.animation.item.M88Mod4CombatPistolAnimator;
import com.avp.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

public class M88Mod4CombatPistolItemRenderer extends MuzzledGunItemRenderer {

    public M88Mod4CombatPistolItemRenderer(String name) {
        super(
            name,
            config -> config
                .setAnimatorProvider(M88Mod4CombatPistolAnimator::new)
        );
    }
}
