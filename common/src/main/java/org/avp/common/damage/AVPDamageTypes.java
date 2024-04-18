package org.avp.common.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import org.avp.common.AVPResources;

public class AVPDamageTypes {

    public static final AVPDamageTypes INSTANCE = new AVPDamageTypes();

    public final ResourceKey<DamageType> acid = create("acid");

    private AVPDamageTypes() {}

    public ResourceKey<DamageType> create(String registryName) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, AVPResources.location(registryName));
    }
}
