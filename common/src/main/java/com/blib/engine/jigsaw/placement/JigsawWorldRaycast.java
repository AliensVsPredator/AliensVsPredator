package com.blib.engine.jigsaw.placement;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.session.EngineSession;

/**
 * Helper that finds the jigsaw block under the cursor, if any, and snapshots it as a {@link JigsawBlockTarget}. Reuses
 * {@link JigsawPlacementCursor#clipFromCursor} so the raycast geometry is exactly the same one
 * {@code FreePlacementResolver} uses — no risk of "the FREE-mode preview hits block X but the SNAP-mode raycast hits
 * block Y" inconsistencies.
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

        var pos = hit.getBlockPos();
        var state = mc.level.getBlockState(pos);
        if (!state.is(Blocks.JIGSAW)) {
            return null;
        }

        // The block entity might be missing (chunk unload race, server desync). Without it we don't have the
        // pool/target/name fields — bail rather than guessing.
        if (!(mc.level.getBlockEntity(pos) instanceof JigsawBlockEntity jigsaw)) {
            return null;
        }

        return new JigsawBlockTarget(
            pos.immutable(),
            JigsawBlock.getFrontFacing(state),
            JigsawBlock.getTopFacing(state),
            jigsaw.getName(),
            jigsaw.getTarget(),
            jigsaw.getPool(),
            jigsaw.getJoint()
        );
    }
}
