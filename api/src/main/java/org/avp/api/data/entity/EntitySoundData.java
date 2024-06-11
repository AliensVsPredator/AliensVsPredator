package org.avp.api.data.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;

import java.util.function.Function;

import org.avp.api.registry.holder.BLHolder;

public record EntitySoundData(
    BLHolder<SoundEvent> ambientSoundEventHolder,
    Function<DamageSource, BLHolder<SoundEvent>> hurtSoundEventHolderSelector,
    BLHolder<SoundEvent> deathSoundEventHolder
) {}
