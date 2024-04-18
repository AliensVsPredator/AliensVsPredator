package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.common.service.SoundEventService;

public class FabricSoundEventService implements SoundEventService {

    @Override
    public Holder<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier) {
        return new Holder<>(registryName, supplier);
    }

    @Override
    public void register(Holder<SoundEvent> holder) {
        Registry.register(BuiltInRegistries.SOUND_EVENT, holder.getResourceLocation(), holder.get());
    }
}
