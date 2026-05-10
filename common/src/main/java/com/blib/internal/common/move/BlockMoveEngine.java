package com.blib.internal.common.move;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blib.internal.common.capture.BlockCaptureEngine;

/**
 * Server-side block-move pipeline. Reads a volume of world blocks via {@link StructureTemplate#fillFromWorld}
 * (block-entity NBT included), optionally clears the origin to air, and re-places the snapshot at the offset
 * destination. {@code copy=true} leaves the origin intact (Photoshop's Alt-drag semantics).
 * <p>
 * StructureTemplate-based snapshot+place is preferable to manual per-block iteration because vanilla already handles
 * block-entity NBT preservation, light updates, and registry-aware state copying. The cost is one extra in-memory
 * allocation of the structure template — fine for the {@code 256³} volume cap we share with capture.
 * <p>
 * Origin/destination overlap is safe: the snapshot is built before any clearing, so blocks in the overlap region
 * survive the clear pass and end up correctly placed.
 */
@ApiStatus.Internal
public final class BlockMoveEngine {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockMoveEngine.class);

    /** Hard cap on move volume (blocks). Reused from {@link BlockCaptureEngine} — same blast-radius concern. */
    public static final long MAX_TOTAL_VOLUME = BlockCaptureEngine.MAX_TOTAL_VOLUME;

    private BlockMoveEngine() {}

    public record MoveRequest(
        BlockPos cornerA,
        BlockPos cornerB,
        int dx,
        int dy,
        int dz,
        boolean copy,
        ResourceKey<Level> dimension
    ) {}

    public record MoveResult(
        boolean success,
        String message,
        int blockCount
    ) {

        public static MoveResult success(int count) {
            return new MoveResult(true, "", count);
        }

        public static MoveResult failure(String msg) {
            return new MoveResult(false, msg, 0);
        }
    }

    public static MoveResult run(MinecraftServer server, MoveRequest req) {
        var dim = server.getLevel(req.dimension());
        if (dim == null) {
            return MoveResult.failure("Dimension not loaded");
        }

        var minX = Math.min(req.cornerA.getX(), req.cornerB.getX());
        var minY = Math.min(req.cornerA.getY(), req.cornerB.getY());
        var minZ = Math.min(req.cornerA.getZ(), req.cornerB.getZ());
        var maxX = Math.max(req.cornerA.getX(), req.cornerB.getX());
        var maxY = Math.max(req.cornerA.getY(), req.cornerB.getY());
        var maxZ = Math.max(req.cornerA.getZ(), req.cornerB.getZ());
        var sx = maxX - minX + 1;
        var sy = maxY - minY + 1;
        var sz = maxZ - minZ + 1;
        var volume = (long) sx * sy * sz;

        if (volume <= 0 || volume > MAX_TOTAL_VOLUME) {
            return MoveResult.failure("Volume out of bounds (max " + MAX_TOTAL_VOLUME + " blocks)");
        }
        // Zero-offset is a no-op — bail before doing the snapshot work.
        if (req.dx == 0 && req.dy == 0 && req.dz == 0) {
            return MoveResult.success(0);
        }

        var origin = new BlockPos(minX, minY, minZ);
        var size = new Vec3i(sx, sy, sz);

        // Snapshot: ignoreEntities=true to match capture's behavior (entity move is a separate concern), no
        // ignored-block filter so air positions also get carried (so they overwrite destination blocks correctly
        // — otherwise a "move" into occupied space would leave debris from the destination's original blocks).
        var template = new StructureTemplate();
        template.fillFromWorld(dim, origin, size, true, null);

        // Optionally clear origin to air. Cleared before placement so origin/destination overlap behaves correctly:
        // the snapshot is already in memory, so clearing the origin can't lose anything.
        if (!req.copy) {
            for (var x = 0; x < sx; x++) {
                for (var y = 0; y < sy; y++) {
                    for (var z = 0; z < sz; z++) {
                        dim.setBlock(new BlockPos(minX + x, minY + y, minZ + z), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }

        // Place at destination. Block.UPDATE_ALL = neighbor + client + observer notifications; matches what users
        // expect from a normal block placement.
        var destination = new BlockPos(minX + req.dx, minY + req.dy, minZ + req.dz);
        var settings = new StructurePlaceSettings();
        template.placeInWorld(dim, destination, destination, settings, dim.getRandom(), Block.UPDATE_ALL);

        return MoveResult.success((int) Math.min(volume, Integer.MAX_VALUE));
    }
}
