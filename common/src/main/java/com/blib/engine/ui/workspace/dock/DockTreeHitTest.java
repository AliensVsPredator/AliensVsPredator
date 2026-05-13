package com.blib.engine.ui.workspace.dock;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Orientation;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.dock.TabbedPanel;

/**
 * Pure hit-test queries against a {@link DockNode} tree. No state, no screen reference — callers pass in the tree and
 * cursor coordinates, the methods return a hit (or null) for the queried element.
 */
@ApiStatus.Internal
public final class DockTreeHitTest {

    private DockTreeHitTest() {}

    /**
     * Returns the divider hit at {@code (mouseX, mouseY)} given the dock tree rooted at {@code node} occupying
     * {@code (x, y, width, height)}, or {@code null} when no resizable divider lies within {@code hitPx} of the cursor.
     * Trim splits (those bordering a {@link Panel#isTrim()} leaf — typically menu bar / status bar) are skipped so
     * their immutable boundaries don't claim the hover.
     */
    public static @Nullable DividerHit findDivider(
        DockNode node,
        int x,
        int y,
        int width,
        int height,
        int mouseX,
        int mouseY,
        int hitPx
    ) {
        if (!(node instanceof DockNode.Split split)) {
            return null;
        }

        var resizable = isResizable(split);

        if (split.orientation() == Orientation.HORIZONTAL) {
            var firstWidth = DockNode.boundary(split.sizing(), width);
            var boundaryX = x + firstWidth;
            if (resizable && Math.abs(mouseX - boundaryX) <= hitPx && mouseY >= y && mouseY < y + height) {
                return new DividerHit(split, x, y, width, height);
            }
            var inFirst = findDivider(split.first(), x, y, firstWidth, height, mouseX, mouseY, hitPx);
            if (inFirst != null) {
                return inFirst;
            }
            return findDivider(split.second(), x + firstWidth, y, width - firstWidth, height, mouseX, mouseY, hitPx);
        }
        var firstHeight = DockNode.boundary(split.sizing(), height);
        var boundaryY = y + firstHeight;
        if (resizable && Math.abs(mouseY - boundaryY) <= hitPx && mouseX >= x && mouseX < x + width) {
            return new DividerHit(split, x, y, width, height);
        }
        var inFirst = findDivider(split.first(), x, y, width, firstHeight, mouseX, mouseY, hitPx);
        if (inFirst != null) {
            return inFirst;
        }
        return findDivider(split.second(), x, y + firstHeight, width, height - firstHeight, mouseX, mouseY, hitPx);
    }

    /**
     * Returns the leaf panel under {@code (mouseX, mouseY)} or {@code null} when the cursor is outside the tree's
     * bounds. The returned panel is whatever the leaf wraps — typically a {@link TabbedPanel} for content regions and a
     * raw panel for trim leaves.
     */
    public static @Nullable Panel panelAt(
        DockNode node,
        int x,
        int y,
        int width,
        int height,
        double mouseX,
        double mouseY
    ) {
        return switch (node) {
            case DockNode.Leaf leaf -> {
                if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height) {
                    yield leaf.panel();
                }
                yield null;
            }
            case DockNode.Split split -> {
                if (split.orientation() == Orientation.HORIZONTAL) {
                    var firstWidth = DockNode.boundary(split.sizing(), width);
                    if (mouseX < x + firstWidth) {
                        yield panelAt(split.first(), x, y, firstWidth, height, mouseX, mouseY);
                    }
                    yield panelAt(split.second(), x + firstWidth, y, width - firstWidth, height, mouseX, mouseY);
                } else {
                    var firstHeight = DockNode.boundary(split.sizing(), height);
                    if (mouseY < y + firstHeight) {
                        yield panelAt(split.first(), x, y, width, firstHeight, mouseX, mouseY);
                    }
                    yield panelAt(split.second(), x, y + firstHeight, width, height - firstHeight, mouseX, mouseY);
                }
            }
        };
    }

    /**
     * Convenience over {@link #panelAt}: returns the tabbed panel at the cursor or {@code null} if the leaf under the
     * cursor isn't a {@link TabbedPanel}.
     */
    public static @Nullable TabbedPanel findTabbedPanelAt(
        DockNode root,
        int width,
        int height,
        int mouseX,
        int mouseY
    ) {
        var leaf = panelAt(root, 0, 0, width, height, mouseX, mouseY);
        return leaf instanceof TabbedPanel tp ? tp : null;
    }

    private static boolean isResizable(DockNode.Split split) {
        return !isTrimLeaf(split.first()) && !isTrimLeaf(split.second());
    }

    private static boolean isTrimLeaf(DockNode node) {
        return node instanceof DockNode.Leaf leaf && leaf.panel().isTrim();
    }
}
