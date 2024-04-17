package org.avp.fabric.service;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import org.avp.api.Holder;
import org.avp.common.service.ParticleTypeService;

import java.util.function.Supplier;

public class FabricParticleTypeService implements ParticleTypeService {

    @Override
    public <T extends ParticleOptions> Holder<ParticleType<T>> createHolder(String registryName, Supplier<ParticleType<T>> supplier) {
        return new Holder<>(registryName, this::simple);
    }

    @Override
    public void register(Holder<ParticleType<?>> holder) {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, holder.getResourceLocation(), holder.get());
    }

    @SuppressWarnings("unchecked")
    private <T extends ParticleOptions> ParticleType<T> simple() {
        return (ParticleType<T>) FabricParticleTypes.simple();
    }
}
