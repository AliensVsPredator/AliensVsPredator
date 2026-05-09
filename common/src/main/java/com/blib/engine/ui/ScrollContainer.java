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

    /** Track rect captured during {@link #renderScrollbar} so mouse handlers can hit-test the bar. */
    private int trackX;

    private int trackY;

    private int trackHeight;

    private boolean rectKnown;

    /** True while the user is dragging the thumb. Persists across frames until {@link #mouseReleased} fires. */
    private boolean draggingThumb;

    /** Pixel offset of the original click within the thumb, so the thumb doesn't jump to cursor center on grab. */
    private float dragOffsetWithinThumb;

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
     * are passed for the hover highlight. Captures the track rect so {@link #mouseClicked} / {@link #mouseDragged} have
     * something to hit-test against.
     */
    public void renderScrollbar(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (!canScroll()) {
            // Content fits — no track is drawn, so kill any stale drag state from before the content shrank
            // (e.g. the user typed a search that filtered most cards out mid-drag).
            this.rectKnown = false;
            this.draggingThumb = false;
            return;
        }

        var barX = x + width - SCROLLBAR_WIDTH;
        this.trackX = barX;
        this.trackY = y;
        this.trackHeight = height;
        this.rectKnown = true;

        var hovered = mouseX >= barX && mouseX < x + width && mouseY >= y && mouseY < y + height;

        graphics.fill(barX, y, barX + SCROLLBAR_WIDTH, y + height, SCROLLBAR_TRACK_COLOR);

        var thumbHeight = computeThumbHeight();
        var thumbY = computeThumbY(thumbHeight);
        var thumbColor = (hovered || draggingThumb) ? SCROLLBAR_THUMB_HOVER_COLOR : SCROLLBAR_THUMB_COLOR;
        graphics.fill(barX, thumbY, barX + SCROLLBAR_WIDTH, thumbY + thumbHeight, thumbColor);
    }

    /**
     * LMB on the scroll track. If the click lands on the thumb, drag starts at the click offset (so the thumb doesn't
     * jump). If it lands on the track elsewhere, the thumb jumps to that position (centered on the cursor) and drag
     * begins immediately, so the user can click-and-drag a long jump fluidly.
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !rectKnown || !canScroll()) {
            return false;
        }
        if (mouseX < trackX || mouseX >= trackX + SCROLLBAR_WIDTH) {
            return false;
        }
        if (mouseY < trackY || mouseY >= trackY + trackHeight) {
            return false;
        }

        var thumbHeight = computeThumbHeight();
        var thumbY = computeThumbY(thumbHeight);

        if (mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
            this.draggingThumb = true;
            this.dragOffsetWithinThumb = (float) (mouseY - thumbY);
        } else {
            this.draggingThumb = true;
            this.dragOffsetWithinThumb = thumbHeight / 2f;
            updateScrollFromMouseY(mouseY, thumbHeight);
        }
        return true;
    }

    /** While dragging, map cursor Y to scroll position. No-ops if no drag is in flight. */
    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (!draggingThumb || button != 0 || !rectKnown) {
            return false;
        }
        updateScrollFromMouseY(mouseY, computeThumbHeight());
        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0 || !draggingThumb) {
            return false;
        }
        this.draggingThumb = false;
        return true;
    }

    private int computeThumbHeight() {
        if (contentHeight <= 0 || trackHeight <= 0) {
            return 8;
        }
        return Math.max(8, (int) ((float) viewHeight / contentHeight * trackHeight));
    }

    private int computeThumbY(int thumbHeight) {
        var max = maxScroll();
        if (max == 0) {
            return trackY;
        }
        return trackY + Math.round(scrollY / max * (trackHeight - thumbHeight));
    }

    private void updateScrollFromMouseY(double mouseY, int thumbHeight) {
        var trackTravel = trackHeight - thumbHeight;
        if (trackTravel <= 0) {
            scrollY = 0;
            return;
        }
        var newThumbTop = mouseY - dragOffsetWithinThumb;
        var fraction = (newThumbTop - trackY) / trackTravel;
        fraction = Math.max(0.0, Math.min(1.0, fraction));
        scrollY = (float) (fraction * maxScroll());
    }
}
