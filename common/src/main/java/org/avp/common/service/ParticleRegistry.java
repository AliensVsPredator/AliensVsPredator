package org.avp.common.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.avp.api.GameObject;

import java.util.function.Function;

public interface ParticleRegistry {

    GameObject<SimpleParticleType> register(String registryName, Function<SpriteSet, ParticleProvider<SimpleParticleType>> factoryProvider);
}
