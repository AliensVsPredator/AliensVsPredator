package org.avp.api.registry.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.registry.holder.BLHolder;

public abstract class AVPAbstractDeferredParticleTypeRegistry {

    protected final List<BLHolder<? extends ParticleType<?>>> entries;

    protected AVPAbstractDeferredParticleTypeRegistry() {
        entries = new ArrayList<>();
    }

    protected abstract <T extends ParticleOptions> BLHolder<ParticleType<T>> createHolder(
        String registryName,
        Supplier<ParticleType<T>> supplier
    );

    public abstract void register();

    public List<BLHolder<? extends ParticleType<?>>> getEntries() {
        return entries;
    }
}
