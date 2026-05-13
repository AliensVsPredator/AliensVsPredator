package com.blib.engine.ui.workspace.dock;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.ui.EngineFont;
import com.blib.engine.ui.dock.DockNode;
import com.blib.engine.ui.dock.Orientation;
import com.blib.engine.ui.dock.Panel;
import com.blib.engine.ui.dock.Sizing;
import com.blib.engine.ui.dock.TabbedPanel;

/**
 * State and behaviour for tab-drag gestures inside the workspace. A press on a tab begins a drag; once the cursor moves
 * past a caller-defined threshold the drag goes "active" and the overlay renders a ghost + drop highlights. Release
 * commits the drop or cancels.
 * <p>
 * Owns the drop placement logic too — split / reorder / merge semantics, drop-zone math, overlay rendering. The screen
 * passes the current dock root in and receives back the (possibly new) root, so the controller doesn't reach into
 * screen state.
 */
@ApiStatus.Internal
public final class TabDragController {

    private static final int TAB_GHOST_BG_COLOR = 0xCC2C2C32;

    private static final int TAB_GHOST_TEXT_COLOR = 0xFFE0E0E0;

    private static final int TAB_DROP_TARGET_COLOR = 0x404F8FFF;

    /**
     * Where a drop happens inside a target {@link TabbedPanel}'s content rect. {@link #CENTER} is a tab merge; the four
     * edge values become 50/50 splits with the dragged tab on the named side.
     */
    public enum DropZone {
        CENTER,
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

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
        if (dx * dx + dy * dy > thresholdSq) {
            d.active = true;
            return true;
        }
        return false;
    }

    public void cancel() {
        this.drag = null;
    }

    /**
     * Render the drop-target highlight and the cursor-following tab ghost. No-op when no drag is active.
     */
    public void renderOverlay(GuiGraphics graphics, int mouseX, int mouseY, DockNode root, int logicalWidth, int logicalHeight) {
        var d = drag;
        if (d == null || !d.active) {
            return;
        }

        // Highlight the drop target — strip area for tab merges, half-rect for edge splits, full content for
        // center-zone merges.
        var target = DockTreeHitTest.findTabbedPanelAt(root, logicalWidth, logicalHeight, mouseX, mouseY);
        if (target != null) {
            if (target.isInTabStrip(mouseX, mouseY)) {
                graphics.fill(
                    target.rectX(),
                    target.rectY(),
                    target.rectX() + target.rectWidth(),
                    target.rectY() + TabbedPanel.TAB_BAR_HEIGHT,
                    TAB_DROP_TARGET_COLOR
                );
            } else {
                renderDropZoneOverlay(graphics, target, computeDropZoneInContent(target, mouseX, mouseY));
            }
        }

        // Ghost: a translucent tab-shaped chip floating with the cursor.
        var font = EngineFont.get();
        var label = d.tab.title();
        var labelWidth = font.width(label);
        var w = labelWidth + 12;
        var h = TabbedPanel.TAB_BAR_HEIGHT;
        var x = mouseX - w / 2;
        var y = mouseY - h / 2;
        graphics.fill(x, y, x + w, y + h, TAB_GHOST_BG_COLOR);
        // +2 compensates for MC font's descender padding so the tab-drag ghost label visually centers; see
        // MenuBarPanel for the full rationale.
        graphics.drawString(font, Component.literal(label), x + 6, y + (h - font.lineHeight + 2) / 2, TAB_GHOST_TEXT_COLOR, false);
    }

    /**
     * Commit the drop at the given cursor position. Returns the resulting dock root — unchanged for merges (the source
     * and target tabbed-panels mutate in place), replaced for edge splits.
     */
    public DockNode completeDrop(double logicalX, double logicalY, DockNode root, int logicalWidth, int logicalHeight) {
        var d = drag;
        if (d == null) {
            return root;
        }
        var target = DockTreeHitTest.findTabbedPanelAt(root, logicalWidth, logicalHeight, (int) logicalX, (int) logicalY);
        if (target == null) {
            return root;
        }
        var zone = target.isInTabStrip(logicalX, logicalY)
            ? DropZone.CENTER
            : computeDropZoneInContent(target, logicalX, logicalY);

        if (zone == DropZone.CENTER) {
            mergeTab(d, target, logicalX);
            return root;
        }

        d.source.removeTab(d.sourceIndex);
        return splitPanel(root, target, d.tab, zone);
    }

    private static void mergeTab(Drag drag, TabbedPanel target, double logicalX) {
        if (target == drag.source) {
            var dropIdx = target.dropInsertionIndex(logicalX);
            if (dropIdx == drag.sourceIndex || dropIdx == drag.sourceIndex + 1) {
                return;
            }
            target.removeTab(drag.sourceIndex);
            if (dropIdx > drag.sourceIndex) {
                dropIdx--;
            }
            target.insertTab(dropIdx, drag.tab);
        } else {
            drag.source.removeTab(drag.sourceIndex);
            var dropIdx = target.dropInsertionIndex(logicalX);
            target.insertTab(dropIdx, drag.tab);
        }
    }

    /**
     * Replace {@code target}'s leaf in the dock tree with a fresh {@link DockNode.Split} containing two leaves: the
     * original target panel on one side, and a new {@link TabbedPanel} holding {@code droppedTab} on the other. Side
     * determined by {@code zone}; default 50/50 ratio.
     */
    private static DockNode splitPanel(DockNode root, TabbedPanel target, Panel droppedTab, DropZone zone) {
        var existingLeaf = new DockNode.Leaf(target);
        var newLeaf = new DockNode.Leaf(new TabbedPanel(droppedTab));
        var sizing = new Sizing.Ratio(0.5f);

        var newSplit = switch (zone) {
            case TOP -> new DockNode.Split(Orientation.VERTICAL, newLeaf, existingLeaf, sizing);
            case BOTTOM -> new DockNode.Split(Orientation.VERTICAL, existingLeaf, newLeaf, sizing);
            case LEFT -> new DockNode.Split(Orientation.HORIZONTAL, newLeaf, existingLeaf, sizing);
            case RIGHT -> new DockNode.Split(Orientation.HORIZONTAL, existingLeaf, newLeaf, sizing);
            case CENTER -> throw new IllegalStateException("CENTER is not a split zone");
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

    /**
     * Map the cursor's position over a {@link TabbedPanel}'s content area (excluding the tab strip) to a
     * {@link DropZone}. The middle 50% × 50% of the content rect is the {@code CENTER} (tab-merge) zone; outside that
     * inner rect, the closest edge defines the split direction.
     */
    private static DropZone computeDropZoneInContent(TabbedPanel target, double mouseX, double mouseY) {
        var contentY = target.rectY() + TabbedPanel.TAB_BAR_HEIGHT;
        var contentH = Math.max(1, target.rectHeight() - TabbedPanel.TAB_BAR_HEIGHT);
        var contentW = Math.max(1, target.rectWidth());

        var relX = (mouseX - target.rectX()) / contentW;
        var relY = (mouseY - contentY) / contentH;

        if (relX > 0.25 && relX < 0.75 && relY > 0.25 && relY < 0.75) {
            return DropZone.CENTER;
        }

        var distLeft = relX;
        var distRight = 1.0 - relX;
        var distTop = relY;
        var distBottom = 1.0 - relY;
        var minDist = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

        if (minDist == distLeft) {
            return DropZone.LEFT;
        }
        if (minDist == distRight) {
            return DropZone.RIGHT;
        }
        if (minDist == distTop) {
            return DropZone.TOP;
        }
        return DropZone.BOTTOM;
    }

    private static void renderDropZoneOverlay(GuiGraphics graphics, TabbedPanel target, DropZone zone) {
        var x = target.rectX();
        var w = target.rectWidth();
        var contentY = target.rectY() + TabbedPanel.TAB_BAR_HEIGHT;
        var contentH = Math.max(0, target.rectHeight() - TabbedPanel.TAB_BAR_HEIGHT);

        var x0 = x;
        var y0 = contentY;
        var x1 = x + w;
        var y1 = contentY + contentH;
        switch (zone) {
            case CENTER -> {
                // full content rect
            }
            case TOP -> y1 = contentY + contentH / 2;
            case BOTTOM -> y0 = contentY + contentH / 2;
            case LEFT -> x1 = x + w / 2;
            case RIGHT -> x0 = x + w / 2;
        }
        graphics.fill(x0, y0, x1, y1, TAB_DROP_TARGET_COLOR);
    }
}
