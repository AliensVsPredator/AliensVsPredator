package org.avp.common.entity.data.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

import java.util.function.Function;

import org.avp.api.Holder;
import org.avp.common.sound.AVPSoundEvents;

public class AlienSounds {

    public static Function<DamageSource, Holder<SoundEvent>> createSoundEventSelector(Holder<SoundEvent> defaultSoundEventHolder) {
        return damageSource -> {
            if (damageSource.is(DamageTypeTags.IS_FIRE)) {
                return AVPSoundEvents.INSTANCE.entityXenomorphHurtScreech;
            }

            return defaultSoundEventHolder;
        };
    }

    private AlienSounds() {
        throw new UnsupportedOperationException();
    }
}
