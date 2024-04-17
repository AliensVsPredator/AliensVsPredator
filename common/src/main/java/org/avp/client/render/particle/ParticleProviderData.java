package org.avp.client.render.particle;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.avp.api.Holder;

import java.util.function.Function;

public record ParticleProviderData<T extends ParticleOptions>(
    Holder<? extends ParticleType<? extends T>> particleTypeHolder,
    Function<SpriteSet, ParticleProvider<? extends T>> providerFactory
) {}
