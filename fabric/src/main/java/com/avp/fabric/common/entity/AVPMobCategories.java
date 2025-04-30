package com.avp.fabric.common.entity;

import net.minecraft.world.entity.MobCategory;

public record AVPMobCategories() {

    static {
        // Ensure class is loaded before the category is accessed.
        MobCategory.values();
    }

    public static MobCategory ALIENS;

    public static MobCategory PREDATOR;
}
