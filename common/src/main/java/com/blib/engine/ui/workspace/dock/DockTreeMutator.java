package com.blib.engine.ui.workspace.dock;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Orientation;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.dock.Sizing;
import com.blib.engine.ui.dock.TabbedPanel;

/**
 * Pure transformations on a {@link DockNode} tree.
 */
@ApiStatus.Internal
public final class DockTreeMutator {

    public enum SplitSide {
        LEFT,
        RIGHT,
        ABOVE,
        BELOW
    }

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

    /**
     * Move one tab out of {@code source} and split {@code target}'s leaf with a new tabbed panel holding that tab.
     * Same-panel moves require at least one tab left behind; otherwise the simplifier would immediately collapse the
     * empty original side and the operation would appear to do nothing.
     */
    public static DockNode moveTabToSplit(
        DockNode root,
        TabbedPanel source,
        int sourceIndex,
        TabbedPanel target,
        SplitSide side
    ) {
        if (sourceIndex < 0 || sourceIndex >= source.tabCount()) {
            return root;
        }
        if (source == target && source.tabCount() <= 1) {
            return root;
        }
        var tab = source.tabs().get(sourceIndex);
        source.removeTab(sourceIndex);
        return splitWithTab(root, target, tab, side);
    }

    private static DockNode splitWithTab(DockNode root, TabbedPanel target, Panel tab, SplitSide side) {
        var existingLeaf = new DockNode.Leaf(target);
        var newLeaf = new DockNode.Leaf(new TabbedPanel(tab));
        var sizing = new Sizing.Ratio(0.5f);

        var newSplit = switch (side) {
            case ABOVE -> new DockNode.Split(Orientation.VERTICAL, newLeaf, existingLeaf, sizing);
            case BELOW -> new DockNode.Split(Orientation.VERTICAL, existingLeaf, newLeaf, sizing);
            case LEFT -> new DockNode.Split(Orientation.HORIZONTAL, newLeaf, existingLeaf, sizing);
            case RIGHT -> new DockNode.Split(Orientation.HORIZONTAL, existingLeaf, newLeaf, sizing);
        };

        return replaceTabbedPanel(root, target, newSplit);
    }

    private static DockNode replaceTabbedPanel(DockNode node, TabbedPanel target, DockNode replacement) {
        if (node instanceof DockNode.Leaf leaf && leaf.panel() == target) {
            return replacement;
        }
        if (node instanceof DockNode.Split split) {
            var first = replaceTabbedPanel(split.first(), target, replacement);
            var second = replaceTabbedPanel(split.second(), target, replacement);
            if (first == split.first() && second == split.second()) {
                return split;
            }
            return new DockNode.Split(split.orientation(), first, second, split.sizing());
        }
        return node;
    }
}
