package org.avp.common.service;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.GameObject;

public interface SoundEventService {

    GameObject<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier);

    void register(GameObject<SoundEvent> gameObject);
}
