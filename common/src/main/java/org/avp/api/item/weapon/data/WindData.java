package org.avp.api.item.weapon.data;

import net.minecraft.sounds.SoundEvent;
import org.avp.api.registry.holder.BLHolder;

public record WindData(
    int windUpTimeInTicks,
    BLHolder<SoundEvent> windUpSoundHolder,
    BLHolder<SoundEvent> windDownSoundHolder
) {
    public static final WindData EMPTY = new WindData(-1, BLHolder.empty(), BLHolder.empty());
}
