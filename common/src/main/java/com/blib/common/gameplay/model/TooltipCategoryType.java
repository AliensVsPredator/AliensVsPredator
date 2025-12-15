package com.blib.common.gameplay.model;

import com.blib.BLib;

public enum TooltipCategoryType {

    REQUIREMENTS("requirements"),
    WHEN_FULL_ARMOR_SET_EQUIPPED("when_full_armor_set_equipped"),
    WHEN_HELMET_EQUIPPED("when_helmet_equipped"),
    WHEN_IN_INVENTORY("when_in_inventory"),
    WHEN_PLACED_IN_WORLD("full_set_bonus"),
    WHEN_USED("when_used"),
    WHEN_USED_ON_ARMOR_STAND("when_used_on_armor_stand");

    private final String translationKey;

    TooltipCategoryType(String translationKey) {
        // FIXME:
        this.translationKey = "tooltip." + BLib.MOD.id() + "." + translationKey;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
