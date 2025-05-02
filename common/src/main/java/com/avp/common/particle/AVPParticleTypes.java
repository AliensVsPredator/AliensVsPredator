package com.avp.common.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPParticleTypes {

    public static final AVPDeferredHolder<SimpleParticleType> ACID = register("acid");

    public static final AVPDeferredHolder<SimpleParticleType> BLUE_ACID = register("blue_acid");

    public static final AVPDeferredHolder<SimpleParticleType> IRRADIATED_ACID = register("irradiated_acid");

    public static AVPDeferredHolder<SimpleParticleType> register(String id) {
        return Services.REGISTRY.register(BuiltInRegistries.PARTICLE_TYPE, id, () -> new SimpleParticleType(false) {});
    }

    public static void initialize() {}
}
