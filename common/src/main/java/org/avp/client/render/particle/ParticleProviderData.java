package org.avp.client.render.particle;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

import org.avp.api.Holder;

public record ParticleProviderData<T extends ParticleOptions>(
    Holder<? extends ParticleType<? extends T>> particleTypeHolder,
    Function<SpriteSet, ParticleProvider<? extends T>> providerFactory
) {}
