package com.blib.engine.ui.workspace.dock;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.TabbedPanel;

/**
 * Pure transformations on a {@link DockNode} tree.
 */
@ApiStatus.Internal
public final class DockTreeMutator {

    private DockTreeMutator() {}

    /**
     * Returns a structurally-simplified copy of {@code node}: any {@link DockNode.Split} where one or both children are
     * empty tabbed-panel leaves collapses to the non-empty side (or to the first side if both are empty). Used after a
     * tab close so a panel that lost its last tab doesn't leave an empty rectangle hanging in the workspace.
     */
    public static DockNode simplify(DockNode node) {
        if (!(node instanceof DockNode.Split split)) {
            return node;
        }
        var first = simplify(split.first());
        var second = simplify(split.second());
        var firstEmpty = isEmptyTabbedPanel(first);
        var secondEmpty = isEmptyTabbedPanel(second);

        if (firstEmpty && !secondEmpty) {
            return second;
        }
        if (secondEmpty && !firstEmpty) {
            return first;
        }
        if (firstEmpty && secondEmpty) {
            return first;
        }
        if (first == split.first() && second == split.second()) {
            return split;
        }
        return new DockNode.Split(split.orientation(), first, second, split.sizing());
    }

    public static boolean isEmptyTabbedPanel(DockNode node) {
        return node instanceof DockNode.Leaf leaf
            && leaf.panel() instanceof TabbedPanel tp
            && tp.tabCount() == 0;
    }
}
