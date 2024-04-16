package org.avp.fabric.service;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.avp.api.GameObject;
import org.avp.common.AVPResources;
import org.avp.common.service.ParticleRegistry;
import org.avp.common.service.Services;

import java.util.function.Function;

public class FabricParticleRegistry implements ParticleRegistry {
    @Override
    public GameObject<SimpleParticleType> register(String registryName, Function<SpriteSet, ParticleProvider<SimpleParticleType>> factoryProvider) {
        var particleType = FabricParticleTypes.simple();
        var resourceLocation = AVPResources.location(registryName);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, resourceLocation, particleType);
        Services.PARTICLE_FACTORY_REGISTRY.register(particleType, factoryProvider);
        return new GameObject<>(registryName, () -> particleType);
    }
}
