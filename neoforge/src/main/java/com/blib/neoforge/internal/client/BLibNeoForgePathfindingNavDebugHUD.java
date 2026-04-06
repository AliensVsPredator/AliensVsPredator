package com.blib.neoforge.internal.client;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import com.blib.mod.client.render.debug.PathfindingNavDebugHUD;

@ApiStatus.Internal
public final class BLibNeoForgePathfindingNavDebugHUD {

    public static void register() {
        NeoForge.EVENT_BUS.<RenderGuiEvent.Post>addListener(
            event -> PathfindingNavDebugHUD.INSTANCE.render(
                event.getGuiGraphics(),
                event.getPartialTick().getRealtimeDeltaTicks()
            )
        );
    }

    private BLibNeoForgePathfindingNavDebugHUD() {
        throw new UnsupportedOperationException();
    }
}
