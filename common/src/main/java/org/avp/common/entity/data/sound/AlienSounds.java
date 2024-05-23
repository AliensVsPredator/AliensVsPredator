package org.avp.common.entity.data.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import org.avp.api.Holder;
import org.avp.common.sound.AVPSoundEvents;

import java.util.function.Function;

public class AlienSounds {

    public static Function<DamageSource, Holder<SoundEvent>> createSoundEventSelector(Holder<SoundEvent> defaultSoundEventHolder) {
        return damageSource -> {
            if (damageSource.is(DamageTypeTags.IS_FIRE)) {
                return AVPSoundEvents.INSTANCE.entityPraetorianHurt;
            }

            return defaultSoundEventHolder;
        };
    }


    private AlienSounds() {
        throw new UnsupportedOperationException();
    }
}
