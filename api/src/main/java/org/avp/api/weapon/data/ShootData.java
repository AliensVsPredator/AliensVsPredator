package org.avp.api.weapon.data;

import net.minecraft.sounds.SoundEvent;
import org.avp.api.registry.holder.BLHolder;

public record ShootData(
    int backgroundShootSoundFrequencyInTicks,
    BLHolder<SoundEvent> backgroundShootSoundHolder,
    int shootSoundFrequencyInTicks,
    BLHolder<SoundEvent> shootSoundHolder
) {
}
