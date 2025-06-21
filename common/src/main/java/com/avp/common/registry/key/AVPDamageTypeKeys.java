package com.avp.common.registry.key;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import com.avp.AVPResources;

public class AVPDamageTypeKeys {

    public static final ResourceKey<DamageType> ACID = create("acid");

    public static final ResourceKey<DamageType> BULLET = create("bullet");

    public static final ResourceKey<DamageType> CHESTBURSTING = create("chestbursting");

    public static final ResourceKey<DamageType> FLAMETHROW = create("flamethrow");

    public static final ResourceKey<DamageType> RAZOR_WIRE = create("razor_wire");

    public static final ResourceKey<DamageType> RADIATION = create("radiation");

    public static final ResourceKey<DamageType> SMOTHERING = create("smothering");

    private static ResourceKey<DamageType> create(String id) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, AVPResources.location(id));
    }
}
