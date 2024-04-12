package org.avp.neoforge.service;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.AVPConstants;
import org.avp.common.service.SoundEventRegistry;
import org.avp.neoforge.util.ForgeGameObject;

public class NeoForgeSoundEventRegistry implements SoundEventRegistry {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(
        BuiltInRegistries.SOUND_EVENT,
        AVPConstants.MOD_ID
    );

    @Override
    public GameObject<SoundEvent> register(String registryName, Supplier<SoundEvent> supplier) {
        return new ForgeGameObject<>(SOUND_EVENTS, registryName, supplier);
    }
}
