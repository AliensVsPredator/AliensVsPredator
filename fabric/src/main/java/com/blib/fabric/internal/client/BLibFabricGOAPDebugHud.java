package com.blib.fabric.internal.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.client.goap.GOAPDebugHud;

@ApiStatus.Internal
public final class BLibFabricGOAPDebugHud {

    public static void register() {
        HudRenderCallback.EVENT.register(
            (drawContext, tickCounter) -> GOAPDebugHud.INSTANCE.render(drawContext, tickCounter.getRealtimeDeltaTicks())
        );
    }

    private BLibFabricGOAPDebugHud() {
        throw new UnsupportedOperationException();
    }
}
