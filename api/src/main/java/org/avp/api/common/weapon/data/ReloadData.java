package org.avp.api.common.weapon.data;

import net.minecraft.sounds.SoundEvent;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.weapon.reload.ReloadBehavior;

public record ReloadData(
    int reloadTimeInTicks,
    BLHolder<SoundEvent> reloadStartSoundHolder,
    BLHolder<SoundEvent> reloadFinishSoundHolder,
    ReloadBehavior reloadBehavior
) {}
