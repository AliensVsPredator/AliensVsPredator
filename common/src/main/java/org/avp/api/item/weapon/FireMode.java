package org.avp.api.item.weapon;

import net.minecraft.sounds.SoundEvent;

import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public record FireMode(
    String identifier,
    int consumedAmmunition,
    int fireRateInTicks,
    GameObject<SoundEvent> shootSound,
    int shootSoundFrequency,
    double range,
    float recoil
) {

    public FireMode {
        if (shootSoundFrequency <= 0) {
            throw new IllegalArgumentException("shootSoundFrequency must be greater than 0!");
        }
    }

    public FireMode(
        String identifier,
        int consumedAmmunition,
        int fireRateInTicks,
        GameObject<SoundEvent> shootSound,
        double range,
        float recoil
    ) {
        this(identifier, consumedAmmunition, fireRateInTicks, shootSound, 1, range, recoil);
    }
}
