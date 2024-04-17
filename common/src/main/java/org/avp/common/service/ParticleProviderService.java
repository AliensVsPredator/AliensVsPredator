package org.avp.common.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.avp.api.Holder;
import org.avp.client.render.particle.ParticleProviderData;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ParticleProviderService {
    Holder<ParticleProviderData<ParticleOptions>> createHolder(String registryName, Supplier<ParticleProviderData<ParticleOptions>> supplier);

    void register(Supplier<ParticleType<ParticleOptions>> simpleParticleTypeSupplier, Function<SpriteSet, ParticleProvider<? extends ParticleOptions>> factoryProvider);

}
