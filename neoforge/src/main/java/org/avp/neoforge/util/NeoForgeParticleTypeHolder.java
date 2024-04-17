package org.avp.neoforge.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.api.Holder;

import java.util.function.Supplier;

public class NeoForgeParticleTypeHolder<T extends ParticleOptions> extends Holder<ParticleType<T>> {

    public NeoForgeParticleTypeHolder(
        DeferredRegister<ParticleType<?>> deferredRegister,
        String registryName,
        Supplier<ParticleType<T>> supplier
    ) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
