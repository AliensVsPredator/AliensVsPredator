package org.avp.api.item.weapon.data;

import net.minecraft.sounds.SoundEvent;
import org.avp.api.Holder;

public record ShootData(
    int backgroundShootSoundFrequencyInTicks,
    Holder<SoundEvent> backgroundShootSoundHolder,
    int shootSoundFrequencyInTicks,
    Holder<SoundEvent> shootSoundHolder
) {
}
