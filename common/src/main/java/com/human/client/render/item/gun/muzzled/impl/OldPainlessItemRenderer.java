package com.human.client.render.item.gun.muzzled.impl;

import com.human.client.animation.item.OldPainlessAnimator;
import com.human.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

import java.util.List;

public class OldPainlessItemRenderer extends MuzzledGunItemRenderer {

    private static final List<String> MUZZLE_FLASH_BONE_NAME_LIST = List.of(
        "gFlash",
        "gFlash2",
        "gFlash3",
        "gFlash4",
        "gFlash5",
        "gFlash6"
    );

    public OldPainlessItemRenderer(String name) {
        super(
            name,
            MUZZLE_FLASH_BONE_NAME_LIST,
            config -> config
                .setAnimatorProvider(OldPainlessAnimator::new)
        );
    }
}
