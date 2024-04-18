package org.avp.neoforge.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.client.render.particle.ParticleProviderData;
import org.avp.common.service.ParticleProviderService;

public class NeoForgeParticleProviderService implements ParticleProviderService {

    @Override
    public Holder<ParticleProviderData<ParticleOptions>> createHolder(
        String registryName,
        Supplier<ParticleProviderData<ParticleOptions>> supplier
    ) {
        return new Holder<>(registryName, supplier);
    }

    @Override
    public void register(
        Supplier<ParticleType<ParticleOptions>> simpleParticleTypeSupplier,
        Function<SpriteSet, ParticleProvider<? extends ParticleOptions>> factoryProvider
    ) {
        // NO-OP IN FORGE
    }
}
