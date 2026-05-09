package com.blib.neoforge.internal.client;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.client.render.v1.item.BLibGizmoPreviewRenderer;

@ApiStatus.Internal
public final class BLibNeoForgeGizmoPreview {

    public static void register() {
        NeoForge.EVENT_BUS.<RenderGuiEvent.Post>addListener(
            event -> BLibGizmoPreviewRenderer.render(event.getGuiGraphics(), event.getPartialTick().getRealtimeDeltaTicks())
        );
    }

    private BLibNeoForgeGizmoPreview() {
        throw new UnsupportedOperationException();
    }
}
