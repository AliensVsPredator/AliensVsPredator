package com.blib.fabric.internal.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.jetbrains.annotations.ApiStatus;

import com.blib.mod.client.render.debug.PathfindingNavDebugHUD;

@ApiStatus.Internal
public final class BLibFabricPathfindingNavDebugHUD {

    public static void register() {
        HudRenderCallback.EVENT.register(
            (guiGraphics, deltaTracker) -> PathfindingNavDebugHUD.INSTANCE.render(
                guiGraphics,
                deltaTracker.getRealtimeDeltaTicks()
            )
        );
    }

    private BLibFabricPathfindingNavDebugHUD() {
        throw new UnsupportedOperationException();
    }
}
