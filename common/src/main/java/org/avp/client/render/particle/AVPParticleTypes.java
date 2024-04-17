package org.avp.client.render.particle;

import net.minecraft.core.particles.SimpleParticleType;
import org.avp.api.Holder;
import org.avp.common.registry.AVPSimpleDeferredParticleTypeRegistry;

public class AVPParticleTypes extends AVPSimpleDeferredParticleTypeRegistry {

    public static final AVPParticleTypes INSTANCE = new AVPParticleTypes();

    public final Holder<SimpleParticleType> acid;

    private AVPParticleTypes() {
        acid = createSimpleHolder("acid");
    }

    private Holder<SimpleParticleType> createSimpleHolder(String registryName) {
        var holder = createHolder(registryName, () -> new SimpleParticleType(false) {});
        return new Holder<>(holder.getResourceLocation().getPath(), () -> (SimpleParticleType) holder.get());
    }
}
