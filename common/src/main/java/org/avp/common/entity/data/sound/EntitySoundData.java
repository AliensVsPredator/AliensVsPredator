package org.avp.common.entity.data.sound;

import net.minecraft.sounds.SoundEvent;
import org.avp.api.Holder;

public record EntitySoundData(
    Holder<SoundEvent> ambientSoundEventHolder,
    Holder<SoundEvent> hurtSoundEventHolder,
    Holder<SoundEvent> deathSoundEventHolder
) {}
