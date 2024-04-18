package org.avp.common.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

import org.avp.api.Holder;
import org.avp.common.sound.AVPSoundEvents;

public class SoundUtils {

    public static Holder<SoundEvent> getRicochetSoundForSoundType(SoundType soundType) {
        Holder<SoundEvent> ricochetSfx;

        if (soundType == SoundType.GLASS) {
            ricochetSfx = AVPSoundEvents.INSTANCE.itemWeaponFxRicochetGlass;
        } else if (soundType == SoundType.GRAVEL) {
            ricochetSfx = AVPSoundEvents.INSTANCE.itemWeaponFxRicochetDirt;
        } else if (soundType == SoundType.METAL) {
            ricochetSfx = AVPSoundEvents.INSTANCE.itemWeaponFxRicochetMetal;
        } else {
            ricochetSfx = AVPSoundEvents.INSTANCE.itemWeaponFxRicochetGeneric;
        }
        return ricochetSfx;
    }

    private SoundUtils() {
        throw new UnsupportedOperationException();
    }
}
