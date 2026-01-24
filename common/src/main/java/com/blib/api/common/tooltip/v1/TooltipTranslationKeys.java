package com.blib.api.common.tooltip.v1;

import org.jetbrains.annotations.NotNull;

import com.blib.mod.BLib;

public class TooltipTranslationKeys {

    public static final String EFFECT_FIRE_RESISTANCE = create("fire_resistance");

    public static final String EFFECT_JUMP_BOOST = create("jump_boost");

    public static final String EFFECT_SLOWNESS = create("slowness");

    public static final String EFFECT_STRENGTH = create("strength");

    public static final String EFFECT_WATER_BREATHING = create("water_breathing");

    private static @NotNull String create(String name) {
        return "tooltip." + BLib.MOD.id() + "." + name;
    }
}
