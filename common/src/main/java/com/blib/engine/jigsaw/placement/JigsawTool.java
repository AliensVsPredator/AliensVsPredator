package com.blib.engine.jigsaw.placement;

import org.jetbrains.annotations.ApiStatus;

/**
 * Mutable singleton holding the user's active {@link PlacementMode}. Sibling of
 * {@link com.blib.engine.jigsaw.JigsawPieceSelection} — selection answers "which piece," tool answers "how to place
 * it."
 * <p>
 * Defaults to {@link PlacementMode#FREE} so the workspace's first interaction with placement matches the legacy "click
 * anywhere → place at cursor" behavior; later phases add UI for switching modes (toolbar segment, hotkey, or inspector
 * dropdown — TBD).
 */
@ApiStatus.Internal
public final class JigsawTool {

    private static PlacementMode activeMode = PlacementMode.FREE;

    private JigsawTool() {}

    public static PlacementMode activeMode() {
        return activeMode;
    }

    public static void setActiveMode(PlacementMode mode) {
        activeMode = mode;
    }

    /** Returns the resolver implementation for the active mode. */
    public static PlacementResolver activeResolver() {
        return switch (activeMode) {
            case FREE -> FreePlacementResolver.INSTANCE;
            case JIGSAW_SNAP -> JigsawSnapResolver.INSTANCE;
            case SURFACE_SNAP -> SurfaceSnapResolver.INSTANCE;
            case GRID_SNAP -> GridSnapResolver.INSTANCE;
        };
    }

    /** Cycle to the next placement mode in declaration order. Wraps around from the last back to {@code FREE}. */
    public static void cycleNextImplementedMode() {
        var values = PlacementMode.values();
        activeMode = values[(activeMode.ordinal() + 1) % values.length];
    }
}
