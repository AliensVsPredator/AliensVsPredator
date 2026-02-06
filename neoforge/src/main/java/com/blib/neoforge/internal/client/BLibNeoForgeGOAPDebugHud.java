package com.blib.neoforge.internal.client;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.goap.GOAPDebugHud;

@ApiStatus.Internal
public final class BLibNeoForgeGOAPDebugHud {

    public static void register() {
        NeoForge.EVENT_BUS.<RenderGuiEvent.Post>addListener(
            event -> GOAPDebugHud.INSTANCE.render(event.getGuiGraphics(), event.getPartialTick().getRealtimeDeltaTicks())
        );
    }

    private BLibNeoForgeGOAPDebugHud() {
        throw new UnsupportedOperationException();
    }
}
