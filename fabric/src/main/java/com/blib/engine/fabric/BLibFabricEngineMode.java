package com.blib.engine.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.session.EngineNavigation;

/**
 * Fabric-side wiring for engine mode: per-tick freecam input integration.
 */
@ApiStatus.Internal
public final class BLibFabricEngineMode {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> EngineNavigation.tick());
    }

    private BLibFabricEngineMode() {
        throw new UnsupportedOperationException();
    }
}
