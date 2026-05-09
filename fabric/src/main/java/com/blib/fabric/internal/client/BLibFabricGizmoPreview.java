package com.blib.fabric.internal.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.render.v1.item.BLibGizmoPreviewRenderer;

@ApiStatus.Internal
public final class BLibFabricGizmoPreview {

    public static void register() {
        HudRenderCallback.EVENT.register(
            (guiGraphics, deltaTracker) -> BLibGizmoPreviewRenderer.render(guiGraphics, deltaTracker.getRealtimeDeltaTicks())
        );
    }

    private BLibFabricGizmoPreview() {
        throw new UnsupportedOperationException();
    }
}
