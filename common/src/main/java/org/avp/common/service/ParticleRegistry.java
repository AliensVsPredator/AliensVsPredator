package org.avp.common.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.avp.api.Holder;

import java.util.function.Function;

public interface ParticleRegistry {

    <T extends ParticleOptions> Holder<ParticleType<T>> register(String registryName, Function<SpriteSet, ParticleProvider<T>> factoryProvider);
}
