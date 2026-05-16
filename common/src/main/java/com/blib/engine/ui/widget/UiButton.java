package com.blib.engine.ui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.layout.UiRect;

@ApiStatus.Internal
public final class UiButton {

    public static final int ADDITIVE_CONTENT_COLOR = 0xFF80E080;

    public static final Style DEFAULT = new Style(
        0xFF14141A,
        0xFF22222C,
        0xFF3C3C46,
        0xFF4A4A56,
        0xFF101013,
        0xFF353540,
        0xFFD0D0D0,
        0xFF606068,
        ADDITIVE_CONTENT_COLOR
    );

    public static final Style CHROME = new Style(
        0xFF1A1A1F,
        0xFF2A2A36,
        0xFF4A4A4A,
        0xFF565660,
        0xFF101013,
        0xFF3A3A40,
        0xFFE0E0E0,
        0xFF606068,
        ADDITIVE_CONTENT_COLOR
    );

    public static final Style CHIP = new Style(
        0xFF2C2C32,
        0xFF3C3C46,
        0xFF3C3C46,
        0xFF4A4A56,
        0xFF101013,
        0xFF3A3A40,
        0xFFE0E0E0,
        0xFF606068,
        ADDITIVE_CONTENT_COLOR
    );

    private UiButton() {}

    public static boolean hovered(UiRect rect, boolean enabled, int mouseX, int mouseY) {
        return enabled && rect.contains(mouseX, mouseY);
    }

    public static void drawFrame(
        GuiGraphics graphics,
        UiRect rect,
        Style style,
        boolean active,
        boolean enabled,
        int mouseX,
        int mouseY
    ) {
        if (rect.isEmpty()) {
            return;
        }
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.bottom(), background(style, active, enabled, hovered(rect, enabled, mouseX, mouseY)));
        drawBorder(graphics, rect, style.borderColor());
    }

    public static void drawBorder(GuiGraphics graphics, UiRect rect, int color) {
        if (rect.isEmpty()) {
            return;
        }
        graphics.fill(rect.x(), rect.y(), rect.right(), rect.y() + 1, color);
        graphics.fill(rect.x(), rect.bottom() - 1, rect.right(), rect.bottom(), color);
        graphics.fill(rect.x(), rect.y(), rect.x() + 1, rect.bottom(), color);
        graphics.fill(rect.right() - 1, rect.y(), rect.right(), rect.bottom(), color);
    }

    public static void drawCenteredLabel(
        GuiGraphics graphics,
        Font font,
        String label,
        UiRect rect,
        Style style,
        int textColor,
        boolean enabled
    ) {
        drawCenteredLabel(graphics, font, label, rect, style, textColor, enabled, true);
    }

    public static void drawCenteredLabel(
        GuiGraphics graphics,
        Font font,
        String label,
        UiRect rect,
        Style style,
        int textColor,
        boolean enabled,
        boolean greenLeadingPlus
    ) {
        var color = enabled ? textColor : style.disabledTextColor();
        if (greenLeadingPlus && label != null && label.startsWith("+")) {
            drawCenteredLeadingPlus(graphics, font, label, rect, style, color, enabled);
            return;
        }
        var text = label == null ? "" : label;
        var textX = rect.x() + Math.max(0, (rect.width() - font.width(text)) / 2);
        var textY = rect.y() + (rect.height() - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal(text), textX, textY, color, false);
    }

    public static void drawPlusIcon(GuiGraphics graphics, UiRect rect, Style style, boolean enabled) {
        var cx = rect.x() + rect.width() / 2;
        var cy = rect.y() + rect.height() / 2;
        var color = enabled ? style.additiveTextColor() : style.disabledTextColor();
        graphics.fill(cx - 3, cy, cx + 4, cy + 1, color);
        graphics.fill(cx, cy - 3, cx + 1, cy + 4, color);
    }

    public static void drawMinusIcon(GuiGraphics graphics, UiRect rect, Style style, boolean enabled) {
        var cx = rect.x() + rect.width() / 2;
        var cy = rect.y() + rect.height() / 2;
        var color = enabled ? style.textColor() : style.disabledTextColor();
        graphics.fill(cx - 3, cy, cx + 4, cy + 1, color);
    }

    private static int background(Style style, boolean active, boolean enabled, boolean hovered) {
        if (!enabled) {
            return style.disabledBgColor();
        }
        if (active) {
            return hovered ? style.activeHoverBgColor() : style.activeBgColor();
        }
        return hovered ? style.hoverBgColor() : style.bgColor();
    }

    private static void drawCenteredLeadingPlus(
        GuiGraphics graphics,
        Font font,
        String label,
        UiRect rect,
        Style style,
        int textColor,
        boolean enabled
    ) {
        var rest = label.substring(1);
        var plusWidth = font.width("+");
        var restWidth = font.width(rest);
        var textX = rect.x() + Math.max(0, (rect.width() - plusWidth - restWidth) / 2);
        var textY = rect.y() + (rect.height() - font.lineHeight + 2) / 2;
        graphics.drawString(font, Component.literal("+"), textX, textY, enabled ? style.additiveTextColor() : style.disabledTextColor(), false);
        graphics.drawString(font, Component.literal(rest), textX + plusWidth, textY, textColor, false);
    }

    public record Style(
        int bgColor,
        int hoverBgColor,
        int activeBgColor,
        int activeHoverBgColor,
        int disabledBgColor,
        int borderColor,
        int textColor,
        int disabledTextColor,
        int additiveTextColor
    ) {

        public Style withActiveColors(int activeBgColor, int activeHoverBgColor) {
            return new Style(
                bgColor,
                hoverBgColor,
                activeBgColor,
                activeHoverBgColor,
                disabledBgColor,
                borderColor,
                textColor,
                disabledTextColor,
                additiveTextColor
            );
        }
    }
}
