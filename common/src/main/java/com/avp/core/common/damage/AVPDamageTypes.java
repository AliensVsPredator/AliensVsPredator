package com.avp.core.common.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import com.avp.core.AVPResources;

public class AVPDamageTypes {

    public static final ResourceKey<DamageType> ACID = create("acid");

    public static final ResourceKey<DamageType> BULLET = create("bullet");

    public static final ResourceKey<DamageType> FLAMETHROW = create("flamethrow");

    public static final ResourceKey<DamageType> RAZOR_WIRE = create("razor_wire");

    private static ResourceKey<DamageType> create(String id) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, AVPResources.location(id));
    }
}
