package org.avp.common.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

import org.avp.api.GameObject;
import org.avp.common.sound.AVPSoundEvents;

public class SoundUtils {

    public static GameObject<SoundEvent> getRicochetSoundForSoundType(SoundType soundType) {
        GameObject<SoundEvent> ricochetSfx;

        if (soundType == SoundType.GLASS) {
            ricochetSfx = AVPSoundEvents.INSTANCE.ITEM_WEAPON_FX_RICOCHET_GLASS;
        } else if (soundType == SoundType.GRAVEL) {
            ricochetSfx = AVPSoundEvents.INSTANCE.ITEM_WEAPON_FX_RICOCHET_DIRT;
        } else if (soundType == SoundType.METAL) {
            ricochetSfx = AVPSoundEvents.INSTANCE.ITEM_WEAPON_FX_RICOCHET_METAL;
        } else {
            ricochetSfx = AVPSoundEvents.INSTANCE.ITEM_WEAPON_FX_RICOCHET_GENERIC;
        }
        return ricochetSfx;
    }

    private SoundUtils() {
        throw new UnsupportedOperationException();
    }
}
