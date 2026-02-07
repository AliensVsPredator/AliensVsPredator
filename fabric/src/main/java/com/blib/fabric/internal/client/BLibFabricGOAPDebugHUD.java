package com.blib.fabric.internal.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.jetbrains.annotations.ApiStatus;

import com.blib.mod.client.render.goap.GOAPDebugHUD;

@ApiStatus.Internal
public final class BLibFabricGOAPDebugHUD {

    public static void register() {
        HudRenderCallback.EVENT.register(
            (guiGraphics, deltaTracker) -> GOAPDebugHUD.INSTANCE.render(guiGraphics, deltaTracker.getRealtimeDeltaTicks())
        );
    }

    private BLibFabricGOAPDebugHUD() {
        throw new UnsupportedOperationException();
    }
}
