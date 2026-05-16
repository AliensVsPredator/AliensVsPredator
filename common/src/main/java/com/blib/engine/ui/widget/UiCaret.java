package com.blib.engine.ui.widget;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class UiCaret {

    public static final String COLLAPSED = "▸";

    public static final String EXPANDED = "▾";

    private UiCaret() {}

    public static String glyph(boolean collapsed) {
        return collapsed ? COLLAPSED : EXPANDED;
    }

    public static void drawPixel(GuiGraphics graphics, int x, int y, boolean collapsed, int color) {
        if (collapsed) {
            for (var row = 0; row < 7; row++) {
                var width = row <= 3 ? row + 1 : 7 - row;
                graphics.fill(x, y + row, x + width, y + row + 1, color);
            }
            return;
        }
        for (var row = 0; row < 4; row++) {
            graphics.fill(x + row, y + row + 1, x + 7 - row, y + row + 2, color);
        }
    }
}
