package com.avp.common.registry.tag;

import com.avp.AVPResources;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class AVPDamageTypesTags {

    public static final TagKey<DamageType> DOES_NOT_HURT_ALIENS = create("does_not_hurt_aliens");

    private static TagKey<DamageType> create(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, AVPResources.location(name));
    }
}
