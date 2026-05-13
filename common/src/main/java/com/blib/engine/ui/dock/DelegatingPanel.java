package com.blib.engine.ui.dock;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base for {@link Panel} implementations that wrap other panels — currently just {@link TabbedPanel}. Owns the
 * input + lifecycle forwarding contract so each container doesn't repeat the same 7 method bodies.
 * <p>
 * <strong>Maintenance rule:</strong> any new method added to {@link Panel} or {@link PanelInput} that should forward to
 * the active child must be overridden here with a forwarding body. Without that, the default inherited from
 * {@code PanelInput} silently no-ops and the child never receives the call — the bug class the unified-forwarding base
 * was introduced to eliminate.
 * <p>
 * Subclasses provide two hooks:
 * <ul>
 * <li>{@link #activeChild()} — the child to forward input + tooltip to (may be {@code null}).</li>
 * <li>{@link #inContentArea(double, double)} — whether the cursor is in the child's content area (so clicks on chrome
 * like the tab strip don't get sent to the child).</li>
 * </ul>
 */
@ApiStatus.Internal
public abstract class DelegatingPanel implements Panel {

    /** The child panel that input events forward to. {@code null} when the container is empty. */
    protected abstract @Nullable Panel activeChild();

    /**
     * Whether the given coordinate is inside the child's content area. Chrome regions (tab strips, divider gutters)
     * return {@code false} so the container's own input state machine — not the child — handles those clicks.
     */
    protected abstract boolean inContentArea(double mouseX, double mouseY);

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!inContentArea(mouseX, mouseY)) {
            return false;
        }
        var child = activeChild();
        return child != null && child.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public final boolean mouseClickedCapture(double mouseX, double mouseY, int button) {
        if (!inContentArea(mouseX, mouseY)) {
            return false;
        }
        var child = activeChild();
        return child != null && child.mouseClickedCapture(mouseX, mouseY, button);
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!inContentArea(mouseX, mouseY)) {
            return false;
        }
        var child = activeChild();
        return child != null && child.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!inContentArea(mouseX, mouseY)) {
            return false;
        }
        var child = activeChild();
        return child != null && child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!inContentArea(mouseX, mouseY)) {
            return false;
        }
        var child = activeChild();
        return child != null && child.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public final boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        var child = activeChild();
        return child != null && child.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public final @Nullable Component tooltipText() {
        var child = activeChild();
        return child != null ? child.tooltipText() : null;
    }
}
