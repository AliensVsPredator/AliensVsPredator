package org.avp.common.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

import java.util.function.Function;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.game.sound.AVPSoundEventRegistry;

public class AlienSounds {

    public static Function<DamageSource, BLHolder<SoundEvent>> createSoundEventSelector(BLHolder<SoundEvent> defaultSoundEventHolder) {
        return damageSource -> {
            if (damageSource.is(DamageTypeTags.IS_FIRE)) {
                return AVPSoundEventRegistry.INSTANCE.entityXenomorphHurtScreech;
            }

            return defaultSoundEventHolder;
        };
    }

    private AlienSounds() {
        throw new UnsupportedOperationException();
    }
}
