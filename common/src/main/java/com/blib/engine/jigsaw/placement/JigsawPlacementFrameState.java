package com.blib.engine.jigsaw.placement;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Transient per-frame state written by {@link com.blib.engine.render.jigsaw.JigsawPlacementWorldRenderer} and read by
 * downstream consumers (status-bar overlay, click handler) so they don't have to redo the resolver + collision-scan
 * work. Refreshed every frame the world renders; cleared on workspace close.
 * <p>
 * Reading is "best effort, last frame's data" — screen render runs immediately after world render in MC's pipeline, so
 * consumers see fresh state with at most one frame of staleness. Click events arriving between frames see whatever the
 * last world render produced, which is fine for an editor: the user moves the cursor, world renders, state updates;
 * click happens, state still describes what they were looking at.
 */
@ApiStatus.Internal
public final class JigsawPlacementFrameState {

    private static @Nullable Placement placement;

    private static @Nullable JigsawBlockTarget snapAnchor;

    private static int collisionCount;

    private JigsawPlacementFrameState() {}

    public static void update(@Nullable Placement p, @Nullable JigsawBlockTarget anchor, int count) {
        placement = p;
        snapAnchor = anchor;
        collisionCount = count;
    }

    /** Wipe state — used on workspace close to avoid carrying stale data into the next session. */
    public static void clear() {
        placement = null;
        snapAnchor = null;
        collisionCount = 0;
    }

    public static @Nullable Placement placement() {
        return placement;
    }

    public static @Nullable JigsawBlockTarget snapAnchor() {
        return snapAnchor;
    }

    public static int collisionCount() {
        return collisionCount;
    }
}
