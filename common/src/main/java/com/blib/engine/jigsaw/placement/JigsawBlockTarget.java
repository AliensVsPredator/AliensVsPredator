package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A jigsaw block found in the world — the "anchor" the snap resolver aligns the candidate piece to, and the snapshot
 * the inspector renders for selection. Captures everything the alignment + compatibility checks need without the
 * resolver having to keep a reference to the live {@link JigsawBlockEntity} (which can become stale if the chunk
 * unloads or the block changes mid-frame).
 */
@ApiStatus.Internal
public record JigsawBlockTarget(
    BlockPos worldPos,
    Direction front,
    Direction top,
    ResourceLocation name,
    ResourceLocation target,
    ResourceKey<StructureTemplatePool> pool,
    JigsawBlockEntity.JointType joint,
    String finalState
) {

    /**
     * Read the live state of a jigsaw block at {@code pos} and snapshot it as a {@code JigsawBlockTarget}. Returns
     * {@code null} if the block isn't a jigsaw or the block entity is missing (chunk unload race, server desync). Both
     * the snap-resolver raycast and the selection inspector go through this helper so they always see the same field
     * set with the same null-handling.
     */
    public static @Nullable JigsawBlockTarget snapshot(BlockGetter level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!state.is(Blocks.JIGSAW)) {
            return null;
        }
        if (!(level.getBlockEntity(pos) instanceof JigsawBlockEntity jigsaw)) {
            return null;
        }
        return new JigsawBlockTarget(
            pos.immutable(),
            JigsawBlock.getFrontFacing(state),
            JigsawBlock.getTopFacing(state),
            jigsaw.getName(),
            jigsaw.getTarget(),
            jigsaw.getPool(),
            jigsaw.getJoint(),
            jigsaw.getFinalState()
        );
    }
}
