package org.avp.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import org.avp.common.AVPResources;

public class AVPDamageTypeTags {

    public static final TagKey<DamageType> IS_BULLET_PROJECTILE = create("is_bullet_projectile");

    public static final TagKey<DamageType> IS_PUNCTURING = create("is_puncturing");

    private static TagKey<DamageType> create(String registryName) {
        return TagKey.create(Registries.DAMAGE_TYPE, AVPResources.location(registryName));
    }

    private AVPDamageTypeTags() {
        throw new UnsupportedOperationException();
    }
}
