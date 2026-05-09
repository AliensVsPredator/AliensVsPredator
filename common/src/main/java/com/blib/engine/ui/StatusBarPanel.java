package com.blib.engine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.gizmo.BLibGizmoState;
import com.blib.engine.session.EngineMode;

/**
 * Bottom-of-screen status bar — full-bleed (no chrome). Shows engine + gizmo state on the left and the workspace name
 * on the right. Placeholder for what will eventually carry build / save / hint indicators.
 */
@ApiStatus.Internal
public final class StatusBarPanel implements Panel {

    public static final int HEIGHT = 13;

    private static final int BACKGROUND_COLOR = 0xFF1A1A1F;

    private static final int BORDER_COLOR = 0xFF0E0E11;

    private static final int LABEL_COLOR = 0xFF808088;

    private static final int VALUE_COLOR = 0xFFB8C0D0;

    private static final int ACCENT_COLOR = 0xFFE6C26B;

    private static final int EDGE_PADDING = 6;

    @Override
    public String title() {
        return "Status";
    }

    @Override
    public boolean hasChrome() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        graphics.fill(x, y, x + width, y + 1, BORDER_COLOR);

        var font = Minecraft.getInstance().font;
        // +2 compensates for MC font's descender padding so labels visually center; see MenuBarPanel.
        var textY = y + (height - font.lineHeight + 2) / 2;

        var engineActive = EngineMode.get().isActive();
        var engineLabel = engineActive ? "ENGINE: ON" : "ENGINE: OFF";
        var gizmoLabel = "GIZMO: " + BLibGizmoState.mode().name();

        graphics.drawString(
            font,
            Component.literal(engineLabel),
            x + EDGE_PADDING,
            textY,
            engineActive ? ACCENT_COLOR : LABEL_COLOR,
            false
        );

        var gizmoX = x + EDGE_PADDING + font.width(engineLabel) + 12;
        graphics.drawString(font, Component.literal(gizmoLabel), gizmoX, textY, VALUE_COLOR, false);

        var workspaceLabel = "Default Workspace";
        var rightX = x + width - EDGE_PADDING - font.width(workspaceLabel);
        graphics.drawString(font, Component.literal(workspaceLabel), rightX, textY, LABEL_COLOR, false);
    }
}
