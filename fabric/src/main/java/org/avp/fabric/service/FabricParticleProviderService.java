package org.avp.fabric.service;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.avp.api.Holder;
import org.avp.client.render.particle.ParticleProviderData;
import org.avp.common.service.ParticleProviderService;

import java.util.function.Function;
import java.util.function.Supplier;

public class FabricParticleProviderService implements ParticleProviderService {

    @Override
    public Holder<ParticleProviderData<ParticleOptions>> createHolder(String registryName, Supplier<ParticleProviderData<ParticleOptions>> supplier) {
        return new Holder<>(registryName, supplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(Supplier<ParticleType<ParticleOptions>> simpleParticleTypeSupplier, Function<SpriteSet, ParticleProvider<? extends ParticleOptions>> factoryProvider) {
        ParticleFactoryRegistry.getInstance().register(
            simpleParticleTypeSupplier.get(),
            spriteSet -> (ParticleProvider<ParticleOptions>) factoryProvider.apply(spriteSet)
        );
    }
}
