package com.blib.engine.ui.panel.base;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.layout.ScrollViewport;
import com.blib.engine.ui.layout.UiRect;

/**
 * Common base for the engine's many "scrollable list" panels (outliner, content browser, faction members, tag browser,
 * action stack, pool editor, etc.). Centralises the {@link ScrollViewport} ownership plus scroll, raw clipping, and
 * scrollbar input. Each implementer keeps its own row layout and render code; the base just makes the scaffolding
 * common.
 * <p>
 * Subclass contract:
 * <ul>
 * <li>{@link #contentHeight} reports total content height for the current frame's data.</li>
 * <li>{@link #renderRows} draws rows at screen-space content coordinates after the current scroll offset has already
 * been applied. This keeps child widget hit-rects in the same coordinate space as mouse input.</li>
 * </ul>
 * Mouse + keyboard inputs flow through normally; scroll wheel and scrollbar dragging are handled here. Subclasses can
 * still layer additional capture regions around the scrollbar.
 */
@ApiStatus.Internal
public abstract class ScrollableListPanel implements Panel {

    protected final ScrollViewport scrollbar = new ScrollViewport();

    /** Last rendered rect — captured at render time so input methods can hit-test against it. */
    private int lastX;

    private int lastY;

    private int lastWidth;

    private int lastHeight;

    protected final ScrollViewport scrollbar() {
        return scrollbar;
    }

    protected final int lastX() {
        return lastX;
    }

    protected final int lastY() {
        return lastY;
    }

    protected final int lastWidth() {
        return lastWidth;
    }

    protected final int lastHeight() {
        return lastHeight;
    }

    /** Total content height in pixels for this frame. Subclass recomputes per frame from its current data. */
    protected abstract int contentHeight();

    /**
     * Render rows at the given content coordinates. The base has already translated the pose stack so {@code y} is the
     * top of the visible area; rows below {@code y + height} are clipped by the active scissor. Mouse coords are
     * pre-translated to match (i.e. you can compare them directly to row positions).
     */
    protected abstract void renderRows(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height,
        int mouseX,
        int mouseY,
        float partialTick
    );

    @Override
    public final void render(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height,
        int mouseX,
        int mouseY,
        float partialTick
    ) {
        this.lastX = x;
        this.lastY = y;
        this.lastWidth = width;
        this.lastHeight = height;

        var frame = scrollbar.begin(graphics, UiRect.of(x, y, width, height), contentHeight());
        try {
            renderRows(graphics, frame.contentX(), frame.contentY(), frame.contentWidth(), height, mouseX, mouseY + frame.scrollY(), partialTick);
        } finally {
            scrollbar.end(graphics, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        return scrollbar.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return scrollbar.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scrollbar.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (
            mouseX >= lastX
                && mouseX < lastX + lastWidth
                && mouseY >= lastY
                && mouseY < lastY + lastHeight
        ) {
            return scrollbar.mouseScrolled(mouseX, mouseY, scrollY);
        }
        return false;
    }
}
