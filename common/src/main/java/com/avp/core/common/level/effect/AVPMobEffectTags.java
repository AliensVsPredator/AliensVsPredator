package com.avp.core.common.level.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

import com.avp.core.AVPResources;

public class AVPMobEffectTags {

    public static final TagKey<MobEffect> DOES_NOT_AFFECT_ALIENS = create("does_not_affect_aliens");

    private static TagKey<MobEffect> create(String name) {
        return TagKey.create(Registries.MOB_EFFECT, AVPResources.location(name));
    }
}
