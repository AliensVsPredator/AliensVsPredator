package com.blib.engine.neoforge;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.session.EngineNavigation;

/**
 * NeoForge-side wiring for engine mode: per-tick freecam input integration.
 */
@ApiStatus.Internal
public final class BLibNeoForgeEngineMode {

    public static void register() {
        NeoForge.EVENT_BUS.<ClientTickEvent.Post>addListener(event -> EngineNavigation.tick());
    }

    private BLibNeoForgeEngineMode() {
        throw new UnsupportedOperationException();
    }
}
