package org.avp.api.item.weapon;

import net.minecraft.sounds.SoundEvent;

import org.avp.api.GameObject;

public record FireMode(
    String identifier,
    int consumedAmmunition,
    int fireRateInTicks,
    GameObject<SoundEvent> shootSound,
    int shootSoundFrequency,
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
        float recoil
    ) {
        this(identifier, consumedAmmunition, fireRateInTicks, shootSound, 1, recoil);
    }
}
