package com.blib.mod.common.gameplay.history;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Pre-and-post block state for one AABB region. A {@link BlockRegionEdit} carries a list of these so a single
 * user-facing gesture (e.g. "move selection") that touches two non-overlapping regions stays one undo step.
 * <p>
 * For each cell in {@link #aabb} we record both the state before the gesture ({@link #beforeStates}) and after
 * ({@link #afterStates}). Block entities are stored separately so chest inventories / sign text survive a round-trip
 * through revert/redo. Apply order during revert: write {@code beforeStates} first, then load {@code beforeBE} NBT —
 * two-phase, so the NBT load runs against an already-correct block kind.
 */
@ApiStatus.Internal
public record RegionSnapshot(
    BoundingBox aabb,
    Map<BlockPos, BlockState> beforeStates,
    Map<BlockPos, CompoundTag> beforeBE,
    Map<BlockPos, BlockState> afterStates,
    Map<BlockPos, CompoundTag> afterBE
) {

    /** Rough retained-size estimate. BlockState references are interned globally (~0 cost); maps and NBT dominate. */
    public long estimatedBytes() {
        long bytes = 64L; // record header + aabb
        bytes += (long) beforeStates.size() * 48L; // ~entry overhead + BlockPos
        bytes += (long) afterStates.size() * 48L;
        for (var nbt : beforeBE.values()) {
            bytes += nbt.sizeInBytes();
        }
        for (var nbt : afterBE.values()) {
            bytes += nbt.sizeInBytes();
        }
        return bytes;
    }
}
