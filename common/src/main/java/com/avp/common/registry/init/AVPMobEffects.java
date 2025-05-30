package com.avp.common.registry.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

import com.avp.common.gameplay.effect.RadiationStatusEffect;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPMobEffects {

    public static final AVPDeferredHolder<MobEffect> RADIATION = register("radiation", RadiationStatusEffect::new);

    private static AVPDeferredHolder<MobEffect> register(String id, Supplier<MobEffect> mobEffectSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.MOB_EFFECT, id, mobEffectSupplier);
    }

    public static void initialize() {}
}
