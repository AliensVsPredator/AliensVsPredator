package org.avp.common.entity.data.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;

import java.util.function.Function;

import org.avp.api.Holder;

public record EntitySoundData(
    Holder<SoundEvent> ambientSoundEventHolder,
    Function<DamageSource, Holder<SoundEvent>> hurtSoundEventHolderSelector,
    Holder<SoundEvent> deathSoundEventHolder
) {}
