package com.blib.mod.common.gameplay.history;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import com.blib.internal.common.storage.BLibDataStoreManager;
import com.blib.mod.common.gameplay.jigsaw.PlacedPieceSync;
import com.blib.mod.common.registry.init.BLibJigsawDataStoreTypes;

/**
 * Composite block-region edit. Covers every gesture that writes blocks: piece place/delete/move, selection
 * cut/paste/delete/move, single-block property edits, jigsaw-block-entity edits, plus any other multi-cell snapshot we
 * want to undo. One gesture = one {@code BlockRegionEdit} even when it touches multiple AABBs (e.g. move = source +
 * destination), so the user sees one entry on the action stack and one Ctrl+Z reverses the whole gesture atomically.
 * <p>
 * {@link #pieceLink} optionally couples the block edit with a placed-piece registry change so undoing a piece placement
 * removes both the blocks and the piece record in one step.
 */
@ApiStatus.Internal
public record BlockRegionEdit(
    ResourceKey<Level> dimension,
    List<RegionSnapshot> regions,
    String description,
    long timestamp,
    @Nullable PieceLink pieceLink
) implements WorldAction {

    public static final String TYPE_ID = "block_region";

    @Override
    public String typeId() {
        return TYPE_ID;
    }

    @Override
    public long estimatedBytes() {
        long bytes = 128L;
        for (var region : regions) {
            bytes += region.estimatedBytes();
        }
        if (pieceLink != null) {
            bytes += 128L;
        }
        return bytes;
    }

    @Override
    public void revert(MinecraftServer server) {
        var level = server.getLevel(dimension);
        if (level == null) {
            return;
        }
        // Two-phase: apply destination regions first (in case of self-overlap between regions in the list — e.g.
        // a move where source and destination AABBs touch), then NBT for each. Within a region the loop writes the
        // captured pre-state for every cell, then loads NBT into the cells that originally had block entities.
        for (var region : regions) {
            applyStates(level, region.beforeStates());
        }
        for (var region : regions) {
            applyBlockEntityNbt(level, region.beforeBE());
        }
        if (pieceLink != null) {
            // revert is the inverse of redo: an addOnRedo piece must be removed; a removeOnRedo piece (with a saved
            // snapshot) must be re-registered.
            if (pieceLink.addOnRedo() != null) {
                removePiece(level, pieceLink.addOnRedo().id());
            }
            if (pieceLink.removedSnapshot() != null) {
                addPiece(level, pieceLink.removedSnapshot());
            }
        }
    }

    @Override
    public void redo(MinecraftServer server) {
        var level = server.getLevel(dimension);
        if (level == null) {
            return;
        }
        for (var region : regions) {
            applyStates(level, region.afterStates());
        }
        for (var region : regions) {
            applyBlockEntityNbt(level, region.afterBE());
        }
        if (pieceLink != null) {
            if (pieceLink.removedSnapshot() != null) {
                removePiece(level, pieceLink.removeOnRedo());
            }
            if (pieceLink.addOnRedo() != null) {
                addPiece(level, pieceLink.addOnRedo());
            }
        }
    }

    private static void applyStates(ServerLevel level, Map<BlockPos, BlockState> states) {
        for (var entry : states.entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), Block.UPDATE_CLIENTS);
        }
    }

    private static void applyBlockEntityNbt(ServerLevel level, Map<BlockPos, CompoundTag> nbtByPos) {
        for (var entry : nbtByPos.entrySet()) {
            var pos = entry.getKey();
            // setBlock above may have produced a different BE than the snapshot expects (e.g. cell now air after a
            // delete). Defensive: skip if air or no BE.
            if (level.getBlockState(pos).is(Blocks.AIR)) {
                continue;
            }
            var be = level.getBlockEntity(pos);
            if (be != null) {
                be.loadWithComponents(entry.getValue(), level.registryAccess());
                be.setChanged();
            }
        }
    }

    private static void addPiece(ServerLevel level, com.blib.mod.common.gameplay.jigsaw.PlacedPiece piece) {
        BLibDataStoreManager.INSTANCE.getLevel(level, BLibJigsawDataStoreTypes.PLACED_PIECES).add(piece);
        PlacedPieceSync.onPieceAdded(level, piece);
    }

    private static void removePiece(ServerLevel level, java.util.UUID id) {
        if (id == null) {
            return;
        }
        var removed = BLibDataStoreManager.INSTANCE.getLevel(level, BLibJigsawDataStoreTypes.PLACED_PIECES).remove(id);
        if (removed != null) {
            PlacedPieceSync.onPieceRemoved(level, removed.id());
        }
    }
}
