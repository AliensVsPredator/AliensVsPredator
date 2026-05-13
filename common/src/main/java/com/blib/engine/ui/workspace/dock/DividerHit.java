package com.blib.engine.ui.workspace.dock;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Orientation;

/**
 * Geometry of a hovered or in-progress divider drag. The split is the dock-tree node whose boundary the user is
 * grabbing; the {@code parent*} fields are the rect that node occupied at the time of the hit so the drag math can
 * compute the new local boundary independently of layout state changes during the drag.
 */
@ApiStatus.Internal
public record DividerHit(
    DockNode.Split split,
    int parentX,
    int parentY,
    int parentWidth,
    int parentHeight
) {

    public int boundaryStartX() {
        if (split.orientation() == Orientation.HORIZONTAL) {
            return parentX + DockNode.boundary(split.sizing(), parentWidth);
        }
        return parentX;
    }

    public int boundaryStartY() {
        if (split.orientation() == Orientation.VERTICAL) {
            return parentY + DockNode.boundary(split.sizing(), parentHeight);
        }
        return parentY;
    }
}
