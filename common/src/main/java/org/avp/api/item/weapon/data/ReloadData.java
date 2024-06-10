package org.avp.api.item.weapon.data;

import net.minecraft.sounds.SoundEvent;
import org.avp.api.Holder;
import org.avp.api.item.weapon.reload.ReloadBehavior;

public record ReloadData(
    int reloadTimeInTicks,
    Holder<SoundEvent> reloadStartSoundHolder,
    Holder<SoundEvent> reloadFinishSoundHolder,
    ReloadBehavior reloadBehavior
) {
}
