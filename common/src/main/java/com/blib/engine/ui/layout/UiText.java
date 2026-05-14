package com.blib.engine.ui.layout;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Text rendering helpers that always honor an explicit available width. Panels should prefer these over raw
 * {@code drawString} when drawing into resizable dock regions.
 */
@ApiStatus.Internal
public final class UiText {

    public static final String ELLIPSIS = "...";

    private UiText() {}

    public static int centeredY(Font font, UiRect rect) {
        return rect.y() + (rect.height() - font.lineHeight + 2) / 2;
    }

    public static String clip(Font font, @Nullable String text, int maxWidth) {
        if (text == null || text.isEmpty() || maxWidth <= 0) {
            return "";
        }
        if (font.width(text) <= maxWidth) {
            return text;
        }
        var ellipsisWidth = font.width(ELLIPSIS);
        if (maxWidth <= ellipsisWidth) {
            return font.plainSubstrByWidth(ELLIPSIS, maxWidth);
        }
        return font.plainSubstrByWidth(text, maxWidth - ellipsisWidth) + ELLIPSIS;
    }

    public static void drawClipped(GuiGraphics graphics, Font font, @Nullable String text, int x, int y, int maxWidth, int color) {
        var clipped = clip(font, text, maxWidth);
        if (!clipped.isEmpty()) {
            graphics.drawString(font, Component.literal(clipped), x, y, color, false);
        }
    }

    public static void drawCentered(GuiGraphics graphics, Font font, @Nullable String text, UiRect rect, int color) {
        var clipped = clip(font, text, rect.width());
        if (clipped.isEmpty()) {
            return;
        }
        var textX = rect.x() + Math.max(0, (rect.width() - font.width(clipped)) / 2);
        graphics.drawString(font, Component.literal(clipped), textX, centeredY(font, rect), color, false);
    }

    public static void drawRight(GuiGraphics graphics, Font font, @Nullable String text, UiRect rect, int color) {
        var clipped = clip(font, text, rect.width());
        if (clipped.isEmpty()) {
            return;
        }
        var textX = rect.right() - font.width(clipped);
        graphics.drawString(font, Component.literal(clipped), textX, centeredY(font, rect), color, false);
    }

    public static void drawLabelValue(
        GuiGraphics graphics,
        Font font,
        UiRect row,
        String label,
        @Nullable String value,
        int labelWidth,
        int gap,
        int labelColor,
        int valueColor
    ) {
        var textY = centeredY(font, row);
        var safeLabelWidth = Math.max(0, Math.min(labelWidth, row.width()));
        drawClipped(graphics, font, label, row.x(), textY, safeLabelWidth, labelColor);

        var valueX = row.x() + safeLabelWidth + gap;
        var valueW = Math.max(0, row.right() - valueX);
        drawClipped(graphics, font, value, valueX, textY, valueW, valueColor);
    }
}
