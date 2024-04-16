package org.avp.neoforge.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.api.GameObject;

import java.util.function.Supplier;

public class NeoForgeParticleTypeGameObject<T extends ParticleOptions> extends GameObject<ParticleType<T>> {

    public NeoForgeParticleTypeGameObject(
        DeferredRegister<ParticleType<?>> deferredRegister,
        String registryName,
        Supplier<ParticleType<T>> supplier
    ) {
        super(registryName, deferredRegister.register(registryName, supplier));
    }
}
