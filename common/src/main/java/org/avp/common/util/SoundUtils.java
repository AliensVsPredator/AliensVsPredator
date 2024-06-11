package org.avp.common.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

import org.avp.api.registry.holder.BLHolder;
import org.avp.common.game.sound.AVPSoundEventRegistry;

public class SoundUtils {

    public static BLHolder<SoundEvent> getRicochetSoundForSoundType(SoundType soundType) {
        BLHolder<SoundEvent> ricochetSfx;

        if (soundType == SoundType.GLASS) {
            ricochetSfx = AVPSoundEventRegistry.INSTANCE.itemWeaponFxRicochetGlass;
        } else if (soundType == SoundType.GRAVEL) {
            ricochetSfx = AVPSoundEventRegistry.INSTANCE.itemWeaponFxRicochetDirt;
        } else if (soundType == SoundType.METAL) {
            ricochetSfx = AVPSoundEventRegistry.INSTANCE.itemWeaponFxRicochetMetal;
        } else {
            ricochetSfx = AVPSoundEventRegistry.INSTANCE.itemWeaponFxRicochetGeneric;
        }
        return ricochetSfx;
    }

    private SoundUtils() {
        throw new UnsupportedOperationException();
    }
}
