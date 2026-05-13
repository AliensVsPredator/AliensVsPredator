package com.blib.engine.ui.dock;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.EngineFont;

/**
 * Draws the chrome around a docked panel — a thin border and a title bar at the top — and returns the inner content
 * rectangle the panel should render into. Kept separate from {@link Panel} so panels can render without re-implementing
 * the same border drawing.
 */
@ApiStatus.Internal
public final class PanelChrome {

    public static final int TITLE_BAR_HEIGHT = 11;

    private static final int BORDER_THICKNESS = 1;

    private static final int TITLE_BAR_COLOR = 0xFF1A1A1A;

    private static final int BORDER_COLOR = 0xFF3A3A3A;

    private static final int TITLE_TEXT_COLOR = 0xFFD0D0D0;

    private static final int TITLE_TEXT_PADDING_X = 4;

    private static final int TITLE_TEXT_PADDING_Y = 1;

    public record Inner(
        int x,
        int y,
        int width,
        int height
    ) {}

    private PanelChrome() {}

    /**
     * Draws the title bar and border for a panel occupying {@code (x, y, width, height)} and returns the inner content
     * rect (the area the panel itself should render into).
     */
    public static Inner draw(GuiGraphics graphics, int x, int y, int width, int height, String title) {
        graphics.fill(x, y, x + width, y + TITLE_BAR_HEIGHT, TITLE_BAR_COLOR);

        graphics.fill(x, y + TITLE_BAR_HEIGHT, x + width, y + TITLE_BAR_HEIGHT + BORDER_THICKNESS, BORDER_COLOR);
        graphics.fill(x, y, x + BORDER_THICKNESS, y + height, BORDER_COLOR);
        graphics.fill(x + width - BORDER_THICKNESS, y, x + width, y + height, BORDER_COLOR);
        graphics.fill(x, y + height - BORDER_THICKNESS, x + width, y + height, BORDER_COLOR);

        var font = EngineFont.get();
        graphics.drawString(
            font,
            Component.literal(title),
            x + TITLE_TEXT_PADDING_X,
            y + TITLE_TEXT_PADDING_Y,
            TITLE_TEXT_COLOR,
            false
        );

        var innerY = y + TITLE_BAR_HEIGHT + BORDER_THICKNESS;
        var innerX = x + BORDER_THICKNESS;
        var innerWidth = Math.max(0, width - 2 * BORDER_THICKNESS);
        var innerHeight = Math.max(0, height - TITLE_BAR_HEIGHT - 2 * BORDER_THICKNESS);
        return new Inner(innerX, innerY, innerWidth, innerHeight);
    }
}
