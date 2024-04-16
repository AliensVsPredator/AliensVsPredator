package org.avp.common.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Function;

public interface ParticleFactoryRegistry {

    void register(
        SimpleParticleType simpleParticleType,
        Function<SpriteSet, ParticleProvider<SimpleParticleType>> factoryProvider
    );
}
