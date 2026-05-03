package com.blib.api.common.pathfinding.v1.evaluator;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;

/**
 * Fast block state access with chunk caching and block property caching. Optimized for spatial locality during
 * pathfinding searches.
 * <p>
 * Chunk data is cached per-chunk to avoid repeated lookups. Block properties (solid, liquid, passable) are cached by
 * block state ID to avoid virtual dispatch overhead (Baritone-style PrecomputedData).
 * </p>
 */
public final class BlockAccessor {

    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    // --- Chunk cache (spatial locality, direct section access) ---
    private final Long2ObjectOpenHashMap<ChunkAccess> chunkMap = new Long2ObjectOpenHashMap<>();

    private LevelChunkSection @Nullable [] cachedSections;

    private int cachedChunkX = Integer.MIN_VALUE;

    private int cachedChunkZ = Integer.MIN_VALUE;

    private int cachedMinSectionY;

    // --- Block property cache (avoid virtual dispatch) ---
    private boolean @Nullable [] solidCache;

    private boolean @Nullable [] liquidCache;

    private boolean @Nullable [] passableCache;

    private boolean @Nullable [] propertyComputed;

    private @Nullable LevelReader level;

    /**
     * Prepares the accessor for synchronous block reads. The level is used for on-demand chunk loading.
     */
    public void prepare(LevelReader level) {
        this.level = level;
        resetChunkCache();
    }

    /**
     * Prepares the accessor for async (off-thread) block reads. Chunks must be pre-loaded via
     * {@link #preloadChunk(int, int, ChunkAccess)} before the search starts. Reads that miss the cache return AIR.
     */
    public void prepareAsync() {
        this.level = null;
        resetChunkCache();
    }

    /**
     * Pre-loads a chunk for async access. Call from the main thread before dispatching a background search.
     */
    public void preloadChunk(int chunkX, int chunkZ, ChunkAccess chunk) {
        chunkMap.put(packChunkKey(chunkX, chunkZ), chunk);
    }

    /**
     * Releases the level reference after a search completes.
     */
    public void cleanup() {
        this.level = null;
    }

    /**
     * Returns the block state at the given position. Uses cached chunk sections for fast spatial access. Returns AIR
     * for out-of-bounds or missing chunks (in async mode).
     */
    public BlockState getBlockState(int x, int y, int z) {
        var cx = x >> 4;
        var cz = z >> 4;

        if (cx != cachedChunkX || cz != cachedChunkZ) {
            var key = packChunkKey(cx, cz);
            var chunk = chunkMap.get(key);

            if (chunk == null) {
                if (level != null) {
                    chunk = level.getChunk(cx, cz);
                    chunkMap.put(key, chunk);
                } else {
                    return AIR;
                }
            }

            cachedSections = chunk.getSections();
            cachedChunkX = cx;
            cachedChunkZ = cz;
            cachedMinSectionY = chunk.getMinSection();
        }

        var sectionIndex = (y >> 4) - cachedMinSectionY;

        if (sectionIndex < 0 || sectionIndex >= cachedSections.length) {
            return AIR;
        }

        var section = cachedSections[sectionIndex];

        if (section == null || section.hasOnlyAir()) {
            return AIR;
        }

        return section.getBlockState(x & 15, y & 15, z & 15);
    }

    public boolean isSolid(BlockState state) {
        var id = Block.BLOCK_STATE_REGISTRY.getId(state);
        ensurePropertyCached(state, id);
        return solidCache[id];
    }

    public boolean isLiquid(BlockState state) {
        var id = Block.BLOCK_STATE_REGISTRY.getId(state);
        ensurePropertyCached(state, id);
        return liquidCache[id];
    }

    public boolean isPassable(BlockState state) {
        var id = Block.BLOCK_STATE_REGISTRY.getId(state);
        ensurePropertyCached(state, id);
        return passableCache[id];
    }

    private void resetChunkCache() {
        cachedSections = null;
        cachedChunkX = Integer.MIN_VALUE;
        cachedChunkZ = Integer.MIN_VALUE;
        chunkMap.clear();

        // Initialize property cache once (block state properties never change at runtime).
        if (solidCache == null) {
            var stateCount = Block.BLOCK_STATE_REGISTRY.size();
            solidCache = new boolean[stateCount];
            liquidCache = new boolean[stateCount];
            passableCache = new boolean[stateCount];
            propertyComputed = new boolean[stateCount];
        }
    }

    private void ensurePropertyCached(BlockState state, int id) {
        if (!propertyComputed[id]) {
            var solid = state.isSolid();
            var liquid = state.liquid();
            solidCache[id] = solid;
            liquidCache[id] = liquid;
            passableCache[id] = !solid && !liquid;
            propertyComputed[id] = true;
        }
    }

    private static long packChunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX & 0xFFFFFFFFL) << 32 | ((long) chunkZ & 0xFFFFFFFFL);
    }
}
