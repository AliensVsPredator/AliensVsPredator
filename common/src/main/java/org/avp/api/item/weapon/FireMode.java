package org.avp.api.item.weapon;

import net.minecraft.sounds.SoundEvent;

import org.avp.api.GameObject;

public record FireMode(
    String identifier,
    int consumedAmmunition,
    int fireRateInTicks,
    GameObject<SoundEvent> fireSound,
    float recoil
) {}
