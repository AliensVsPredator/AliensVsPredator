package org.avp.api.item.weapon.data;

import net.minecraft.sounds.SoundEvent;
import org.avp.api.Holder;

public record WindData(
    int windUpTimeInTicks,
    Holder<SoundEvent> windUpSoundHolder,
    Holder<SoundEvent> windDownSoundHolder
) {
    public static final WindData EMPTY = new WindData(-1, Holder.empty(), Holder.empty());
}
