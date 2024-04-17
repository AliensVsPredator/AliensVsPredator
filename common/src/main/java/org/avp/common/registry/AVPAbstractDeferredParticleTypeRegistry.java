package org.avp.common.registry;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.avp.api.Holder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AVPAbstractDeferredParticleTypeRegistry {

    protected final List<Holder<? extends ParticleType<?>>> entries;

    protected AVPAbstractDeferredParticleTypeRegistry() {
        entries = new ArrayList<>();
    }

    protected abstract <T extends ParticleOptions> Holder<ParticleType<T>> createHolder(String registryName, Supplier<ParticleType<T>> supplier);

    public abstract void register();

    public List<Holder<? extends ParticleType<?>>> getEntries() {
        return entries;
    }
}
