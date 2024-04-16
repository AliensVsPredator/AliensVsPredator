package org.avp.fabric.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.avp.common.service.ParticleFactoryRegistry;

import java.util.function.Function;

public class FabricParticleFactoryRegistry implements ParticleFactoryRegistry {

    @Override
    public void register(
        SimpleParticleType simpleParticleType,
        Function<SpriteSet, ParticleProvider<SimpleParticleType>> factoryProvider
    ) {
        net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry.getInstance().register(
            simpleParticleType,
            factoryProvider::apply
        );
    }
}
