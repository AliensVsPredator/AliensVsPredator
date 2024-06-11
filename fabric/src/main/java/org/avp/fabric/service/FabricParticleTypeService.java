package org.avp.fabric.service;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.service.ParticleTypeService;
import org.avp.common.registry.holder.AVPHolder;

public class FabricParticleTypeService implements ParticleTypeService {

    @Override
    public <T extends ParticleOptions> BLHolder<ParticleType<T>> createHolder(String registryName, Supplier<ParticleType<T>> supplier) {
        return new AVPHolder<>(registryName, this::simple);
    }

    @Override
    public void register(BLHolder<ParticleType<?>> holder) {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, holder.getResourceLocation(), holder.get());
    }

    @SuppressWarnings("unchecked")
    private <T extends ParticleOptions> ParticleType<T> simple() {
        return (ParticleType<T>) FabricParticleTypes.simple();
    }
}
