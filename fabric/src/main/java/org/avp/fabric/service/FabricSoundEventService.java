package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.service.SoundEventService;
import org.avp.common.registry.holder.AVPHolder;

public class FabricSoundEventService implements SoundEventService {

    @Override
    public BLHolder<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier) {
        return new AVPHolder<>(registryName, supplier);
    }

    @Override
    public void register(BLHolder<SoundEvent> holder) {
        Registry.register(BuiltInRegistries.SOUND_EVENT, holder.getResourceLocation(), holder.get());
    }
}
