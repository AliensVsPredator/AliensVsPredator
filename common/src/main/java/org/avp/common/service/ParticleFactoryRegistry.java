package org.avp.common.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ParticleFactoryRegistry {

    <T extends ParticleOptions> void register(
        Supplier<ParticleType<T>> simpleParticleTypeSupplier,
        Function<SpriteSet, ParticleProvider<T>> factoryProvider
    );
}
