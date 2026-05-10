package com.blib.internal.common.capture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.blib.internal.common.storage.EngineProjectIO;

/**
 * Server-side block-capture pipeline. Reads a volume of world blocks (with full block-entity NBT) and writes either a
 * single {@code .nbt} file under the project's {@code captures/} folder ({@link CaptureMode#GENERAL}) or a set of
 * jigsaw sub-pieces with auto-stitched seams plus a generated pool JSON in the project's datapack
 * ({@link CaptureMode#JIGSAW}).
 * <p>
 * All work runs on the server thread because {@link Level#getBlockState} (and {@link StructureTemplate#fillFromWorld})
 * aren't thread-safe. The caller (network handler) is on the server thread already, so no scheduling needed here.
 * <p>
 * Validation rejects volumes &gt; {@link #MAX_TOTAL_VOLUME} blocks to protect against an accidental whole-region
 * selection that would freeze the server thread for seconds.
 */
@ApiStatus.Internal
public final class BlockCaptureEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockCaptureEngine.class);

    /** Vanilla's jigsaw piece size limit. Per-axis cap for {@link CaptureMode#JIGSAW} sub-pieces. */
    public static final int MAX_PIECE_SIZE = 48;

    /** Hard cap on total capture volume (blocks). 256³ = ~16.7M cells. Larger feels like a wrong selection. */
    public static final long MAX_TOTAL_VOLUME = 256L * 256L * 256L;

    /** Capture-name regex — same shape as project name; capture names become resource paths in JIGSAW mode. */
    private static final Pattern CAPTURE_NAME_PATTERN = Pattern.compile("^[a-z0-9_-]{1,32}$");

    public record CaptureRequest(
        String projectName,
        String captureName,
        BlockPos cornerA,
        BlockPos cornerB,
        CaptureMode mode,
        ResourceKey<Level> dimension
    ) {}

    public record CaptureResult(
        boolean success,
        String message,
        List<Path> filesWritten
    ) {

        public static CaptureResult success(List<Path> files, String msg) {
            return new CaptureResult(true, msg, files);
        }

        public static CaptureResult failure(String msg) {
            return new CaptureResult(false, msg, List.of());
        }
    }

    private BlockCaptureEngine() {}

    public static CaptureResult run(MinecraftServer server, CaptureRequest req) {
        // Validation — fail-closed before touching the world.
        try {
            EngineProjectIO.validateProjectName(req.projectName());
            validateCaptureName(req.captureName());
        } catch (IllegalArgumentException e) {
            return CaptureResult.failure(e.getMessage());
        }
        if (!EngineProjectIO.isBLibProject(EngineProjectIO.projectRoot(req.projectName()))) {
            return CaptureResult.failure("Project '" + req.projectName() + "' does not exist");
        }
        var level = server.getLevel(req.dimension());
        if (level == null) {
            return CaptureResult.failure("Unknown dimension: " + req.dimension().location());
        }

        var min = minCorner(req.cornerA(), req.cornerB());
        var max = maxCorner(req.cornerA(), req.cornerB());
        var size = new Vec3i(max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1);
        var volume = (long) size.getX() * size.getY() * size.getZ();
        if (volume > MAX_TOTAL_VOLUME) {
            return CaptureResult.failure(
                "Volume too large: " + volume + " blocks (cap " + MAX_TOTAL_VOLUME + "). Split the capture into smaller regions."
            );
        }

        return switch (req.mode()) {
            case GENERAL -> runGeneral(level, req, min, size);
            case JIGSAW -> runJigsaw(level, req, min, size);
        };
    }

    /** Single fillFromWorld of the entire AABB → write to the project's {@code captures/} folder. No reload. */
    private static CaptureResult runGeneral(Level level, CaptureRequest req, BlockPos min, Vec3i size) {
        var template = new StructureTemplate();
        // ignoreEntities=true matches the user's "files written are explicit" preference and avoids capturing players.
        template.fillFromWorld(level, min, size, true, null);
        var nbt = template.save(new CompoundTag());
        try {
            var path = EngineProjectIO.writeCaptureNbt(req.projectName(), req.captureName(), nbt);
            LOGGER.info("[BLib] capture (general) wrote {} ({} blocks)", path, (long) size.getX() * size.getY() * size.getZ());
            return CaptureResult.success(
                List.of(path),
                "Saved general capture (" + size.getX() + "×" + size.getY() + "×" + size.getZ() + ")"
            );
        } catch (IOException e) {
            LOGGER.error("[BLib] capture (general) write failed for {}", req.captureName(), e);
            return CaptureResult.failure("Disk write failed: " + e.getMessage());
        }
    }

    /**
     * Split the AABB into ≤{@link #MAX_PIECE_SIZE}³ sub-pieces. For each cell: fillFromWorld, inject seams, write to
     * the datapack's structures folder. After all sub-pieces, build the pool JSON.
     */
    private static CaptureResult runJigsaw(Level level, CaptureRequest req, BlockPos min, Vec3i size) {
        var xCells = ceilDiv(size.getX(), MAX_PIECE_SIZE);
        var yCells = ceilDiv(size.getY(), MAX_PIECE_SIZE);
        var zCells = ceilDiv(size.getZ(), MAX_PIECE_SIZE);
        var totalPieces = xCells * yCells * zCells;
        var poolId = ResourceLocation.fromNamespaceAndPath(req.projectName(), req.captureName());
        var pieceIds = new ArrayList<ResourceLocation>(totalPieces);
        var filesWritten = new ArrayList<Path>(totalPieces + 1);
        var startNanos = System.nanoTime();
        // Up-front log so a user staring at "Capturing…" in the panel can confirm the server received the request and
        // see the predicted piece count. Without this, large captures look frozen on the server side too.
        LOGGER.info(
            "[BLib] capture (jigsaw) starting: project={} name={} volume={}×{}×{} ({} blocks) pieces={}×{}×{} ({} total)",
            req.projectName(),
            req.captureName(),
            size.getX(),
            size.getY(),
            size.getZ(),
            (long) size.getX() * size.getY() * size.getZ(),
            xCells,
            yCells,
            zCells,
            totalPieces
        );

        var pieceCounter = 0;
        for (var i = 0; i < xCells; i++) {
            for (var j = 0; j < yCells; j++) {
                for (var k = 0; k < zCells; k++) {
                    pieceCounter++;
                    var cellMinX = min.getX() + i * MAX_PIECE_SIZE;
                    var cellMinY = min.getY() + j * MAX_PIECE_SIZE;
                    var cellMinZ = min.getZ() + k * MAX_PIECE_SIZE;
                    // Last cell on each axis may be smaller than MAX_PIECE_SIZE.
                    var cellSizeX = Math.min(MAX_PIECE_SIZE, size.getX() - i * MAX_PIECE_SIZE);
                    var cellSizeY = Math.min(MAX_PIECE_SIZE, size.getY() - j * MAX_PIECE_SIZE);
                    var cellSizeZ = Math.min(MAX_PIECE_SIZE, size.getZ() - k * MAX_PIECE_SIZE);
                    var cellMin = new BlockPos(cellMinX, cellMinY, cellMinZ);
                    var cellSize = new Vec3i(cellSizeX, cellSizeY, cellSizeZ);

                    var template = new StructureTemplate();
                    template.fillFromWorld(level, cellMin, cellSize, true, null);
                    var nbt = template.save(new CompoundTag());

                    JigsawSeamInjector.inject(nbt, i, j, k, xCells, yCells, zCells, cellSize, req.captureName(), poolId);

                    var pieceName = req.captureName() + "_" + i + "_" + j + "_" + k;
                    var pieceId = ResourceLocation.fromNamespaceAndPath(req.projectName(), pieceName);
                    pieceIds.add(pieceId);

                    var relPath = EngineProjectIO.structureRelPath(req.projectName(), pieceName);
                    try {
                        var path = EngineProjectIO.writeDataNbt(req.projectName(), relPath, nbt);
                        filesWritten.add(path);
                        // Per-piece log so a user can tail the log and see progress for long captures. The server
                        // thread is blocked through this loop so the client UI can't show progress in real time
                        // until we add async / per-tick chunking, but the log is tail-able while the op runs (the
                        // logger uses an async appender on most setups, so writes don't block the server thread).
                        LOGGER.info(
                            "[BLib] capture (jigsaw) piece {}/{}: wrote {}",
                            pieceCounter,
                            totalPieces,
                            path.getFileName()
                        );
                    } catch (IOException e) {
                        LOGGER.error("[BLib] capture (jigsaw) write failed for piece {}", pieceId, e);
                        return CaptureResult.failure("Disk write failed for piece " + pieceId + ": " + e.getMessage());
                    }
                }
            }
        }

        // Pool JSON ties the pieces together. Vanilla's jigsaw resolver consults this pool when expanding a placed
        // jigsaw block whose pool field matches.
        var poolJson = CapturePoolJsonBuilder.buildPool(pieceIds);
        try {
            var poolPath = EngineProjectIO.writePoolJson(req.projectName(), poolId, poolJson);
            filesWritten.add(poolPath);
        } catch (IOException e) {
            LOGGER.error("[BLib] capture (jigsaw) pool write failed for {}", poolId, e);
            return CaptureResult.failure("Pool write failed: " + e.getMessage());
        }

        var elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
        var summary = "Saved jigsaw capture (" + xCells + "×" + yCells + "×" + zCells + " = " + totalPieces + " pieces, " + elapsedMs
            + "ms)";
        LOGGER.info("[BLib] capture (jigsaw) done in {}ms: {}", elapsedMs, summary);
        return CaptureResult.success(filesWritten, summary);
    }

    private static void validateCaptureName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Capture name is empty");
        }
        if (!CAPTURE_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Capture name must be 1–32 lowercase letters, digits, '_' or '-'");
        }
    }

    private static BlockPos minCorner(BlockPos a, BlockPos b) {
        return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
    }

    private static BlockPos maxCorner(BlockPos a, BlockPos b) {
        return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
    }

    private static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }
}
