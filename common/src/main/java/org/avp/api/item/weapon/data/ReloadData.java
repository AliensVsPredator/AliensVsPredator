package org.avp.api.item.weapon.data;

import net.minecraft.sounds.SoundEvent;
import org.avp.api.registry.holder.BLHolder;
import org.avp.api.item.weapon.reload.ReloadBehavior;

public record ReloadData(
    int reloadTimeInTicks,
    BLHolder<SoundEvent> reloadStartSoundHolder,
    BLHolder<SoundEvent> reloadFinishSoundHolder,
    ReloadBehavior reloadBehavior
) {
}
