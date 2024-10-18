package org.avp.neoforge.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.common.registry.holder.AVPHolder;

public class NeoForgeParticleTypeHolder<T extends ParticleOptions> extends AVPHolder<ParticleType<T>> {

    public NeoForgeParticleTypeHolder(
        DeferredRegister<ParticleType<?>> deferredRegister,
        String registryName,
        Supplier<ParticleType<T>> supplier
    ) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
