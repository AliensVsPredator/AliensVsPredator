package com.avp.fabric.common.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import com.avp.AVPResources;

public class AVPDamageTypesTags {

    public static final TagKey<DamageType> DOES_NOT_HURT_SENTRY_TURRETS = create("does_not_hurt_sentry_turrets");

    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, AVPResources.location(name));
    }
}
