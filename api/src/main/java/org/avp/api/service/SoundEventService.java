package org.avp.api.service;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;

public interface SoundEventService {

    BLHolder<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier);

    void register(BLHolder<SoundEvent> holder);
}
