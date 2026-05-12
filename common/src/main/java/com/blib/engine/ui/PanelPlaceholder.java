package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

/**
 * Centered dim-text helper for panels that can't show their content (no world, no project, no server data). Standard
 * wording keeps the menu-overlay UX consistent across panels — every "this needs X" screen looks the same.
 */
@ApiStatus.Internal
public final class PanelPlaceholder {

    public static final String NEEDS_WORLD = "Load a world to use this panel";

    public static final String NEEDS_PROJECT = "Open a project to use this panel";

    public static final String NEEDS_SERVER_DATA = "No data yet — load a world to populate";

    private static final int TEXT_COLOR = 0xFF808088;

    private PanelPlaceholder() {}

    /** Center {@code message} inside the panel rect, vertically and horizontally, in dim grey. */
    public static void drawCentered(GuiGraphics graphics, int x, int y, int width, int height, String message) {
        var font = EngineFont.get();
        var textWidth = font.width(message);
        var textX = x + (width - textWidth) / 2;
        var textY = y + (height - font.lineHeight) / 2;
        graphics.drawString(font, Component.literal(message), textX, textY, TEXT_COLOR, false);
    }
}
