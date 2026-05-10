package com.blib.internal.common.clipboard;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.common.capture.BlockCaptureEngine;

/**
 * Server-side clipboard operations: Copy/Cut to {@link ServerBlockClipboard}, Paste from it, and Delete (clear AABB to
 * air without touching the clipboard). Mirrors {@code BlockMoveEngine} in style — StructureTemplate snapshot for read,
 * vanilla {@link StructureTemplate#placeInWorld} for write — so block-entity NBT, light updates, and registry state are
 * handled by the canonical path.
 * <p>
 * Volume cap reused from {@link BlockCaptureEngine#MAX_TOTAL_VOLUME} (256³). Cap is checked on the source AABB for
 * Copy/Cut/Delete; for Paste the size is whatever the clipboard holds (already capped on the read that filled it).
 */
@ApiStatus.Internal
public final class BlockClipboardEngine {

    public static final long MAX_TOTAL_VOLUME = BlockCaptureEngine.MAX_TOTAL_VOLUME;

    private BlockClipboardEngine() {}

    public record OpResult(
        boolean success,
        String message,
        int blockCount
    ) {

        public static OpResult success(int count) {
            return new OpResult(true, "", count);
        }

        public static OpResult failure(String msg) {
            return new OpResult(false, msg, 0);
        }
    }

    /**
     * Snapshot the AABB into {@link ServerBlockClipboard} (replacing any previous contents). When {@code deleteSource}
     * is true, additionally clears the source to air — the standard Cut semantic. Validates against the volume cap;
     * returns failure without touching the world or clipboard if oversized.
     */
    public static OpResult copy(
        MinecraftServer server,
        BlockPos cornerA,
        BlockPos cornerB,
        boolean deleteSource,
        ResourceKey<Level> dimension
    ) {
        var dim = server.getLevel(dimension);
        if (dim == null) {
            return OpResult.failure("Dimension not loaded");
        }

        var minX = Math.min(cornerA.getX(), cornerB.getX());
        var minY = Math.min(cornerA.getY(), cornerB.getY());
        var minZ = Math.min(cornerA.getZ(), cornerB.getZ());
        var maxX = Math.max(cornerA.getX(), cornerB.getX());
        var maxY = Math.max(cornerA.getY(), cornerB.getY());
        var maxZ = Math.max(cornerA.getZ(), cornerB.getZ());
        var sx = maxX - minX + 1;
        var sy = maxY - minY + 1;
        var sz = maxZ - minZ + 1;
        var volume = (long) sx * sy * sz;

        if (volume <= 0 || volume > MAX_TOTAL_VOLUME) {
            return OpResult.failure("Volume out of bounds (max " + MAX_TOTAL_VOLUME + " blocks)");
        }

        var origin = new BlockPos(minX, minY, minZ);
        var size = new Vec3i(sx, sy, sz);

        var template = new StructureTemplate();
        template.fillFromWorld(dim, origin, size, true, null);
        ServerBlockClipboard.put(template, size, dimension);

        if (deleteSource) {
            for (var x = 0; x < sx; x++) {
                for (var y = 0; y < sy; y++) {
                    for (var z = 0; z < sz; z++) {
                        dim.setBlock(new BlockPos(minX + x, minY + y, minZ + z), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }

        return OpResult.success((int) Math.min(volume, Integer.MAX_VALUE));
    }

    /**
     * Place the current clipboard contents at {@code destination}. Returns failure with an empty-clipboard message if
     * the slot is empty; otherwise the placement always succeeds (vanilla {@code placeInWorld} handles all edge cases).
     * Block-entity NBT, light updates, and state copying come for free from the structure-template path.
     */
    public static OpResult paste(MinecraftServer server, BlockPos destination, ResourceKey<Level> dimension) {
        if (!ServerBlockClipboard.hasContents()) {
            return OpResult.failure("Clipboard is empty");
        }
        var dim = server.getLevel(dimension);
        if (dim == null) {
            return OpResult.failure("Dimension not loaded");
        }

        var template = ServerBlockClipboard.template();
        var size = ServerBlockClipboard.size();
        if (template == null || size == null) {
            return OpResult.failure("Clipboard is empty");
        }

        var settings = new StructurePlaceSettings();
        template.placeInWorld(dim, destination, destination, settings, dim.getRandom(), Block.UPDATE_ALL);
        return OpResult.success(size.getX() * size.getY() * size.getZ());
    }

    /**
     * Clear the AABB to air without touching the clipboard. Fast path — skips the structure-template snapshot since
     * we're not preserving any state.
     */
    public static OpResult delete(MinecraftServer server, BlockPos cornerA, BlockPos cornerB, ResourceKey<Level> dimension) {
        var dim = server.getLevel(dimension);
        if (dim == null) {
            return OpResult.failure("Dimension not loaded");
        }

        var minX = Math.min(cornerA.getX(), cornerB.getX());
        var minY = Math.min(cornerA.getY(), cornerB.getY());
        var minZ = Math.min(cornerA.getZ(), cornerB.getZ());
        var maxX = Math.max(cornerA.getX(), cornerB.getX());
        var maxY = Math.max(cornerA.getY(), cornerB.getY());
        var maxZ = Math.max(cornerA.getZ(), cornerB.getZ());
        var sx = maxX - minX + 1;
        var sy = maxY - minY + 1;
        var sz = maxZ - minZ + 1;
        var volume = (long) sx * sy * sz;

        if (volume <= 0 || volume > MAX_TOTAL_VOLUME) {
            return OpResult.failure("Volume out of bounds (max " + MAX_TOTAL_VOLUME + " blocks)");
        }

        for (var x = 0; x < sx; x++) {
            for (var y = 0; y < sy; y++) {
                for (var z = 0; z < sz; z++) {
                    dim.setBlock(new BlockPos(minX + x, minY + y, minZ + z), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }

        return OpResult.success((int) Math.min(volume, Integer.MAX_VALUE));
    }
}
