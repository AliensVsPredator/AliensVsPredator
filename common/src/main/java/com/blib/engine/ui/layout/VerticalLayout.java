package com.blib.engine.ui.layout;

import org.jetbrains.annotations.ApiStatus;

/**
 * Tiny immediate-mode vertical flow cursor. It does not render anything itself; it just hands callers stable row rects
 * and keeps y-position math centralized.
 */
@ApiStatus.Internal
public final class VerticalLayout {

    private final int x;

    private final int width;

    private int y;

    public VerticalLayout(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = Math.max(0, width);
    }

    public UiRect take(int height) {
        var rect = new UiRect(x, y, width, height);
        y += Math.max(0, height);
        return rect;
    }

    public UiRect take(int height, int gapAfter) {
        var rect = take(height);
        gap(gapAfter);
        return rect;
    }

    public void gap(int amount) {
        y += Math.max(0, amount);
    }

    public int y() {
        return y;
    }

    public static UiRect[] columns(UiRect row, int count, int gap) {
        if (count <= 0) {
            return new UiRect[0];
        }
        var out = new UiRect[count];
        var available = Math.max(0, row.width() - Math.max(0, count - 1) * gap);
        var base = available / count;
        var remainder = available % count;
        var cursorX = row.x();
        for (var i = 0; i < count; i++) {
            var w = base + (i < remainder ? 1 : 0);
            out[i] = new UiRect(cursorX, row.y(), w, row.height());
            cursorX += w + gap;
        }
        return out;
    }
}
