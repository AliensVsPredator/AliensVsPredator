package com.blib.engine.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.input.BLibKeyBindings;

/**
 * NeoForge-side registration for BLib's vanilla {@link net.minecraft.client.KeyMapping}s. Subscribes to
 * {@code RegisterKeyMappingsEvent} on the mod event bus, then calls {@code event.register} so they show up in MC's
 * controls menu.
 */
@ApiStatus.Internal
public final class BLibNeoForgeKeyBindings {

    public static void register(IEventBus modEventBus) {
        modEventBus.<RegisterKeyMappingsEvent>addListener(event -> event.register(BLibKeyBindings.TOGGLE_ENGINE));
    }

    private BLibNeoForgeKeyBindings() {}
}
