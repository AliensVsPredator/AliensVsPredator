package com.blib.engine.ui.panel.chrome;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.EngineTickControl;
import com.blib.engine.ui.panel.viewport.ViewportPanel;

/**
 * Tiny transport overlay anchored at the top-left of the live-world {@link ViewportPanel}. Lives on the viewport
 * because the transport controls only mean anything in the context of the rendered world — same reasoning as the
 * modeler viewport's mode-switch toolbar ({@link ModelerViewportToolbar}). Playback state is read from
 * {@link EngineTickControl} every frame so the icons stay in sync with hotkeys or external triggers.
 */
@ApiStatus.Internal
public final class ViewportTransportToolbar {

    private static final int BUTTON_SIZE = 16;

    private static final int STEP_SIZE_WIDTH = 28;

    private static final int SPEED_WIDTH = 28;

    private static final int BUTTON_GAP = 2;

    private static final int INSET = 4;

    private static final int BG_COLOR = 0xFF1A1A1F;

    /** Tinted background when paused — makes the "world is frozen" state legible at a glance. */
    private static final int BG_PAUSED_COLOR = 0xFF463A1A;

    private static final int BG_FAST_FORWARD_COLOR = 0xFF1E4A43;

    private static final int BORDER_COLOR = 0xFF3A3A40;

    private static final int ICON_COLOR = 0xFFE0E0E0;

    private static final int ICON_DISABLED_COLOR = 0xFF606068;

    private static final int LABEL_COLOR = 0xFFE0E0E0;

    /** Result of a click against the toolbar. */
    public enum Hit {
        NONE,
        PLAY,
        STEP,
        STEP_SIZE,
        FAST_FORWARD,
        FAST_FORWARD_SPEED
    }

    private ViewportTransportToolbar() {}

    public static void render(GuiGraphics graphics, int panelX, int panelY) {
        // Transport controls act on the integrated server. With no world (menu-overlay mode pre-world-load) every
        // button is a no-op, so hide the toolbar entirely rather than show non-functional chrome.
        if (Minecraft.getInstance().getSingleplayerServer() == null) {
            return;
        }
        var playX = panelX + INSET;
        var stepX = playX + BUTTON_SIZE + BUTTON_GAP;
        var stepSizeX = stepX + BUTTON_SIZE + BUTTON_GAP;
        var fastForwardX = stepSizeX + STEP_SIZE_WIDTH + BUTTON_GAP;
        var speedX = fastForwardX + BUTTON_SIZE + BUTTON_GAP;
        var btnY = panelY + INSET;

        var paused = EngineTickControl.isPaused();
        var fastForwarding = EngineTickControl.isFastForwarding();

        drawButton(graphics, playX, btnY, paused ? BG_PAUSED_COLOR : BG_COLOR);
        if (paused) {
            drawPlayIcon(graphics, playX, btnY);
        } else {
            drawPauseIcon(graphics, playX, btnY);
        }

        drawButton(graphics, stepX, btnY, BG_COLOR);
        // Step is a no-op when the world isn't paused (server's stepGameIfPaused only advances a frozen game), so we
        // dim the icon as a soft "this won't do anything" cue while still letting the click fall through harmlessly.
        drawStepIcon(graphics, stepX, btnY, paused ? ICON_COLOR : ICON_DISABLED_COLOR);

        drawLabelButton(
            graphics,
            stepSizeX,
            btnY,
            STEP_SIZE_WIDTH,
            EngineTickControl.selectedStepTicks() + "t",
            BG_COLOR
        );

        drawButton(graphics, fastForwardX, btnY, fastForwarding ? BG_FAST_FORWARD_COLOR : BG_COLOR);
        drawFastForwardIcon(graphics, fastForwardX, btnY, ICON_COLOR);

        drawLabelButton(
            graphics,
            speedX,
            btnY,
            SPEED_WIDTH,
            EngineTickControl.selectedFastForwardMultiplier() + "x",
            fastForwarding ? BG_FAST_FORWARD_COLOR : BG_COLOR
        );
    }

    public static Hit hitTest(double mouseX, double mouseY, int panelX, int panelY) {
        var playX = panelX + INSET;
        var stepX = playX + BUTTON_SIZE + BUTTON_GAP;
        var stepSizeX = stepX + BUTTON_SIZE + BUTTON_GAP;
        var fastForwardX = stepSizeX + STEP_SIZE_WIDTH + BUTTON_GAP;
        var speedX = fastForwardX + BUTTON_SIZE + BUTTON_GAP;
        var btnY = panelY + INSET;
        if (mouseY < btnY || mouseY >= btnY + BUTTON_SIZE) {
            return Hit.NONE;
        }
        if (mouseX >= playX && mouseX < playX + BUTTON_SIZE) {
            return Hit.PLAY;
        }
        if (mouseX >= stepX && mouseX < stepX + BUTTON_SIZE) {
            return Hit.STEP;
        }
        if (mouseX >= stepSizeX && mouseX < stepSizeX + STEP_SIZE_WIDTH) {
            return Hit.STEP_SIZE;
        }
        if (mouseX >= fastForwardX && mouseX < fastForwardX + BUTTON_SIZE) {
            return Hit.FAST_FORWARD;
        }
        if (mouseX >= speedX && mouseX < speedX + SPEED_WIDTH) {
            return Hit.FAST_FORWARD_SPEED;
        }
        return Hit.NONE;
    }

    private static void drawButton(GuiGraphics graphics, int x, int y, int bgColor) {
        drawButton(graphics, x, y, BUTTON_SIZE, bgColor);
    }

    private static void drawButton(GuiGraphics graphics, int x, int y, int width, int bgColor) {
        graphics.fill(x, y, x + width, y + BUTTON_SIZE, bgColor);
        graphics.fill(x, y, x + width, y + 1, BORDER_COLOR);
        graphics.fill(x, y + BUTTON_SIZE - 1, x + width, y + BUTTON_SIZE, BORDER_COLOR);
        graphics.fill(x, y, x + 1, y + BUTTON_SIZE, BORDER_COLOR);
        graphics.fill(x + width - 1, y, x + width, y + BUTTON_SIZE, BORDER_COLOR);
    }

    private static void drawLabelButton(GuiGraphics graphics, int x, int y, int width, String label, int bgColor) {
        drawButton(graphics, x, y, width, bgColor);
        var font = Minecraft.getInstance().font;
        var textX = x + (width - font.width(label)) / 2;
        var textY = y + (BUTTON_SIZE - font.lineHeight) / 2 + 1;
        graphics.drawString(font, label, textX, textY, LABEL_COLOR, false);
    }

    private static void drawPlayIcon(GuiGraphics graphics, int btnX, int btnY) {
        var rows = 7;
        var halfHeight = (rows - 1) / 2;
        var triangleWidth = halfHeight + 1;
        var startX = btnX + (BUTTON_SIZE - triangleWidth) / 2;
        var startY = btnY + (BUTTON_SIZE - rows) / 2;
        for (var row = 0; row < rows; row++) {
            var w = halfHeight + 1 - Math.abs(row - halfHeight);
            graphics.fill(startX, startY + row, startX + w, startY + row + 1, ICON_COLOR);
        }
    }

    private static void drawPauseIcon(GuiGraphics graphics, int btnX, int btnY) {
        var cx = btnX + BUTTON_SIZE / 2;
        var cy = btnY + BUTTON_SIZE / 2;
        var barWidth = 2;
        var barHalfHeight = 4;
        var gap = 2;
        graphics.fill(cx - gap - barWidth, cy - barHalfHeight, cx - gap, cy + barHalfHeight, ICON_COLOR);
        graphics.fill(cx + gap, cy - barHalfHeight, cx + gap + barWidth, cy + barHalfHeight, ICON_COLOR);
    }

    private static void drawStepIcon(GuiGraphics graphics, int btnX, int btnY, int color) {
        var rows = 7;
        var halfHeight = (rows - 1) / 2;
        var triangleWidth = halfHeight + 1;
        var barWidth = 1;
        var bartriangleGap = 1;
        var clusterWidth = triangleWidth + bartriangleGap + barWidth;
        var startX = btnX + (BUTTON_SIZE - clusterWidth) / 2;
        var startY = btnY + (BUTTON_SIZE - rows) / 2;
        for (var row = 0; row < rows; row++) {
            var w = halfHeight + 1 - Math.abs(row - halfHeight);
            graphics.fill(startX, startY + row, startX + w, startY + row + 1, color);
        }
        var barX = startX + triangleWidth + bartriangleGap;
        graphics.fill(barX, startY, barX + barWidth, startY + rows, color);
    }

    private static void drawFastForwardIcon(GuiGraphics graphics, int btnX, int btnY, int color) {
        var rows = 7;
        var halfHeight = (rows - 1) / 2;
        var triangleWidth = halfHeight + 1;
        var triangleGap = 1;
        var clusterWidth = triangleWidth * 2 + triangleGap;
        var startX = btnX + (BUTTON_SIZE - clusterWidth) / 2;
        var startY = btnY + (BUTTON_SIZE - rows) / 2;
        drawTriangle(graphics, startX, startY, rows, color);
        drawTriangle(graphics, startX + triangleWidth + triangleGap, startY, rows, color);
    }

    private static void drawTriangle(GuiGraphics graphics, int startX, int startY, int rows, int color) {
        var halfHeight = (rows - 1) / 2;
        for (var row = 0; row < rows; row++) {
            var w = halfHeight + 1 - Math.abs(row - halfHeight);
            graphics.fill(startX, startY + row, startX + w, startY + row + 1, color);
        }
    }
}
