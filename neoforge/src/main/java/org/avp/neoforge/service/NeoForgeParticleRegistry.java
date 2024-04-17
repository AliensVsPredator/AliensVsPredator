package org.avp.neoforge.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.api.Holder;
import org.avp.common.AVPConstants;
import org.avp.common.service.ParticleRegistry;
import org.avp.common.service.Services;
import org.avp.neoforge.util.NeoForgeParticleTypeHolder;

import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeParticleRegistry implements ParticleRegistry {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, AVPConstants.MOD_ID);

    @Override
    public <T extends ParticleOptions> Holder<ParticleType<T>> register(String registryName, Function<SpriteSet, ParticleProvider<T>> factoryProvider) {
        var particleType = this.<T>simple();
        Supplier<ParticleType<T>> supplier = () -> particleType;
        Services.PARTICLE_FACTORY_REGISTRY.register(supplier, factoryProvider);
        return new NeoForgeParticleTypeHolder<>(PARTICLE_TYPES, registryName, supplier);
    }

    @SuppressWarnings("unchecked")
    private <T extends ParticleOptions> ParticleType<T> simple() {
        return (ParticleType<T>) new SimpleParticleType(false);
    }
}
