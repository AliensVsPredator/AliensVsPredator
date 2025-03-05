package com.avp.core.common.entity;

import net.minecraft.world.entity.MobCategory;

public class AVPMobCategories {

    static {
        // Ensure class is loaded before the category is accessed.
        MobCategory.values();
    }

    public static MobCategory ALIENS;
}
