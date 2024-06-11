package org.avp.neoforge.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.avp.api.client.ParticleProviderData;
import org.avp.api.registry.holder.BLHolder;
import org.avp.api.service.ParticleProviderService;
import org.avp.common.registry.holder.AVPHolder;

import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeParticleProviderService implements ParticleProviderService {

    @Override
    public BLHolder<ParticleProviderData<ParticleOptions>> createHolder(
        String registryName,
        Supplier<ParticleProviderData<ParticleOptions>> supplier
    ) {
        return new AVPHolder<>(registryName, supplier);
    }

    @Override
    public void register(
        Supplier<ParticleType<ParticleOptions>> simpleParticleTypeSupplier,
        Function<SpriteSet, ParticleProvider<? extends ParticleOptions>> factoryProvider
    ) {
        // NO-OP IN FORGE
    }
}
