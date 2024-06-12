package org.avp.neoforge.service;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.AVPConstants;
import org.avp.api.service.ParticleTypeService;
import org.avp.neoforge.util.NeoForgeParticleTypeHolder;

public class NeoForgeParticleTypeService implements ParticleTypeService {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
        BuiltInRegistries.PARTICLE_TYPE,
        AVPConstants.MOD_ID
    );

    @Override
    public <T extends ParticleOptions> BLHolder<ParticleType<T>> createHolder(String registryName, Supplier<ParticleType<T>> supplier) {
        return new NeoForgeParticleTypeHolder<>(PARTICLE_TYPES, registryName, supplier);
    }

    @Override
    public void register(BLHolder<ParticleType<?>> holder) { /* NO-OP FOR FORGE */ }
}
