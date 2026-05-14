package com.blib.engine.ui.layout;

import org.jetbrains.annotations.ApiStatus;

/**
 * Immutable logical-pixel rectangle for immediate-mode panel layout. Width and height are clamped at construction so
 * downstream code can safely use {@link #right()} / {@link #bottom()} without defensive negative-size checks.
 */
@ApiStatus.Internal
public record UiRect(
    int x,
    int y,
    int width,
    int height
) {

    public UiRect {
        width = Math.max(0, width);
        height = Math.max(0, height);
    }

    public static UiRect of(int x, int y, int width, int height) {
        return new UiRect(x, y, width, height);
    }

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public boolean isEmpty() {
        return width <= 0 || height <= 0;
    }

    public boolean contains(double px, double py) {
        return px >= x && px < right() && py >= y && py < bottom();
    }

    public UiRect inset(int all) {
        return inset(all, all, all, all);
    }

    public UiRect inset(int top, int right, int bottom, int left) {
        return new UiRect(x + left, y + top, width - left - right, height - top - bottom);
    }

    public UiRect withHeight(int nextHeight) {
        return new UiRect(x, y, width, nextHeight);
    }

    public UiRect withWidth(int nextWidth) {
        return new UiRect(x, y, nextWidth, height);
    }

    public UiRect translated(int dx, int dy) {
        return new UiRect(x + dx, y + dy, width, height);
    }

    public UiRect minusRight(int px) {
        return new UiRect(x, y, width - px, height);
    }

    public UiRect minusBottom(int px) {
        return new UiRect(x, y, width, height - px);
    }
}
