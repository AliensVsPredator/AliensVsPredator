package org.avp.api.common.weapon.data;

import net.minecraft.sounds.SoundEvent;
import org.avp.api.common.registry.holder.BLHolder;

public record ShootData(
    int backgroundShootSoundFrequencyInTicks,
    BLHolder<SoundEvent> backgroundShootSoundHolder,
    int shootSoundFrequencyInTicks,
    BLHolder<SoundEvent> shootSoundHolder
) {
}
