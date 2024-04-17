package org.avp.common.service;

import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.Holder;

public interface SoundEventService {

    Holder<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier);

    void register(Holder<SoundEvent> holder);
}
