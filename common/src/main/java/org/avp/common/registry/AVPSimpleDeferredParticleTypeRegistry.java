package org.avp.common.registry;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.service.Services;

public class AVPSimpleDeferredParticleTypeRegistry extends AVPAbstractDeferredParticleTypeRegistry {

    @Override
    protected <T extends ParticleOptions> Holder<ParticleType<T>> createHolder(String registryName, Supplier<ParticleType<T>> supplier) {
        var holder = Services.PARTICLE_TYPE_SERVICE.createHolder(registryName, supplier);
        entries.add(holder);
        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void register() {
        entries.forEach(holder -> Services.PARTICLE_TYPE_SERVICE.register((Holder<ParticleType<?>>) holder));
    }
}
