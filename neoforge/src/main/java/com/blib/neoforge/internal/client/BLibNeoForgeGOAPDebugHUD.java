package com.blib.neoforge.internal.client;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import com.blib.mod.client.render.goap.GOAPDebugHUD;

@ApiStatus.Internal
public final class BLibNeoForgeGOAPDebugHUD {

    public static void register() {
        NeoForge.EVENT_BUS.<RenderGuiEvent.Post>addListener(
            event -> GOAPDebugHUD.INSTANCE.render(event.getGuiGraphics(), event.getPartialTick().getRealtimeDeltaTicks())
        );
    }

    private BLibNeoForgeGOAPDebugHUD() {
        throw new UnsupportedOperationException();
    }
}
