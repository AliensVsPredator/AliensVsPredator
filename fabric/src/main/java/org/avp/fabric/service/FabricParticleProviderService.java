package org.avp.fabric.service;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;
import java.util.function.Supplier;

import org.avp.api.client.ParticleProviderData;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.ParticleProviderService;
import org.avp.common.registry.holder.AVPHolder;

public class FabricParticleProviderService implements ParticleProviderService {

    @Override
    public BLHolder<ParticleProviderData<ParticleOptions>> createHolder(
        String registryName,
        Supplier<ParticleProviderData<ParticleOptions>> supplier
    ) {
        return new AVPHolder<>(registryName, supplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(
        Supplier<ParticleType<ParticleOptions>> simpleParticleTypeSupplier,
        Function<SpriteSet, ParticleProvider<? extends ParticleOptions>> factoryProvider
    ) {
        ParticleFactoryRegistry.getInstance()
            .register(
                simpleParticleTypeSupplier.get(),
                spriteSet -> (ParticleProvider<ParticleOptions>) factoryProvider.apply(spriteSet)
            );
    }
}
