package org.avp.neoforge.service;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.common.AVPConstants;
import org.avp.api.service.SoundEventService;
import org.avp.neoforge.util.NeoForgeHolder;

public class NeoForgeSoundEventService implements SoundEventService {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(
        BuiltInRegistries.SOUND_EVENT,
        AVPConstants.MOD_ID
    );

    @Override
    public BLHolder<SoundEvent> createHolder(String registryName, Supplier<SoundEvent> supplier) {
        return new NeoForgeHolder<>(SOUND_EVENTS, registryName, supplier);
    }

    @Override
    public void register(BLHolder<SoundEvent> holder) { /* NO-OP FOR FORGE */ }
}
