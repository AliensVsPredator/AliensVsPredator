package org.avp.neoforge.service;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.AVPConstants;
import org.avp.common.service.SoundEventService;
import org.avp.neoforge.util.ForgeGameObject;

public class NeoForgeSoundEventService implements SoundEventService {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(
        BuiltInRegistries.SOUND_EVENT,
        AVPConstants.MOD_ID
    );

    @Override
    public GameObject<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier) {
        return new ForgeGameObject<>(SOUND_EVENTS, registryName, supplier);
    }

    @Override
    public void register(GameObject<SoundEvent> gameObject) { /* NO-OP FOR FORGE */ }
}
