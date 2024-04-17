package org.avp.client.render.particle;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.avp.api.GameObject;
import org.avp.common.service.Services;

import java.util.function.Function;

public class AVPParticleTypes {

    public static final GameObject<SimpleParticleType> ACID;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private static GameObject<SimpleParticleType> registerSimple(String registryName, Function<SpriteSet, ParticleProvider<SimpleParticleType>> factoryProvider) {
        var gameObject = Services.PARTICLE_REGISTRY.register(registryName, factoryProvider);
        return new GameObject<>(
            gameObject.getResourceLocation().getPath(),
            () -> (SimpleParticleType) gameObject.get()
        );
    }

    static {
        ACID = registerSimple("acid", AcidParticleFactory::new);
    }

    private AVPParticleTypes() {}
}
