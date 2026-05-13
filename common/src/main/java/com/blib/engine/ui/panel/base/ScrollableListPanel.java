package com.blib.engine.ui.panel.base;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.widget.ScrollContainer;

/**
 * Common base for the engine's many "scrollable list" panels (outliner, content browser, faction members, tag browser,
 * action stack, pool editor, etc.). Centralises the {@link ScrollContainer} ownership + the scroll/clip/translate
 * pattern those panels currently repeat individually. Each implementer keeps its own row layout and render code; the
 * base just makes the scaffolding common.
 * <p>
 * Subclass contract:
 * <ul>
 * <li>{@link #contentHeight} reports total content height for the current frame's data.</li>
 * <li>{@link #renderRows} draws rows at content-coordinate {@code y} (i.e. before the scroll translate). The base
 * translates the pose stack so {@code y=0} is the top of the panel's visible area at the current scroll offset.</li>
 * </ul>
 * Mouse + keyboard inputs flow through normally; only {@link #mouseScrolled} is intercepted to drive the scroll
 * container. Panels that also want to forward scrollbar clicks/drags should expose their own
 * {@link #mouseClickedCapture} / {@link #mouseDragged} / {@link #mouseReleased} overrides; the base provides hooks for
 * doing this against {@link #scrollbar()} but does not bind them by default (panels often have additional capture
 * regions like splitters or thumb drags they need to coordinate).
 */
@ApiStatus.Internal
public abstract class ScrollableListPanel implements Panel {

    protected final ScrollContainer scrollbar = new ScrollContainer();

    /** Last rendered rect — captured at render time so input methods can hit-test against it. */
    private int lastX;

    private int lastY;

    private int lastWidth;

    private int lastHeight;

    protected final ScrollContainer scrollbar() {
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

        scrollbar.layout(height, contentHeight());
        var scrollY = (int) scrollbar.scrollY();

        graphics.enableScissor(x, y, x + width, y + height);
        graphics.pose().pushPose();
        graphics.pose().translate(0, -scrollY, 0);
        renderRows(graphics, x, y, width, height, mouseX, mouseY + scrollY, partialTick);
        graphics.pose().popPose();
        graphics.disableScissor();

        scrollbar.renderScrollbar(graphics, x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (
            mouseX >= lastX
                && mouseX < lastX + lastWidth
                && mouseY >= lastY
                && mouseY < lastY + lastHeight
        ) {
            return scrollbar.mouseScrolled(scrollY);
        }
        return false;
    }
}
