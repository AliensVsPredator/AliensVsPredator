package org.avp.common.service;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

import org.avp.api.Holder;

public interface ParticleTypeService {

    <T extends ParticleOptions> Holder<ParticleType<T>> createHolder(String registryName, Supplier<ParticleType<T>> supplier);

    void register(Holder<ParticleType<?>> holder);
}
