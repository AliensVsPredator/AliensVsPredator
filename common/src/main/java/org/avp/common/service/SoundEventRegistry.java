package org.avp.common.service;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.GameObject;

public interface SoundEventRegistry {

    GameObject<SoundEvent> register(String registryName, Supplier<SoundEvent> supplier);
}
