package com.blib.engine.ui.widget;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

/**
 * Small boolean checkbox: an 11×11 square that fills with an accent-color check glyph when on. Click toggles. Used by
 * the generic block inspector for {@code BooleanProperty} rows (waterlogged, snowy, lit, …) — segmented controls and
 * selects both feel heavy when there are only two states. Each instance tracks its own rect from the most recent
 * {@link #render} call so {@link #mouseClicked} can hit-test independently.
 */
@ApiStatus.Internal
public final class Checkbox {

    public static final int SIZE = 11;

    private static final int BG_UNCHECKED = 0xFF14141A;

    private static final int BG_CHECKED = 0xFF3C3C46;

    private static final int BG_HOVER = 0xFF1A1A22;

    private static final int BORDER_COLOR = 0xFF353540;

    private static final int BORDER_HOVER_COLOR = 0xFF4F8FFF;

    private static final int CHECK_COLOR = 0xFFE6C26B;

    private final Consumer<Boolean> onToggle;

    private boolean checked;

    private int rectX;

    private int rectY;

    public Checkbox(boolean initial, Consumer<Boolean> onToggle) {
        this.checked = initial;
        this.onToggle = onToggle;
    }

    public boolean checked() {
        return checked;
    }

    /** Sync the visual state from external truth (e.g. live BlockState) without firing the toggle callback. */
    public void setChecked(boolean value) {
        this.checked = value;
    }

    public void render(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        this.rectX = x;
        this.rectY = y;
        var hovered = mouseX >= x && mouseX < x + SIZE && mouseY >= y && mouseY < y + SIZE;
        var bg = checked ? BG_CHECKED : (hovered ? BG_HOVER : BG_UNCHECKED);
        var border = hovered ? BORDER_HOVER_COLOR : BORDER_COLOR;
        graphics.fill(x, y, x + SIZE, y + SIZE, bg);
        graphics.fill(x, y, x + SIZE, y + 1, border);
        graphics.fill(x, y + SIZE - 1, x + SIZE, y + SIZE, border);
        graphics.fill(x, y, x + 1, y + SIZE, border);
        graphics.fill(x + SIZE - 1, y, x + SIZE, y + SIZE, border);

        if (checked) {
            drawCheck(graphics, x, y);
        }
    }

    /** Pixel-art check glyph centered in the 11×11 box — short diagonal down-right, longer diagonal up-right. */
    private static void drawCheck(GuiGraphics graphics, int x, int y) {
        graphics.fill(x + 3, y + 5, x + 4, y + 6, CHECK_COLOR);
        graphics.fill(x + 4, y + 6, x + 5, y + 7, CHECK_COLOR);
        graphics.fill(x + 5, y + 6, x + 6, y + 7, CHECK_COLOR);
        graphics.fill(x + 6, y + 5, x + 7, y + 6, CHECK_COLOR);
        graphics.fill(x + 7, y + 4, x + 8, y + 5, CHECK_COLOR);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (mouseX < rectX || mouseX >= rectX + SIZE || mouseY < rectY || mouseY >= rectY + SIZE) {
            return false;
        }
        this.checked = !this.checked;
        onToggle.accept(this.checked);
        return true;
    }
}
