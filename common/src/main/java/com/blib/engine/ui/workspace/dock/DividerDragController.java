package com.blib.engine.ui.workspace.dock;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.ui.dock.Orientation;
import com.blib.engine.ui.dock.Sizing;

/**
 * Owns the in-progress divider drag. The workspace screen calls into this once per mouse event; the controller mutates
 * the bound {@link Sizing} instance, so the next layout pass reflects the new partitioning. No screen state lives here
 * — the geometry needed for the drag is captured in the {@link DividerHit} passed to {@link #begin(DividerHit)}.
 */
@ApiStatus.Internal
public final class DividerDragController {

    /** Floor on any panel size during a divider drag (logical pixels). */
    public static final int MIN_PANEL_SIZE_PX = 24;

    private @Nullable DividerHit active;

    public boolean isActive() {
        return active != null;
    }

    public @Nullable DividerHit active() {
        return active;
    }

    public void begin(DividerHit hit) {
        this.active = hit;
    }

    public void end() {
        this.active = null;
    }

    /** Applies the drag to the bound sizing. Returns {@code true} when a drag was in progress. */
    public boolean apply(double mouseLogicalX, double mouseLogicalY) {
        var d = active;
        if (d == null) {
            return false;
        }
        if (d.split().orientation() == Orientation.HORIZONTAL) {
            var newBoundary = (int) Math.round(mouseLogicalX - d.parentX());
            var clamped = clamp(newBoundary, d.parentWidth());
            writeBoundary(d.split().sizing(), clamped, d.parentWidth());
        } else {
            var newBoundary = (int) Math.round(mouseLogicalY - d.parentY());
            var clamped = clamp(newBoundary, d.parentHeight());
            writeBoundary(d.split().sizing(), clamped, d.parentHeight());
        }
        return true;
    }

    private static int clamp(int value, int parentSize) {
        return Math.max(MIN_PANEL_SIZE_PX, Math.min(parentSize - MIN_PANEL_SIZE_PX, value));
    }

    private static void writeBoundary(Sizing sizing, int newBoundary, int parentSize) {
        switch (sizing) {
            case Sizing.Ratio r -> r.value = Math.max(0.01f, Math.min(0.99f, (float) newBoundary / parentSize));
            case Sizing.FirstFixed f -> f.pixels = newBoundary;
            case Sizing.SecondFixed s -> s.pixels = parentSize - newBoundary;
        }
    }
}
