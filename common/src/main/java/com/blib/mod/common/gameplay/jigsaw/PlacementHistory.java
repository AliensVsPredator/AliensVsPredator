package com.blib.mod.common.gameplay.jigsaw;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Bounded server-side undo stack for jigsaw placements. Captures pre-placement world state in
 * {@link #push(ServerLevel, BoundingBox, ResourceLocation)} and restores it in {@link #undo(ServerLevel)}.
 * <p>
 * Global (cross-player) but dimension-aware: undo from a particular {@link ServerLevel} only pops entries from that
 * dimension. With engine mode being singleplayer-only this is effectively a per-session global stack; the dimension
 * filter just protects against the user nether-portaling between place and undo.
 * <p>
 * Capacity defaults to 64 entries — large enough for an authoring session, small enough that the worst-case NBT
 * retention (a chest-stuffed mansion times 64) stays in tens of MB. Snapshots are FIFO-evicted when the cap is hit;
 * older entries become unreachable.
 */
@ApiStatus.Internal
public final class PlacementHistory {

    private static final int MAX_ENTRIES = 64;

    /** {@code addFirst} = newest at head; {@code removeLast} = drop oldest when over cap. */
    private static final Deque<PlacementSnapshot> stack = new ArrayDeque<>();

    private PlacementHistory() {}

    /**
     * Capture the pre-placement state of every cell in {@code aabb} and push it onto the stack. Run this BEFORE the
     * actual {@code placeInWorld} call so the snapshot reflects the original world state, not the post-placement one.
     */
    public static synchronized void push(ServerLevel level, BoundingBox aabb, ResourceLocation templateId) {
        var states = new HashMap<BlockPos, net.minecraft.world.level.block.state.BlockState>();
        var blockEntityNbt = new HashMap<BlockPos, net.minecraft.nbt.CompoundTag>();

        for (var pos : BlockPos.betweenClosed(aabb.minX(), aabb.minY(), aabb.minZ(), aabb.maxX(), aabb.maxY(), aabb.maxZ())) {
            var immutable = pos.immutable();
            states.put(immutable, level.getBlockState(immutable));
            var blockEntity = level.getBlockEntity(immutable);
            if (blockEntity != null) {
                blockEntityNbt.put(immutable, blockEntity.saveWithFullMetadata(level.registryAccess()));
            }
        }

        var snapshot = new PlacementSnapshot(level.dimension(), aabb, states, blockEntityNbt, templateId, System.currentTimeMillis());
        stack.addFirst(snapshot);
        while (stack.size() > MAX_ENTRIES) {
            stack.removeLast();
        }
    }

    /**
     * Pop and restore the most recent snapshot whose {@code dimension} matches {@code level}. Returns {@code true} if a
     * snapshot was found and applied, {@code false} otherwise (empty stack, or no entries matching this dimension).
     */
    public static synchronized boolean undo(ServerLevel level) {
        Iterator<PlacementSnapshot> iter = stack.iterator();
        while (iter.hasNext()) {
            var snapshot = iter.next();
            if (snapshot.dimension().equals(level.dimension())) {
                iter.remove();
                restore(level, snapshot);
                return true;
            }
        }
        return false;
    }

    /** Clear the stack — invoked when the integrated server stops so a fresh session starts clean. */
    public static synchronized void clear() {
        stack.clear();
    }

    public static synchronized int size() {
        return stack.size();
    }

    /**
     * Apply a snapshot back to the world. Two-phase to handle block-entity replacement cleanly:
     * <ol>
     * <li>Set every cell to its captured block state with {@code UPDATE_CLIENTS} only — no neighbor updates, so
     * removing the placed structure doesn't pop adjacent torches / repeaters / etc. The barrier-trick
     * (set-to-barrier-then-set-to-target) used by vanilla {@code placeInWorld} isn't needed here because restore is
     * replacing one consistent state with another, not splicing into existing structure.</li>
     * <li>For cells that originally had a block entity, load the captured NBT into the freshly-placed BE so chest
     * inventories, sign text, etc. come back intact.</li>
     * </ol>
     */
    private static void restore(ServerLevel level, PlacementSnapshot snapshot) {
        for (var entry : snapshot.states().entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), Block.UPDATE_CLIENTS);
        }
        for (var entry : snapshot.blockEntityNbt().entrySet()) {
            var pos = entry.getKey();
            // The setBlock above may have produced a different BE than the snapshot expects (e.g. if the placed
            // structure changed the block kind). Defensive null + air check: if the post-restore block has no BE
            // or is air, the captured NBT can't apply — skip rather than throw.
            if (level.getBlockState(pos).is(Blocks.AIR)) {
                continue;
            }
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.loadWithComponents(entry.getValue(), level.registryAccess());
                blockEntity.setChanged();
            }
        }
    }
}
