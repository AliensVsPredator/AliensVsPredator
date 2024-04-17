package org.avp.fabric.service;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.AVPResources;
import org.avp.common.service.SoundEventService;

public class FabricSoundEventService implements SoundEventService {

    @Override
    public GameObject<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier) {
        return new GameObject<>(registryName, supplier);
    }

    @Override
    public void register(GameObject<SoundEvent> gameObject) {
        Registry.register(BuiltInRegistries.SOUND_EVENT, gameObject.getResourceLocation(), gameObject.get());
    }
}
