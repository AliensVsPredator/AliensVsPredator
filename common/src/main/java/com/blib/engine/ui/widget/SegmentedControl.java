package com.blib.engine.ui.widget;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.layout.UiRect;
import com.blib.engine.ui.layout.UiText;

/**
 * Horizontal radio-button strip — a row of equal-width labeled segments where exactly one is "selected". Clicking a
 * segment makes it active. Used for tab-like view switches inside a panel (e.g. Agent / World State on the GOAP details
 * panel).
 */
@ApiStatus.Internal
public final class SegmentedControl {

    public static final int HEIGHT = 14;

    private static final int BG_COLOR = 0xFF1A1A1F;

    private static final int SEGMENT_INACTIVE_BG = 0xFF1A1A1F;

    private static final int SEGMENT_HOVER_BG = 0xFF2C2C32;

    private static final int SEGMENT_ACTIVE_BG = 0xFF3C3C46;

    private static final int SEPARATOR_COLOR = 0xFF101013;

    private static final int TEXT_INACTIVE = 0xFF888892;

    private static final int TEXT_ACTIVE = 0xFFE6C26B;

    private final List<String> labels;

    private int selectedIndex;

    private int rectX;

    private int rectY;

    private int rectWidth;

    public SegmentedControl(List<String> labels, int selectedIndex) {
        if (labels.isEmpty()) {
            throw new IllegalArgumentException("at least one segment required");
        }
        this.labels = List.copyOf(labels);
        this.selectedIndex = Math.max(0, Math.min(labels.size() - 1, selectedIndex));
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < labels.size()) {
            this.selectedIndex = index;
        }
    }

    public void render(GuiGraphics graphics, int x, int y, int width, int mouseX, int mouseY) {
        this.rectX = x;
        this.rectY = y;
        this.rectWidth = width;
        if (width <= 0) {
            return;
        }

        graphics.fill(x, y, x + width, y + HEIGHT, BG_COLOR);

        var font = EngineFont.get();
        for (var i = 0; i < labels.size(); i++) {
            var sx = segmentLeft(i);
            var sxEnd = segmentLeft(i + 1);
            var hovered = mouseX >= sx && mouseX < sxEnd && mouseY >= y && mouseY < y + HEIGHT;
            var bg = i == selectedIndex ? SEGMENT_ACTIVE_BG : (hovered ? SEGMENT_HOVER_BG : SEGMENT_INACTIVE_BG);
            graphics.fill(sx, y, sxEnd, y + HEIGHT, bg);

            var label = labels.get(i);
            var textColor = i == selectedIndex ? TEXT_ACTIVE : TEXT_INACTIVE;
            UiText.drawCentered(graphics, font, label, UiRect.of(sx + 2, y, Math.max(0, sxEnd - sx - 4), HEIGHT), textColor);

            if (i > 0) {
                graphics.fill(sx, y, sx + 1, y + HEIGHT, SEPARATOR_COLOR);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (mouseY < rectY || mouseY >= rectY + HEIGHT || mouseX < rectX || mouseX >= rectX + rectWidth) {
            return false;
        }
        for (var i = 0; i < labels.size(); i++) {
            if (mouseX >= segmentLeft(i) && mouseX < segmentLeft(i + 1)) {
                this.selectedIndex = i;
                return true;
            }
        }
        return false;
    }

    private int segmentLeft(int index) {
        return rectX + (int) Math.round((double) index * rectWidth / labels.size());
    }
}
