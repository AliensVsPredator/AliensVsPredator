package com.blib.engine.jigsaw.placement;

import org.jetbrains.annotations.ApiStatus;

/**
 * Mutable singleton holding placement-side user preferences that aren't tied to the active piece (which lives in
 * {@link com.blib.engine.jigsaw.JigsawPieceSelection}) or the active mode (which lives in {@link JigsawTool}). Phase 4
 * adds collision-handling policy; later phases extend this with replacement modes, RNG seed, generation depth, etc.
 * <p>
 * In-memory only — all preferences reset on workspace close so a fresh {@code /blib engine} session starts with sane
 * defaults. Per the engine's overall stance on persistence (see {@code JIGSAW_MANIPULATION_PLAN.md}), nothing
 * serializes to disk in the current phase set.
 */
@ApiStatus.Internal
public final class JigsawPlacementOptions {

    /**
     * What the placement pipeline does when the proposed placement would overwrite existing world blocks.
     * <ul>
     * <li>{@link #BLOCK} — refuse the click outright; the ghost still renders (red-tinted) so the user can see why it's
     * being blocked, but no packet is sent.</li>
     * <li>{@link #WARN} — allow the placement, but tint the ghost red and surface the count so the user has to
     * deliberately click through. Default — matches the spec's "warn but allow" middle ground.</li>
     * <li>{@link #ALLOW} — allow without visual emphasis. The count is still shown for informational purposes.</li>
     * </ul>
     */
    public enum CollisionPolicy {
        BLOCK,
        WARN,
        ALLOW
    }

    private static CollisionPolicy collisionPolicy = CollisionPolicy.WARN;

    /**
     * Block-grid quantization for {@link PlacementMode#GRID_SNAP}. Defaults to 16 = chunk-aligned, which is the common
     * authoring case ("place this on a chunk boundary"). Smaller values like 4 or 8 are useful for sub-chunk grids; 1
     * effectively disables snapping.
     */
    private static int gridSize = 16;

    private JigsawPlacementOptions() {}

    public static CollisionPolicy collisionPolicy() {
        return collisionPolicy;
    }

    public static void setCollisionPolicy(CollisionPolicy policy) {
        collisionPolicy = policy;
    }

    public static int gridSize() {
        return gridSize;
    }

    public static void setGridSize(int size) {
        gridSize = Math.max(1, size);
    }

    /** Reset all options to defaults. Called on workspace close. */
    public static void reset() {
        collisionPolicy = CollisionPolicy.WARN;
        gridSize = 16;
    }
}
