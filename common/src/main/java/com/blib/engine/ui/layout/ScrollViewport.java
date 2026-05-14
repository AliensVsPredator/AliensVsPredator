package com.blib.engine.ui.layout;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.widget.ScrollContainer;

/**
 * Standard scrollable viewport for immediate-mode panels. It owns the {@link ScrollContainer}, applies raw clipping,
 * exposes the scrolled content origin, and reserves a stable gutter so rows and text never render underneath the
 * scrollbar.
 */
@ApiStatus.Internal
public final class ScrollViewport {

    private final ScrollContainer scroll = new ScrollContainer();

    private UiRect viewport = UiRect.of(0, 0, 0, 0);

    private boolean open;

    public void reset() {
        scroll.reset();
    }

    public void clear() {
        viewport = UiRect.of(0, 0, 0, 0);
        scroll.layout(0, 0);
        open = false;
    }

    public Frame begin(GuiGraphics graphics, UiRect nextViewport, int contentHeight) {
        this.viewport = nextViewport;
        scroll.layout(nextViewport.height(), contentHeight);
        open = true;
        PanelScissor.enable(graphics, nextViewport);
        return new Frame(nextViewport, (int) scroll.scrollY(), scroll.canScroll());
    }

    public void end(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!open) {
            return;
        }
        open = false;
        PanelScissor.disable(graphics);
        scroll.renderScrollbar(graphics, viewport.x(), viewport.y(), viewport.width(), viewport.height(), mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return scroll.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        return scroll.mouseDragged(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scroll.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (!viewport.contains(mouseX, mouseY)) {
            return false;
        }
        return scroll.mouseScrolled(scrollY);
    }

    public int scrollY() {
        return (int) scroll.scrollY();
    }

    public void scrollBy(float deltaPx) {
        scroll.scrollBy(deltaPx);
    }

    public record Frame(
        UiRect viewport,
        int scrollY,
        boolean canScroll
    ) {

        public int contentX() {
            return viewport.x();
        }

        public int contentY() {
            return viewport.y() - scrollY;
        }

        public int contentWidth() {
            return Math.max(0, viewport.width() - ScrollContainer.SCROLLBAR_GUTTER);
        }

        public UiRect visibleContentRect() {
            return new UiRect(viewport.x(), viewport.y(), contentWidth(), viewport.height());
        }
    }
}
