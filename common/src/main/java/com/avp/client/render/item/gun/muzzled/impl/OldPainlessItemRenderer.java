package com.avp.client.render.item.gun.muzzled.impl;

import java.util.List;

import com.avp.client.animation.item.OldPainlessAnimator;
import com.avp.client.render.item.gun.muzzled.MuzzledGunItemRenderer;

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
