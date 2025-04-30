package com.avp.fabric.common.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import com.avp.fabric.AVPResources;

public class AVPEffects {

    public static final Holder<MobEffect> RADIATION_EFFECT = create("radiation", new RadiationStatusEffect());

    private static Holder<MobEffect> create(String id, MobEffect mobEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, AVPResources.location(id), mobEffect);
    }

    public static void initialize() {}
}
