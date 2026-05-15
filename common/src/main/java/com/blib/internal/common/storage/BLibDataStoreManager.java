package com.blib.internal.common.storage;

import com.just.core.functional.option.Option;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.storage.v1.DataStore;
import com.blib.api.common.storage.v1.DataStoreType;
import com.blib.internal.common.util.BLibSaveTiming;

@ApiStatus.Internal
public class BLibDataStoreManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibDataStoreManager.class);

    private static final long SLOW_CHUNK_EVENT_LOG_THRESHOLD_MS = 50L;

    public static final BLibDataStoreManager INSTANCE = new BLibDataStoreManager();

    private final BLibChunkDataStoreManager chunk;

    private final BLibGlobalDataStoreManager global;

    private final BLibLevelDataStoreManager level;

    private final ChunkEventTimingStats chunkSaveTimingStats;

    private final ChunkEventTimingStats chunkUnloadTimingStats;

    private BLibDataStoreManager() {
        this.chunk = new BLibChunkDataStoreManager();
        this.global = new BLibGlobalDataStoreManager();
        this.level = new BLibLevelDataStoreManager();
        this.chunkSaveTimingStats = new ChunkEventTimingStats("chunk save event");
        this.chunkUnloadTimingStats = new ChunkEventTimingStats("chunk unload event");
    }

    public <T extends DataStore> T getGlobal(MinecraftServer minecraftServer, BLibHolder<DataStoreType<T>> holder) {
        return global.get(minecraftServer, holder);
    }

    public <T extends DataStore> T getLevel(ServerLevel serverLevel, BLibHolder<DataStoreType<T>> holder) {
        return this.level.get(serverLevel, holder);
    }

    public <T extends DataStore> Option<T> getChunk(ServerLevel serverLevel, ChunkPos chunkPos, BLibHolder<DataStoreType<T>> holder) {
        return chunk.get(serverLevel, chunkPos, holder);
    }

    public <T extends DataStore> Option<T> getOrCreatePersistentChunk(ServerLevel serverLevel, ChunkPos chunkPos, BLibHolder<DataStoreType<T>> holder) {
        return chunk.getOrCreatePersistent(serverLevel, chunkPos, holder);
    }

    public <T extends DataStore> void forEachStoredChunk(ServerLevel serverLevel, BLibHolder<DataStoreType<T>> holder, BiConsumer<ChunkPos, T> consumer) {
        chunk.forEachStoredChunk(serverLevel, holder, consumer);
    }

    public void saveGlobalData(MinecraftServer minecraftServer) {
        global.save(minecraftServer);
    }

    public void saveLevelData(ServerLevel serverLevel) {
        var dimension = serverLevel.dimension().location();
        BLibSaveTiming.time(LOGGER, "level store files " + dimension, () -> level.save(serverLevel));
        BLibSaveTiming.time(LOGGER, "all loaded chunk store regions " + dimension, () -> chunk.saveAllForLevel(serverLevel));
    }

    public void saveChunkData(ServerLevel serverLevel, ChunkPos chunkPos) {
        var startedAt = System.nanoTime();
        var wroteStore = false;

        try {
            wroteStore = chunk.saveChunk(serverLevel, chunkPos);
        } finally {
            chunkSaveTimingStats.record(LOGGER, serverLevel, chunkPos, wroteStore, startedAt);
        }
    }

    public void saveChunkData(ServerLevel serverLevel, Collection<ChunkPos> chunkPositions) {
        chunk.saveChunks(serverLevel, chunkPositions);
    }

    public void saveChunkDataWithTiming(ServerLevel serverLevel, Collection<ChunkPos> chunkPositions) {
        BLibSaveTiming.time(
            LOGGER,
            "selected chunk store regions " + serverLevel.dimension().location() + " requestedChunks=" + chunkPositions.size(),
            () -> chunk.saveChunksWithTiming(serverLevel, chunkPositions)
        );
    }

    public void onServerStopped(MinecraftServer minecraftServer) {
        logAndResetChunkEventTimings("server stopped before data store clear");
        global.clear();
        level.clear();
        chunk.clear();
    }

    public void logAndResetChunkEventTimings(String reason) {
        chunkSaveTimingStats.logAndReset(LOGGER, reason);
        chunkUnloadTimingStats.logAndReset(LOGGER, reason);
    }

    public void onChunkUnload(ServerLevel serverLevel, ChunkAccess chunkAccess) {
        onChunkUnload(serverLevel, chunkAccess.getPos());
    }

    public void onChunkUnload(ServerLevel serverLevel, ChunkPos chunkPos) {
        var startedAt = System.nanoTime();
        var wroteStore = false;

        try {
            wroteStore = chunk.onChunkUnload(serverLevel, chunkPos);
        } finally {
            chunkUnloadTimingStats.record(LOGGER, serverLevel, chunkPos, wroteStore, startedAt);
        }
    }

    private static final class ChunkEventTimingStats {

        private final String label;

        private long count;

        private long wroteStoreCount;

        private long totalNanos;

        private long maxNanos;

        private String maxDimension;

        private ChunkPos maxPos;

        private ChunkEventTimingStats(String label) {
            this.label = label;
        }

        private void record(Logger logger, ServerLevel level, ChunkPos pos, boolean wroteStore, long startedAtNanos) {
            var elapsedNanos = System.nanoTime() - startedAtNanos;
            var dimension = level.dimension().location().toString();

            synchronized (this) {
                count++;

                if (wroteStore) {
                    wroteStoreCount++;
                }

                totalNanos += elapsedNanos;

                if (elapsedNanos > maxNanos) {
                    maxNanos = elapsedNanos;
                    maxDimension = dimension;
                    maxPos = pos;
                }
            }

            var elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);

            if (elapsedMillis >= SLOW_CHUNK_EVENT_LOG_THRESHOLD_MS) {
                logger.info(
                    "[BLib save timing] slow {} dimension={} chunk={},{} wroteStore={} in {} ms",
                    label,
                    dimension,
                    pos.x,
                    pos.z,
                    wroteStore,
                    elapsedMillis
                );
            }
        }

        private synchronized void logAndReset(Logger logger, String reason) {
            var maxChunk = maxPos == null ? "-" : maxDimension + " " + maxPos.x + "," + maxPos.z;

            logger.info(
                "[BLib save timing] {} {} count={} wroteStores={} total={} ms max={} ms maxChunk={}",
                reason,
                label,
                count,
                wroteStoreCount,
                TimeUnit.NANOSECONDS.toMillis(totalNanos),
                TimeUnit.NANOSECONDS.toMillis(maxNanos),
                maxChunk
            );

            count = 0L;
            wroteStoreCount = 0L;
            totalNanos = 0L;
            maxNanos = 0L;
            maxDimension = null;
            maxPos = null;
        }
    }
}
