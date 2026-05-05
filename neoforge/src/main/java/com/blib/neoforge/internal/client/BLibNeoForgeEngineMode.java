package com.blib.neoforge.internal.client;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.engine.EngineHud;
import com.blib.internal.client.engine.EngineNavigation;

/**
 * NeoForge-side wiring for engine mode: per-tick freecam input integration and HUD overlay rendering.
 */
@ApiStatus.Internal
public final class BLibNeoForgeEngineMode {

    public static void register() {
        NeoForge.EVENT_BUS.<ClientTickEvent.Post>addListener(event -> EngineNavigation.tick());
        NeoForge.EVENT_BUS.<RenderGuiEvent.Post>addListener(event -> EngineHud.render(event.getGuiGraphics()));
    }

    private BLibNeoForgeEngineMode() {
        throw new UnsupportedOperationException();
    }
}
