package com.avp.common.data;

import org.jetbrains.annotations.NotNull;

import com.avp.AVP;

public class TooltipTranslationKeys {

    public static final String EFFECT_AUTO_EQUIP_ARMOR_SET = create("auto_equip_armor_set");

    public static final String EFFECT_AUTO_EQUIP_ARMOR_STAND_ARMOR_SET = create("auto_equip_armor_stand_armor_set");

    public static final String EFFECT_AUTO_STORE_IRRADIATED_ITEMS = create("auto_store_irradiated_items");

    public static final String EFFECT_FIRE_RESISTANCE = create("fire_resistance");

    public static final String EFFECT_GUNS_AUTO_RELOAD_FROM_CHEST = create("guns_auto_reload_from_chest");

    public static final String EFFECT_JUMP_BOOST = create("jump_boost");

    public static final String EFFECT_NEARBY_TURRETS_USE_AMMO_FROM_CHEST = create("nearby_turrets_use_ammo_from_chest");

    public static final String EFFECT_PREVENTS_FACEHUGGING = create("prevents_facehugging");

    public static final String EFFECT_RADIATION_RESISTANCE = create("radiation_resistance");

    public static final String EFFECT_SLOWNESS = create("slowness");

    public static final String EFFECT_STRENGTH = create("strength");

    public static final String EFFECT_WATER_BREATHING = create("water_breathing");

    public static final String REQUIRES_NEARBY_AMMO_CHEST_WITH_AMMO = create("requires_nearby_ammo_chest_with_ammo");

    public static final String REQUIRES_REDSTONE_POWER = create("requires_redstone_power");

    private static @NotNull String create(String name) {
        return "tooltip." + AVP.MOD_ID + "." + name;
    }
}
