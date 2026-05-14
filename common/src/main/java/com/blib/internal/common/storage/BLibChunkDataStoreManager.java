package com.blib.internal.common.storage;

import com.just.core.functional.option.Option;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.storage.v1.DataStore;
import com.blib.api.common.storage.v1.DataStoreType;

@ApiStatus.Internal
class BLibChunkDataStoreManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibChunkDataStoreManager.class);

    private static final String LEVELS_FOLDER = "levels";

    private static final String CHUNKS_FOLDER = "chunks";

    private static final String REGION_SIZE_KEY = "region_size";

    // Region size: 32x32 chunks per region file
    // Using bit shift for efficient coordinate conversion
    // To support other power-of-2 sizes in the future, change these constants
    private static final int REGION_SHIFT = 5; // log2(32) = 5

    private static final int REGION_MASK = (1 << REGION_SHIFT) - 1; // 31, for modulo operation

    // Chunk stores: dimension -> chunk pos -> store id -> store
    private final Map<ResourceKey<Level>, Map<ChunkPos, Map<ResourceLocation, DataStore>>> stores = new HashMap<>();

    // Cached region NBT data: dimension -> namespace -> region key -> region tag
    private final Map<ResourceKey<Level>, Map<String, Map<Long, CompoundTag>>> loadedRegions = new HashMap<>();

    // Reference counts for regions: dimension -> region key -> number of tracked chunks
    // When the count reaches 0, the region's cached data is released from memory.
    private final Map<ResourceKey<Level>, Map<Long, Integer>> regionRefCounts = new HashMap<>();

    // ==================== Public Methods ====================

    @SuppressWarnings("unchecked")
    <T extends DataStore> Option<T> get(ServerLevel level, ChunkPos pos, BLibHolder<DataStoreType<T>> type) {
        if (!level.hasChunk(pos.x, pos.z)) {
            return Option.none();
        }

        return getOrCreate(level, pos, type);
    }

    <T extends DataStore> Option<T> getOrCreatePersistent(
        ServerLevel level,
        ChunkPos pos,
        BLibHolder<DataStoreType<T>> type
    ) {
        return getOrCreate(level, pos, type);
    }

    @SuppressWarnings("unchecked")
    private <T extends DataStore> Option<T> getOrCreate(
        ServerLevel level,
        ChunkPos pos,
        BLibHolder<DataStoreType<T>> type
    ) {
        var levelKey = level.dimension();
        var id = type.getResourceLocation();
        var levelChunkStores = stores.computeIfAbsent(levelKey, k -> new HashMap<>());
        var isNewChunk = !levelChunkStores.containsKey(pos);
        var chunkDataStores = levelChunkStores.computeIfAbsent(pos, p -> new HashMap<>());

        if (isNewChunk && level.hasChunk(pos.x, pos.z)) {
            incrementRegionRefCount(levelKey, pos);
        }

        return Option.some((T) chunkDataStores.computeIfAbsent(id, k -> loadOrCreate(level, pos, type)));
    }

    <T extends DataStore> void forEachStoredChunk(
        ServerLevel level,
        BLibHolder<DataStoreType<T>> type,
        BiConsumer<ChunkPos, T> consumer
    ) {
        var id = type.getResourceLocation();
        var folder = getRegionFolder(level, id.getNamespace());

        if (!Files.isDirectory(folder)) {
            return;
        }

        try (var paths = Files.list(folder)) {
            paths
                .filter(Files::isRegularFile)
                .forEach(path -> loadStoredChunksFromRegion(level, type, consumer, path));
        } catch (IOException e) {
            LOGGER.error("Failed to list chunk data store region files in {}", folder, e);
        }
    }

    void saveChunk(ServerLevel level, ChunkPos pos) {
        var levelKey = level.dimension();
        var levelChunkStores = stores.get(levelKey);

        if (levelChunkStores == null) {
            return;
        }

        var chunkDataStores = levelChunkStores.get(pos);

        if (chunkDataStores == null || chunkDataStores.isEmpty()) {
            return;
        }

        // Group stores by namespace and save to appropriate region files
        var storesByNamespace = new HashMap<String, Map<ResourceLocation, DataStore>>();

        for (var entry : chunkDataStores.entrySet()) {
            var id = entry.getKey();
            var store = entry.getValue();

            storesByNamespace
                .computeIfAbsent(id.getNamespace(), k -> new HashMap<>())
                .put(id, store);
        }

        for (var entry : storesByNamespace.entrySet()) {
            var namespace = entry.getKey();
            var namespacedStores = entry.getValue();

            saveChunkToRegion(level, pos, namespace, namespacedStores);
        }
    }

    void saveAllForLevel(ServerLevel level) {
        var levelKey = level.dimension();
        var levelChunkStores = stores.get(levelKey);

        if (levelChunkStores == null || levelChunkStores.isEmpty()) {
            return;
        }

        // Group all chunks by namespace and region
        // Structure: namespace -> region key -> chunk pos -> stores
        var byNamespaceAndRegion = new HashMap<String, Map<Long, Map<ChunkPos, Map<ResourceLocation, DataStore>>>>();

        for (var chunkEntry : levelChunkStores.entrySet()) {
            var pos = chunkEntry.getKey();
            var chunkStores = chunkEntry.getValue();
            var regionKey = getRegionKey(pos);

            for (var storeEntry : chunkStores.entrySet()) {
                var id = storeEntry.getKey();
                var store = storeEntry.getValue();

                byNamespaceAndRegion
                    .computeIfAbsent(id.getNamespace(), k -> new HashMap<>())
                    .computeIfAbsent(regionKey, k -> new HashMap<>())
                    .computeIfAbsent(pos, k -> new HashMap<>())
                    .put(id, store);
            }
        }

        // Save each region
        for (var namespaceEntry : byNamespaceAndRegion.entrySet()) {
            var namespace = namespaceEntry.getKey();

            for (var regionEntry : namespaceEntry.getValue().entrySet()) {
                var regionKey = regionEntry.getKey();
                var chunksInRegion = regionEntry.getValue();

                saveRegion(level, namespace, regionKey, chunksInRegion);
            }
        }
    }

    void onChunkUnload(ServerLevel level, ChunkPos pos) {
        saveChunk(level, pos);

        var levelKey = level.dimension();
        var levelChunkStores = stores.get(levelKey);

        if (levelChunkStores != null && levelChunkStores.remove(pos) != null) {
            decrementRegionRefCount(levelKey, pos);
        }
    }

    void clear() {
        stores.clear();
        loadedRegions.clear();
        regionRefCounts.clear();
    }

    void clearLevel(ResourceKey<Level> levelKey) {
        stores.remove(levelKey);
        loadedRegions.remove(levelKey);
        regionRefCounts.remove(levelKey);
    }

    // ==================== Load/Save Helpers ====================

    private <T extends DataStore> T loadOrCreate(
        ServerLevel level,
        ChunkPos pos,
        BLibHolder<DataStoreType<T>> type
    ) {
        var id = type.getResourceLocation();
        var store = type.value().createInstance();

        // Try to load from region file
        var regionTag = getOrLoadRegion(level, pos, id.getNamespace());

        if (regionTag != null) {
            var chunkKey = getChunkKey(pos);
            if (regionTag.contains(chunkKey)) {
                var chunkTag = regionTag.getCompound(chunkKey);
                if (chunkTag.contains(id.getPath())) {
                    var storeTag = chunkTag.getCompound(id.getPath());
                    store.load(storeTag);
                    LOGGER.debug("Loaded chunk store '{}' at {} from region", id, pos);
                }
            }
        }

        return store;
    }

    private void saveChunkToRegion(
        ServerLevel level,
        ChunkPos pos,
        String namespace,
        Map<ResourceLocation, DataStore> chunkStores
    ) {
        var regionKey = getRegionKey(pos);

        // Load existing region data or create new
        var regionTag = getOrLoadRegion(level, pos, namespace);

        if (regionTag == null) {
            regionTag = new CompoundTag();
        }

        // Update the chunk's data in the region
        var chunkKey = getChunkKey(pos);
        var chunkTag = regionTag.contains(chunkKey) ? regionTag.getCompound(chunkKey) : new CompoundTag();

        for (var entry : chunkStores.entrySet()) {
            var id = entry.getKey();
            var store = entry.getValue();
            var storeTag = new CompoundTag();
            store.save(storeTag);

            if (storeTag.isEmpty()) {
                chunkTag.remove(id.getPath());
            } else {
                chunkTag.put(id.getPath(), storeTag);
            }
        }

        if (chunkTag.isEmpty()) {
            regionTag.remove(chunkKey);
        } else {
            regionTag.put(chunkKey, chunkTag);
        }

        // Update cache
        var levelKey = level.dimension();
        loadedRegions
            .computeIfAbsent(levelKey, k -> new HashMap<>())
            .computeIfAbsent(namespace, k -> new HashMap<>())
            .put(regionKey, regionTag);

        // Write to disk
        writeRegionToDisk(getRegionPath(level, namespace, regionKey), regionTag);
    }

    private void saveRegion(
        ServerLevel level,
        String namespace,
        long regionKey,
        Map<ChunkPos, Map<ResourceLocation, DataStore>> chunksInRegion
    ) {
        // Load existing region data or create new
        var regionTag = getOrLoadRegionByKey(level, namespace, regionKey);

        if (regionTag == null) {
            regionTag = new CompoundTag();
        }

        // Update all chunks in the region
        for (var chunkEntry : chunksInRegion.entrySet()) {
            var pos = chunkEntry.getKey();
            var chunkStores = chunkEntry.getValue();

            var chunkKey = getChunkKey(pos);
            var chunkTag = regionTag.contains(chunkKey) ? regionTag.getCompound(chunkKey) : new CompoundTag();

            for (var storeEntry : chunkStores.entrySet()) {
                var id = storeEntry.getKey();
                var store = storeEntry.getValue();
                var storeTag = new CompoundTag();
                store.save(storeTag);

                if (storeTag.isEmpty()) {
                    chunkTag.remove(id.getPath());
                } else {
                    chunkTag.put(id.getPath(), storeTag);
                }
            }

            if (chunkTag.isEmpty()) {
                regionTag.remove(chunkKey);
            } else {
                regionTag.put(chunkKey, chunkTag);
            }
        }

        // Write to disk
        writeRegionToDisk(getRegionPath(level, namespace, regionKey), regionTag);
    }

    private void writeRegionToDisk(Path path, CompoundTag regionTag) {
        if (regionTag.isEmpty()) {
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                    LOGGER.debug("Deleted empty region file {}", path);
                } catch (IOException e) {
                    LOGGER.error("Failed to delete empty region file {}", path, e);
                }
            }
        } else {
            try {
                Files.createDirectories(path.getParent());
                regionTag.putInt(REGION_SIZE_KEY, REGION_SIZE);
                NbtIo.writeCompressed(regionTag, path);
                regionTag.remove(REGION_SIZE_KEY);
                LOGGER.debug("Saved region file {}", path);
            } catch (IOException e) {
                LOGGER.error("Failed to save region file {}", path, e);
            }
        }
    }

    // ==================== Region Cache ====================

    private <T extends DataStore> void loadStoredChunksFromRegion(
        ServerLevel level,
        BLibHolder<DataStoreType<T>> type,
        BiConsumer<ChunkPos, T> consumer,
        Path path
    ) {
        var regionKey = parseRegionKey(path.getFileName().toString());

        if (regionKey == null) {
            return;
        }

        var id = type.getResourceLocation();
        var regionTag = getOrLoadRegionByKey(level, id.getNamespace(), regionKey);

        if (regionTag == null || regionTag.isEmpty()) {
            return;
        }

        for (var chunkKey : regionTag.getAllKeys()) {
            if (REGION_SIZE_KEY.equals(chunkKey) || !regionTag.contains(chunkKey, Tag.TAG_COMPOUND)) {
                continue;
            }

            var pos = parseChunkPos(regionKey, chunkKey);

            if (pos == null) {
                continue;
            }

            var chunkTag = regionTag.getCompound(chunkKey);

            if (!chunkTag.contains(id.getPath(), Tag.TAG_COMPOUND)) {
                continue;
            }

            var store = type.value().createInstance();
            store.load(chunkTag.getCompound(id.getPath()));
            consumer.accept(pos, store);
        }
    }

    private CompoundTag getOrLoadRegion(ServerLevel level, ChunkPos pos, String namespace) {
        return getOrLoadRegionByKey(level, namespace, getRegionKey(pos));
    }

    private CompoundTag getOrLoadRegionByKey(ServerLevel level, String namespace, long regionKey) {
        var levelKey = level.dimension();

        // Check cache first
        var levelRegions = loadedRegions.get(levelKey);

        if (levelRegions != null) {
            var namespaceRegions = levelRegions.get(namespace);

            if (namespaceRegions != null && namespaceRegions.containsKey(regionKey)) {
                return namespaceRegions.get(regionKey);
            }
        }

        // Load from disk
        var path = getRegionPath(level, namespace, regionKey);
        CompoundTag regionTag = null;

        if (Files.exists(path)) {
            try {
                regionTag = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());

                if (regionTag.contains(REGION_SIZE_KEY)) {
                    var storedSize = regionTag.getInt(REGION_SIZE_KEY);
                    regionTag.remove(REGION_SIZE_KEY);

                    if (storedSize != REGION_SIZE) {
                        LOGGER.warn(
                            "Region file {} was saved with region size {} but current size is {}. Data may be misaligned.",
                            path,
                            storedSize,
                            REGION_SIZE
                        );
                    }
                }

                LOGGER.debug("Loaded region file {}", path);
            } catch (IOException e) {
                LOGGER.error("Failed to load region file {}", path, e);
            }
        }

        // Cache the result (even if null, store an empty tag to avoid repeated disk checks)
        loadedRegions
            .computeIfAbsent(levelKey, k -> new HashMap<>())
            .computeIfAbsent(namespace, k -> new HashMap<>())
            .put(regionKey, regionTag != null ? regionTag : new CompoundTag());

        return regionTag;
    }

    // ==================== Region Reference Counting ====================

    private void incrementRegionRefCount(ResourceKey<Level> levelKey, ChunkPos pos) {
        var regionKey = getRegionKey(pos);

        regionRefCounts
            .computeIfAbsent(levelKey, k -> new HashMap<>())
            .merge(regionKey, 1, Integer::sum);
    }

    private void decrementRegionRefCount(ResourceKey<Level> levelKey, ChunkPos pos) {
        var levelRefCounts = regionRefCounts.get(levelKey);

        if (levelRefCounts == null) {
            return;
        }

        var regionKey = getRegionKey(pos);
        var count = levelRefCounts.getOrDefault(regionKey, 0) - 1;

        if (count <= 0) {
            // Last chunk in this region was unloaded - release the cached region data
            levelRefCounts.remove(regionKey);

            var levelRegions = loadedRegions.get(levelKey);

            if (levelRegions != null) {
                for (var namespaceRegions : levelRegions.values()) {
                    namespaceRegions.remove(regionKey);
                }
            }

            LOGGER.debug(
                "Released region cache for region ({}, {}) in dimension '{}'",
                (int) (regionKey >> 32),
                (int) regionKey,
                levelKey.location()
            );
        } else {
            levelRefCounts.put(regionKey, count);
        }
    }

    // ==================== Path Helper Methods ====================

    private static final int REGION_SIZE = 1 << REGION_SHIFT;

    /**
     * Gets the path for a region file.
     * <p>
     * Structure: {@code world/blib/data_storage/{namespace}/levels/{dimension}/chunks/r{size}.{rx}.{rz}.nbt}
     */
    private Path getRegionPath(ServerLevel level, String namespace, long regionKey) {
        var rx = (int) (regionKey >> 32);
        var rz = (int) regionKey;

        return getRegionFolder(level, namespace)
            .resolve("r" + REGION_SIZE + "." + rx + "." + rz + ".nbt");
    }

    private Path getRegionFolder(ServerLevel level, String namespace) {
        return DataStoreIO.getBlibDataPath(level.getServer())
            .resolve(namespace)
            .resolve(LEVELS_FOLDER)
            .resolve(DataStoreIO.getDimensionFolder(level))
            .resolve(CHUNKS_FOLDER);
    }

    // ==================== Region Coordinate Helper Methods ====================

    /**
     * Converts a chunk position to a region key (packed long with rx in high bits, rz in low bits).
     */
    private static long getRegionKey(ChunkPos pos) {
        var rx = pos.x >> REGION_SHIFT;
        var rz = pos.z >> REGION_SHIFT;

        return ((long) rx << 32) | (rz & 0xFFFFFFFFL);
    }

    private static Long parseRegionKey(String fileName) {
        var prefix = "r" + REGION_SIZE + ".";
        var suffix = ".nbt";

        if (!fileName.startsWith(prefix) || !fileName.endsWith(suffix)) {
            return null;
        }

        var coordinates = fileName.substring(prefix.length(), fileName.length() - suffix.length());
        var separator = coordinates.indexOf('.');

        if (separator < 0) {
            return null;
        }

        try {
            var rx = Integer.parseInt(coordinates.substring(0, separator));
            var rz = Integer.parseInt(coordinates.substring(separator + 1));

            return ((long) rx << 32) | (rz & 0xFFFFFFFFL);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets the chunk key for storage within a region file. Uses relative coordinates within the region (0-31, 0-31).
     */
    private static String getChunkKey(ChunkPos pos) {
        var relX = pos.x & REGION_MASK;
        var relZ = pos.z & REGION_MASK;

        return relX + "," + relZ;
    }

    private static ChunkPos parseChunkPos(long regionKey, String chunkKey) {
        var separator = chunkKey.indexOf(',');

        if (separator < 0) {
            return null;
        }

        try {
            var relX = Integer.parseInt(chunkKey.substring(0, separator));
            var relZ = Integer.parseInt(chunkKey.substring(separator + 1));

            if (relX < 0 || relX > REGION_MASK || relZ < 0 || relZ > REGION_MASK) {
                return null;
            }

            var rx = (int) (regionKey >> 32);
            var rz = (int) regionKey;

            return new ChunkPos((rx << REGION_SHIFT) + relX, (rz << REGION_SHIFT) + relZ);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
