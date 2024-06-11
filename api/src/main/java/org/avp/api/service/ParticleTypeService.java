package org.avp.api.service;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

import org.avp.api.registry.holder.BLHolder;

public interface ParticleTypeService {

    <T extends ParticleOptions> BLHolder<ParticleType<T>> createHolder(String registryName, Supplier<ParticleType<T>> supplier);

    void register(BLHolder<ParticleType<?>> holder);
}
