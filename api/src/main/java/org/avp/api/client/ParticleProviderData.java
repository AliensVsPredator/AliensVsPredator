package org.avp.api.client;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

import org.avp.api.registry.holder.BLHolder;

public record ParticleProviderData<T extends ParticleOptions>(
    BLHolder<? extends ParticleType<? extends T>> particleTypeHolder,
    Function<SpriteSet, ParticleProvider<? extends T>> providerFactory
) {}
