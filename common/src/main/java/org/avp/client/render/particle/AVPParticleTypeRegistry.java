package org.avp.client.render.particle;

import net.minecraft.core.particles.SimpleParticleType;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.registry.particle.AVPSimpleDeferredParticleTypeRegistry;
import org.avp.common.registry.holder.AVPHolder;

public class AVPParticleTypeRegistry extends AVPSimpleDeferredParticleTypeRegistry {

    public static final AVPParticleTypeRegistry INSTANCE = new AVPParticleTypeRegistry();

    public final BLHolder<SimpleParticleType> acid;

    private AVPParticleTypeRegistry() {
        acid = createSimpleHolder("acid");
    }

    private BLHolder<SimpleParticleType> createSimpleHolder(String registryName) {
        var holder = createHolder(registryName, () -> new SimpleParticleType(false) {});
        return new AVPHolder<>(holder.getResourceLocation().getPath(), () -> (SimpleParticleType) holder.get());
    }
}
