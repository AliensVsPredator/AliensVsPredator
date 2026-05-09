package com.blib.engine.jigsaw.placement;

import org.jetbrains.annotations.ApiStatus;

/**
 * Strategy selector for {@link PlacementResolver}. Each mode has a distinct way of turning a cursor position into a
 * concrete {@link Placement} — see {@code JigsawPiecePlacementPlan} for the spec mapping. Phase 2 implements only
 * {@link #FREE}; the other modes are wired through the enum so the UI affordances and packets that reference them exist
 * before the actual resolver implementations land.
 */
@ApiStatus.Internal
public enum PlacementMode {

    /** Anchor is the block on the face the cursor's clip ray hits — same behavior as the pre-resolver code. */
    FREE("Free"),

    /** Snap to a compatible jigsaw block in the world. Phase 3. */
    JIGSAW_SNAP("Jigsaw Snap"),

    /** Drop the structure onto the ground under the cursor. Phase 6. */
    SURFACE_SNAP("Surface Snap"),

    /** Quantize cursor position to a fixed grid. Phase 6. */
    GRID_SNAP("Grid Snap");

    private final String displayName;

    PlacementMode(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
