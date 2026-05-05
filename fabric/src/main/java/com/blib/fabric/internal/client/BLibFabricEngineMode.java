package com.blib.fabric.internal.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.engine.EngineHud;
import com.blib.internal.client.engine.EngineNavigation;

/**
 * Fabric-side wiring for engine mode: per-tick freecam input integration and HUD overlay rendering.
 */
@ApiStatus.Internal
public final class BLibFabricEngineMode {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> EngineNavigation.tick());
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> EngineHud.render(guiGraphics));
    }

    private BLibFabricEngineMode() {
        throw new UnsupportedOperationException();
    }
}
