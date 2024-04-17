package org.avp.neoforge.service;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.api.Holder;
import org.avp.common.AVPConstants;
import org.avp.common.service.ParticleTypeService;
import org.avp.neoforge.util.NeoForgeParticleTypeHolder;

import java.util.function.Supplier;

public class NeoForgeParticleTypeService implements ParticleTypeService {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, AVPConstants.MOD_ID);

    @Override
    public <T extends ParticleOptions> Holder<ParticleType<T>> createHolder(String registryName, Supplier<ParticleType<T>> supplier) {
        return new NeoForgeParticleTypeHolder<>(PARTICLE_TYPES, registryName, supplier);
    }

    @Override
    public void register(Holder<ParticleType<?>> holder) { /* NO-OP FOR FORGE */ }
}
