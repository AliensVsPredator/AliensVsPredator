package com.blib.engine.ui.layout;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Text rendering helpers that always honor an explicit available width. Panels should prefer these over raw
 * {@code drawString} when drawing into resizable dock regions.
 */
@ApiStatus.Internal
public final class UiText {

    public static final String ELLIPSIS = "...";

    private static final ThreadLocal<TooltipCapture> TOOLTIP_CAPTURE = new ThreadLocal<>();

    private static @Nullable Component capturedTruncatedTextTooltip;

    private UiText() {}

    public static void clearCapturedTruncatedTextTooltip() {
        capturedTruncatedTextTooltip = null;
    }

    public static @Nullable Component capturedTruncatedTextTooltip() {
        return capturedTruncatedTextTooltip;
    }

    public static void setCapturedTruncatedTextTooltip(Component tooltip) {
        capturedTruncatedTextTooltip = tooltip;
    }

    public static TooltipCaptureScope captureTruncatedTextTooltips(int mouseX, int mouseY, UiRect hoverBounds, Consumer<Component> tooltipSink) {
        var previous = TOOLTIP_CAPTURE.get();
        TOOLTIP_CAPTURE.set(new TooltipCapture(mouseX, mouseY, hoverBounds, tooltipSink));
        return new TooltipCaptureScope(previous);
    }

    public static int centeredY(Font font, UiRect rect) {
        return rect.y() + (rect.height() - font.lineHeight + 2) / 2;
    }

    public static boolean isTruncated(Font font, @Nullable String text, int maxWidth) {
        return text != null && !text.isEmpty() && maxWidth > 0 && font.width(text) > maxWidth;
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
        captureTooltipIfTruncated(font, text, x, y, maxWidth);
    }

    public static void drawCentered(GuiGraphics graphics, Font font, @Nullable String text, UiRect rect, int color) {
        var clipped = clip(font, text, rect.width());
        if (clipped.isEmpty()) {
            return;
        }
        var textX = rect.x() + Math.max(0, (rect.width() - font.width(clipped)) / 2);
        graphics.drawString(font, Component.literal(clipped), textX, centeredY(font, rect), color, false);
        captureTooltipIfTruncated(font, text, rect);
    }

    public static void drawRight(GuiGraphics graphics, Font font, @Nullable String text, UiRect rect, int color) {
        var clipped = clip(font, text, rect.width());
        if (clipped.isEmpty()) {
            return;
        }
        var textX = rect.right() - font.width(clipped);
        graphics.drawString(font, Component.literal(clipped), textX, centeredY(font, rect), color, false);
        captureTooltipIfTruncated(font, text, rect);
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

    private static void captureTooltipIfTruncated(Font font, @Nullable String text, UiRect rect) {
        captureTooltipIfTruncated(font, text, rect.x(), rect.y(), rect.width(), rect.height());
    }

    private static void captureTooltipIfTruncated(Font font, @Nullable String text, int x, int y, int maxWidth) {
        captureTooltipIfTruncated(font, text, x, y, maxWidth, font.lineHeight);
    }

    private static void captureTooltipIfTruncated(Font font, @Nullable String text, int x, int y, int maxWidth, int height) {
        if (!isTruncated(font, text, maxWidth)) {
            return;
        }
        var capture = TOOLTIP_CAPTURE.get();
        if (capture == null || !capture.hoverBounds().contains(capture.mouseX(), capture.mouseY())) {
            return;
        }
        if (capture.mouseX() < x || capture.mouseX() >= x + maxWidth || capture.mouseY() < y || capture.mouseY() >= y + height) {
            return;
        }
        capture.tooltipSink().accept(Component.literal(text));
    }

    private record TooltipCapture(
        int mouseX,
        int mouseY,
        UiRect hoverBounds,
        Consumer<Component> tooltipSink
    ) {}

    public static final class TooltipCaptureScope implements AutoCloseable {

        private final @Nullable TooltipCapture previous;

        private TooltipCaptureScope(@Nullable TooltipCapture previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (previous == null) {
                TOOLTIP_CAPTURE.remove();
            } else {
                TOOLTIP_CAPTURE.set(previous);
            }
        }
    }
}
