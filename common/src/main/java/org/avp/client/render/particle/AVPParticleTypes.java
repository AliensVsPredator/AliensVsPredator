package org.avp.client.render.particle;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.avp.api.Holder;
import org.avp.common.service.Services;

import java.util.function.Function;

public class AVPParticleTypes {

    public static final Holder<SimpleParticleType> ACID;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static Holder<SimpleParticleType> registerSimple(String registryName, Function<SpriteSet, ParticleProvider<SimpleParticleType>> factoryProvider) {
        var holder = Services.PARTICLE_TYPE_SERVICE.register(registryName, factoryProvider);
        return new Holder<>(
            holder.getResourceLocation().getPath(),
            () -> (SimpleParticleType) holder.get()
        );
    }

    static {
        ACID = registerSimple("acid", AcidParticleProvider::new);
    }

    private AVPParticleTypes() {}
}
