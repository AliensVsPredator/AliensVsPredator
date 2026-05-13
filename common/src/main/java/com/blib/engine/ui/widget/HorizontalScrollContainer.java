package com.blib.engine.ui.widget;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

/**
 * X-axis sibling of {@link ScrollContainer}. Stateful horizontal-scroll-position holder for a panel section that's
 * wider than its visible rect. Each frame the panel reports viewport width + content width; this class clamps the
 * offset and exposes it. The panel itself owns clipping (scissor) and translation — this class only owns position math
 * + the horizontal scrollbar render at the bottom edge.
 */
@ApiStatus.Internal
public final class HorizontalScrollContainer {

    private static final int SCROLLBAR_HEIGHT = 6;

    private static final int SCROLLBAR_TRACK_COLOR = 0x40000000;

    private static final int SCROLLBAR_THUMB_COLOR = 0xFF505058;

    private static final int SCROLLBAR_THUMB_HOVER_COLOR = 0xFF6868FF;

    private static final float SCROLL_PIXELS_PER_TICK = 18f;

    private float scrollX;

    private int viewWidth;

    private int contentWidth;

    private int trackX;

    private int trackY;

    private int trackWidth;

    private boolean rectKnown;

    private boolean draggingThumb;

    private float dragOffsetWithinThumb;

    public void layout(int viewWidth, int contentWidth) {
        this.viewWidth = Math.max(0, viewWidth);
        this.contentWidth = Math.max(0, contentWidth);

        var max = maxScroll();
        if (scrollX > max) {
            scrollX = max;
        }
        if (scrollX < 0) {
            scrollX = 0;
        }
    }

    public float scrollX() {
        return scrollX;
    }

    public int maxScroll() {
        return Math.max(0, contentWidth - viewWidth);
    }

    public boolean canScroll() {
        return contentWidth > viewWidth;
    }

    public void reset() {
        scrollX = 0;
    }

    public void scrollBy(float deltaPx) {
        var max = maxScroll();
        scrollX = Math.max(0, Math.min(max, scrollX + deltaPx));
    }

    /** Wheel-input handler: returns true if the scroll was consumed (i.e., we have content to scroll). */
    public boolean mouseScrolled(double scrollDx) {
        if (!canScroll()) {
            return false;
        }
        scrollBy((float) (-scrollDx * SCROLL_PIXELS_PER_TICK));
        return true;
    }

    /**
     * Renders a horizontal scrollbar along the bottom edge of the given rect. No-ops when the content fits. {@code
     * mouseX/Y} are passed for the hover highlight. Captures the track rect so click / drag handlers have something to
     * hit-test against.
     */
    public void renderScrollbar(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (!canScroll()) {
            this.rectKnown = false;
            this.draggingThumb = false;
            return;
        }

        var barY = y + height - SCROLLBAR_HEIGHT;
        this.trackX = x;
        this.trackY = barY;
        this.trackWidth = width;
        this.rectKnown = true;

        var hovered = mouseX >= x && mouseX < x + width && mouseY >= barY && mouseY < barY + SCROLLBAR_HEIGHT;

        graphics.fill(x, barY, x + width, barY + SCROLLBAR_HEIGHT, SCROLLBAR_TRACK_COLOR);

        var thumbWidth = computeThumbWidth();
        var thumbX = computeThumbX(thumbWidth);
        var thumbColor = (hovered || draggingThumb) ? SCROLLBAR_THUMB_HOVER_COLOR : SCROLLBAR_THUMB_COLOR;
        graphics.fill(thumbX, barY, thumbX + thumbWidth, barY + SCROLLBAR_HEIGHT, thumbColor);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !rectKnown || !canScroll()) {
            return false;
        }
        if (mouseY < trackY || mouseY >= trackY + SCROLLBAR_HEIGHT) {
            return false;
        }
        if (mouseX < trackX || mouseX >= trackX + trackWidth) {
            return false;
        }

        var thumbWidth = computeThumbWidth();
        var thumbX = computeThumbX(thumbWidth);

        if (mouseX >= thumbX && mouseX < thumbX + thumbWidth) {
            this.draggingThumb = true;
            this.dragOffsetWithinThumb = (float) (mouseX - thumbX);
        } else {
            this.draggingThumb = true;
            this.dragOffsetWithinThumb = thumbWidth / 2f;
            updateScrollFromMouseX(mouseX, thumbWidth);
        }
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (!draggingThumb || button != 0 || !rectKnown) {
            return false;
        }
        updateScrollFromMouseX(mouseX, computeThumbWidth());
        return true;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button != 0 || !draggingThumb) {
            return false;
        }
        this.draggingThumb = false;
        return true;
    }

    private int computeThumbWidth() {
        if (contentWidth <= 0 || trackWidth <= 0) {
            return 8;
        }
        return Math.max(8, (int) ((float) viewWidth / contentWidth * trackWidth));
    }

    private int computeThumbX(int thumbWidth) {
        var max = maxScroll();
        if (max == 0) {
            return trackX;
        }
        return trackX + Math.round(scrollX / max * (trackWidth - thumbWidth));
    }

    private void updateScrollFromMouseX(double mouseX, int thumbWidth) {
        var trackTravel = trackWidth - thumbWidth;
        if (trackTravel <= 0) {
            scrollX = 0;
            return;
        }
        var newThumbLeft = mouseX - dragOffsetWithinThumb;
        var fraction = (newThumbLeft - trackX) / trackTravel;
        fraction = Math.max(0.0, Math.min(1.0, fraction));
        scrollX = (float) (fraction * maxScroll());
    }
}
