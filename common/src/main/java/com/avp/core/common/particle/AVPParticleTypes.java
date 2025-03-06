package com.avp.core.common.particle;

import com.avp.core.platform.service.Services;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;

public class AVPParticleTypes {

    public static final Supplier<SimpleParticleType> ACID = register("acid");

    public static final Supplier<SimpleParticleType> BLUE_ACID = register("blue_acid");

    public static Supplier<SimpleParticleType> register(String id) {
        return Services.REGISTRY.registerParticle(id, () -> new SimpleParticleType(false) {});
    }

    public static void initialize() {}
}
