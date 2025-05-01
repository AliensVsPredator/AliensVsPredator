package com.avp.common.effect;

import com.bvanseg.just.functional.function.Lazy;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

import java.util.function.Supplier;

import com.avp.service.Services;

public class AVPEffects {

    private static final Supplier<MobEffect> RADIATION = register("radiation", RadiationStatusEffect::new);

    public static final Lazy<Holder<MobEffect>> RADIATION_HOLDER = Lazy.of(() -> Holder.direct(RADIATION.get()));

    private static Supplier<MobEffect> register(String id, Supplier<MobEffect> mobEffectSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.MOB_EFFECT, id, mobEffectSupplier);
    }

    public static void initialize() {}
}
