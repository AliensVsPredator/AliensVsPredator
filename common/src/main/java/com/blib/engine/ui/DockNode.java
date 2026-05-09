package com.blib.engine.ui;

import org.jetbrains.annotations.ApiStatus;

/**
 * Binary tree describing how the workspace screen subdivides into panels. {@link Leaf} holds a single {@link Panel};
 * {@link Split} divides its rectangle along an {@link Orientation} between two children, with the boundary placed by a
 * {@link Sizing} (ratio for resizable regions, fixed pixels for menu bars / status bars / sidebars).
 */
@ApiStatus.Internal
public sealed interface DockNode permits DockNode.Leaf, DockNode.Split {

    record Leaf(Panel panel) implements DockNode {}

    record Split(
        Orientation orientation,
        DockNode first,
        DockNode second,
        Sizing sizing
    ) implements DockNode {}

    /**
     * Computes the boundary offset (along the split's primary axis) between the first and second child given the total
     * size available. Reads {@link Sizing} live so resize drags that mutate the sizing instance take effect on the next
     * layout pass.
     */
    static int boundary(Sizing sizing, int total) {
        return switch (sizing) {
            case Sizing.Ratio r -> Math.max(0, Math.min(total, Math.round(total * r.value)));
            case Sizing.FirstFixed f -> Math.max(0, Math.min(total, f.pixels));
            case Sizing.SecondFixed s -> Math.max(0, total - Math.min(total, s.pixels));
        };
    }
}
