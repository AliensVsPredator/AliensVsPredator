package org.avp.fabric.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.avp.common.service.ParticleFactoryRegistry;

import java.util.function.Function;
import java.util.function.Supplier;

public class FabricParticleFactoryRegistry implements ParticleFactoryRegistry {

    @Override
    public <T extends ParticleOptions> void register(Supplier<ParticleType<T>> simpleParticleTypeSupplier, Function<SpriteSet, ParticleProvider<T>> factoryProvider) {
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(
            simpleParticleTypeSupplier.get(),
            factoryProvider::apply
        );
    }
}
