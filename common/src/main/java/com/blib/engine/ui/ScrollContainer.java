package com.blib.engine.ui;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

/**
 * Stateful scroll-position holder for a panel section that's taller than its visible rect. Each frame the panel tells
 * the container its viewport size and its current content size; the container clamps the scroll offset and exposes it.
 * The panel is responsible for actually clipping (via {@code graphics.enableScissor}) and translating (via
 * {@code pose.translate}) — this class just owns position math + scrollbar render.
 */
@ApiStatus.Internal
public final class ScrollContainer {

    private static final int SCROLLBAR_WIDTH = 3;

    private static final int SCROLLBAR_TRACK_COLOR = 0x40000000;

    private static final int SCROLLBAR_THUMB_COLOR = 0xFF505058;

    private static final int SCROLLBAR_THUMB_HOVER_COLOR = 0xFF6868FF;

    private static final float SCROLL_PIXELS_PER_TICK = 18f;

    private float scrollY;

    private int viewHeight;

    private int contentHeight;

    public void layout(int viewHeight, int contentHeight) {
        this.viewHeight = Math.max(0, viewHeight);
        this.contentHeight = Math.max(0, contentHeight);

        var max = maxScroll();
        if (scrollY > max) {
            scrollY = max;
        }
        if (scrollY < 0) {
            scrollY = 0;
        }
    }

    public float scrollY() {
        return scrollY;
    }

    public int maxScroll() {
        return Math.max(0, contentHeight - viewHeight);
    }

    public boolean canScroll() {
        return contentHeight > viewHeight;
    }

    public void reset() {
        scrollY = 0;
    }

    public void scrollBy(float deltaPx) {
        var max = maxScroll();
        scrollY = Math.max(0, Math.min(max, scrollY + deltaPx));
    }

    /** Mouse-wheel handler: returns true if the scroll was consumed (i.e., we have content to scroll). */
    public boolean mouseScrolled(double scrollDy) {
        if (!canScroll()) {
            return false;
        }
        scrollBy((float) (-scrollDy * SCROLL_PIXELS_PER_TICK));
        return true;
    }

    /**
     * Renders a vertical scrollbar at the right edge of the given rect. No-ops when the content fits. {@code mouseX/Y}
     * are passed for the hover highlight.
     */
    public void renderScrollbar(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (!canScroll()) {
            return;
        }

        var barX = x + width - SCROLLBAR_WIDTH;
        var hovered = mouseX >= barX && mouseX < x + width && mouseY >= y && mouseY < y + height;

        graphics.fill(barX, y, barX + SCROLLBAR_WIDTH, y + height, SCROLLBAR_TRACK_COLOR);

        var thumbHeight = Math.max(8, (int) ((float) viewHeight / contentHeight * height));
        var thumbY = y + Math.round(scrollY / maxScroll() * (height - thumbHeight));
        graphics.fill(
            barX,
            thumbY,
            barX + SCROLLBAR_WIDTH,
            thumbY + thumbHeight,
            hovered ? SCROLLBAR_THUMB_HOVER_COLOR : SCROLLBAR_THUMB_COLOR
        );
    }
}
