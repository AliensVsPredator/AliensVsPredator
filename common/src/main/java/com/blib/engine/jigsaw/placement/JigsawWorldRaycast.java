package com.blib.engine.jigsaw.placement;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.session.EngineSession;

/**
 * Helper that finds the jigsaw block under the cursor, if any, and snapshots it as a {@link JigsawBlockTarget}. Reuses
 * {@link JigsawPlacementCursor#clipFromCursor} so the raycast geometry is exactly the same one
 * {@code FreePlacementResolver} uses — no risk of "the FREE-mode preview hits block X but the SNAP-mode raycast hits
 * block Y" inconsistencies. The actual block→target read is shared with the selection inspector via
 * {@link JigsawBlockTarget#snapshot}.
 */
@ApiStatus.Internal
public final class JigsawWorldRaycast {

    private JigsawWorldRaycast() {}

    /**
     * Returns the jigsaw block the cursor is pointing at, or {@code null} if the cursor isn't over a jigsaw (or is
     * outside the viewport / pointed at the sky / etc.). The returned target captures all NBT we care about — name,
     * target, pool, joint — plus the orientation read directly from the block state.
     */
    public static @Nullable JigsawBlockTarget raycastJigsaw(EngineSession session) {
        var hit = JigsawPlacementCursor.clipFromCursor(session);
        if (hit == null) {
            return null;
        }

        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }

        return JigsawBlockTarget.snapshot(mc.level, hit.getBlockPos());
    }
}
