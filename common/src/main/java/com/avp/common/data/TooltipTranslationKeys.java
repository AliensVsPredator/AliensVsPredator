package com.avp.common.data;

import org.jetbrains.annotations.NotNull;

import com.avp.AVP;

public class TooltipTranslationKeys {

    public static final String EFFECT_FIRE_RESISTANCE = create("fire_resistance");

    public static final String EFFECT_JUMP_BOOST = create("jump_boost");

    public static final String EFFECT_PREVENTS_FACEHUGGING = create("prevents_facehugging");

    public static final String EFFECT_SLOWNESS = create("slowness");

    public static final String EFFECT_STRENGTH = create("strength");

    public static final String EFFECT_WATER_BREATHING = create("water_breathing");

    private static @NotNull String create(String name) {
        return "tooltip." + AVP.MOD_ID + "." + name;
    }
}
