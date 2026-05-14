package com.blib.engine.ui.layout;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.widget.HorizontalScrollContainer;
import com.blib.engine.ui.widget.ScrollContainer;

/**
 * Standard scrollable viewport for immediate-mode panels. It owns scroll containers, applies raw clipping, exposes the
 * scrolled content origin, and reserves stable gutters so rows and text never render underneath scrollbars.
 */
@ApiStatus.Internal
public final class ScrollViewport {

    private static final int SCROLLBAR_CORNER_COLOR = 0x40000000;

    private final ScrollContainer scrollY = new ScrollContainer();

    private final HorizontalScrollContainer scrollX = new HorizontalScrollContainer();

    private UiRect viewport = UiRect.of(0, 0, 0, 0);

    private UiRect contentViewport = UiRect.of(0, 0, 0, 0);

    private boolean open;

    public void reset() {
        scrollY.reset();
        scrollX.reset();
    }

    public void clear() {
        viewport = UiRect.of(0, 0, 0, 0);
        contentViewport = UiRect.of(0, 0, 0, 0);
        scrollY.layout(0, 0);
        scrollX.layout(0, 0);
        open = false;
    }

    public Frame begin(GuiGraphics graphics, UiRect nextViewport, int contentHeight) {
        return beginInternal(graphics, nextViewport, Math.max(0, nextViewport.width() - ScrollContainer.SCROLLBAR_GUTTER), contentHeight, false);
    }

    public Frame begin(GuiGraphics graphics, UiRect nextViewport, int contentWidth, int contentHeight) {
        return beginInternal(graphics, nextViewport, contentWidth, contentHeight, true);
    }

    private Frame beginInternal(GuiGraphics graphics, UiRect nextViewport, int contentWidth, int contentHeight, boolean allowHorizontalScroll) {
        this.viewport = nextViewport;
        var logicalContentWidth = Math.max(0, contentWidth);
        var logicalContentHeight = Math.max(0, contentHeight);

        if (allowHorizontalScroll) {
            contentViewport = computeContentViewport(nextViewport, logicalContentWidth, logicalContentHeight);
            scrollX.layout(contentViewport.width(), logicalContentWidth);
        } else {
            contentViewport = nextViewport;
            scrollX.layout(0, 0);
        }
        scrollY.layout(contentViewport.height(), logicalContentHeight);
        open = true;
        PanelScissor.enable(graphics, contentViewport);
        return new Frame(
            nextViewport,
            contentViewport,
            logicalContentWidth,
            (int) scrollX.scrollX(),
            (int) scrollY.scrollY(),
            scrollX.canScroll(),
            scrollY.canScroll()
        );
    }

    public void end(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!open) {
            return;
        }
        open = false;
        PanelScissor.disable(graphics);

        var verticalTrackHeight = Math.max(0, viewport.height() - (scrollX.canScroll() ? ScrollContainer.SCROLLBAR_GUTTER : 0));
        var horizontalTrackWidth = Math.max(0, viewport.width() - (scrollY.canScroll() ? ScrollContainer.SCROLLBAR_GUTTER : 0));
        scrollY.renderScrollbar(graphics, viewport.x(), viewport.y(), viewport.width(), verticalTrackHeight, mouseX, mouseY);
        scrollX.renderScrollbar(graphics, viewport.x(), viewport.y(), horizontalTrackWidth, viewport.height(), mouseX, mouseY);
        if (scrollX.canScroll() && scrollY.canScroll()) {
            graphics.fill(
                viewport.right() - ScrollContainer.SCROLLBAR_GUTTER,
                viewport.bottom() - ScrollContainer.SCROLLBAR_GUTTER,
                viewport.right(),
                viewport.bottom(),
                SCROLLBAR_CORNER_COLOR
            );
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return scrollY.mouseClicked(mouseX, mouseY, button) || scrollX.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        var vertical = scrollY.mouseDragged(mouseX, mouseY, button);
        var horizontal = scrollX.mouseDragged(mouseX, mouseY, button);
        return vertical || horizontal;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var vertical = scrollY.mouseReleased(mouseX, mouseY, button);
        var horizontal = scrollX.mouseReleased(mouseX, mouseY, button);
        return vertical || horizontal;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (!viewport.contains(mouseX, mouseY)) {
            return false;
        }
        return this.scrollY.mouseScrolled(scrollY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!viewport.contains(mouseX, mouseY)) {
            return false;
        }

        var consumed = false;
        if (scrollX != 0.0D) {
            consumed |= this.scrollX.mouseScrolled(scrollX);
        }
        if (scrollY != 0.0D) {
            if (Screen.hasShiftDown()) {
                var horizontal = this.scrollX.mouseScrolled(scrollY);
                consumed |= horizontal;
                if (!horizontal) {
                    consumed |= this.scrollY.mouseScrolled(scrollY);
                }
            } else {
                consumed |= this.scrollY.mouseScrolled(scrollY);
                if (!consumed) {
                    consumed |= this.scrollX.mouseScrolled(scrollY);
                }
            }
        }
        return consumed;
    }

    public int scrollY() {
        return (int) scrollY.scrollY();
    }

    public int scrollX() {
        return (int) scrollX.scrollX();
    }

    public void scrollBy(float deltaPx) {
        scrollY.scrollBy(deltaPx);
    }

    public void scrollByX(float deltaPx) {
        scrollX.scrollBy(deltaPx);
    }

    public boolean containsVisibleContent(double mouseX, double mouseY) {
        return contentViewport.contains(mouseX, mouseY);
    }

    private static UiRect computeContentViewport(UiRect viewport, int contentWidth, int contentHeight) {
        var needsHorizontal = contentWidth > viewport.width();
        var needsVertical = contentHeight > viewport.height();
        for (var i = 0; i < 2; i++) {
            var viewWidth = Math.max(0, viewport.width() - (needsVertical ? ScrollContainer.SCROLLBAR_GUTTER : 0));
            var viewHeight = Math.max(0, viewport.height() - (needsHorizontal ? ScrollContainer.SCROLLBAR_GUTTER : 0));
            needsHorizontal = contentWidth > viewWidth;
            needsVertical = contentHeight > viewHeight;
        }
        return UiRect.of(
            viewport.x(),
            viewport.y(),
            viewport.width() - (needsVertical ? ScrollContainer.SCROLLBAR_GUTTER : 0),
            viewport.height() - (needsHorizontal ? ScrollContainer.SCROLLBAR_GUTTER : 0)
        );
    }

    public record Frame(
        UiRect viewport,
        UiRect contentViewport,
        int contentWidth,
        int scrollX,
        int scrollY,
        boolean canScrollX,
        boolean canScrollY
    ) {

        public int contentX() {
            return contentViewport.x() - scrollX;
        }

        public int contentY() {
            return contentViewport.y() - scrollY;
        }

        public int contentWidth() {
            return contentWidth;
        }

        public boolean canScroll() {
            return canScrollY;
        }

        public UiRect visibleContentRect() {
            return contentViewport;
        }
    }
}
