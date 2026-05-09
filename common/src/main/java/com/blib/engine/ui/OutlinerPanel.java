package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.gizmo.BLibGizmoState;
import com.blib.engine.session.EngineMode;

/**
 * Placeholder outliner panel: opaque dark fill with a few read-only status lines pulled from the engine state
 * singletons. This is the seed of what will become a full outliner / properties panel — for now it just proves the dock
 * layout works and the panel can read engine state.
 */
@ApiStatus.Internal
public final class OutlinerPanel implements Panel {

    private static final int BACKGROUND_COLOR = 0xFF101010;

    private static final int LABEL_COLOR = 0xFFB0B0B0;

    private static final int VALUE_COLOR = 0xFFFFD060;

    private static final int CONTENT_PADDING = 5;

    private static final int LINE_HEIGHT = 10;

    @Override
    public String title() {
        return "Outliner";
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);

        var font = Minecraft.getInstance().font;
        var contentX = x + CONTENT_PADDING;
        var contentY = y + CONTENT_PADDING;

        var engineState = EngineMode.get().isActive() ? "ON" : "OFF";
        var gizmoMode = BLibGizmoState.mode().name();

        graphics.drawString(font, Component.literal("Engine Mode:"), contentX, contentY, LABEL_COLOR, false);
        graphics.drawString(font, Component.literal(engineState), contentX + 70, contentY, VALUE_COLOR, false);
        contentY += LINE_HEIGHT;

        graphics.drawString(font, Component.literal("Gizmo:"), contentX, contentY, LABEL_COLOR, false);
        graphics.drawString(font, Component.literal(gizmoMode), contentX + 70, contentY, VALUE_COLOR, false);
    }
}
