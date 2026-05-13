package com.blib.engine.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.render.gizmo.BLibGizmoPreviewRenderer;

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
