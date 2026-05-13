package com.blib.engine.ui.workspace.dock;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.dock.TabbedPanel;

/**
 * Mutable tab-drag state. Created on a tab press; promoted to {@link Drag#active} once the cursor moves past a
 * caller-defined threshold; cleared on release or successful drop. The tab itself stays in {@link Drag#source} during
 * the drag — only on a successful drop does the source actually lose it.
 * <p>
 * Higher-level concerns (the workspace screen) still own tab-drop placement math (split / reorder semantics), drop-zone
 * computation, and overlay rendering — those reach into screen-local layout state. This controller exists so the
 * <em>state</em> of the drag (source / index / payload / cursor) is no longer entangled with the rest of the screen.
 */
@ApiStatus.Internal
public final class TabDragController {

    public static final class Drag {

        public final TabbedPanel source;

        public final int sourceIndex;

        public final Panel tab;

        public final double startX;

        public final double startY;

        public boolean active;

        public Drag(TabbedPanel source, int sourceIndex, Panel tab, double startX, double startY) {
            this.source = source;
            this.sourceIndex = sourceIndex;
            this.tab = tab;
            this.startX = startX;
            this.startY = startY;
        }
    }

    private @Nullable Drag drag;

    public boolean isPending() {
        return drag != null;
    }

    public boolean isActive() {
        return drag != null && drag.active;
    }

    public @Nullable Drag drag() {
        return drag;
    }

    public void begin(TabbedPanel source, int sourceIndex, Panel tab, double startX, double startY) {
        this.drag = new Drag(source, sourceIndex, tab, startX, startY);
    }

    /** Promote the pending press to an active drag once cursor motion exceeds the threshold. */
    public boolean promoteIfFarEnough(double cursorX, double cursorY, double thresholdSq) {
        var d = drag;
        if (d == null || d.active) {
            return false;
        }
        var dx = cursorX - d.startX;
        var dy = cursorY - d.startY;
        if (dx * dx + dy * dy >= thresholdSq) {
            d.active = true;
            return true;
        }
        return false;
    }

    public void cancel() {
        this.drag = null;
    }
}
