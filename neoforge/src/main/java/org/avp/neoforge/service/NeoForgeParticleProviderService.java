package org.avp.neoforge.service;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.avp.api.Tuple;
import org.avp.common.service.ParticleProviderService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class NeoForgeParticleProviderService implements ParticleProviderService {

    private static final List<Tuple<Supplier<ParticleType<?>>, Function<SpriteSet, ParticleProvider<?>>>> ENTRIES = new ArrayList<>();

    public static List<Tuple<Supplier<ParticleType<?>>, Function<SpriteSet, ParticleProvider<?>>>> getEntries() {
        return ENTRIES;
    }

    @Override
    public <T extends ParticleOptions> void register(Supplier<ParticleType<T>> simpleParticleTypeSupplier, Function<SpriteSet, ParticleProvider<T>> factoryProvider) {
        ENTRIES.add(
            new Tuple<>(
                simpleParticleTypeSupplier::get,
                factoryProvider::apply
            )
        );
    }
}
