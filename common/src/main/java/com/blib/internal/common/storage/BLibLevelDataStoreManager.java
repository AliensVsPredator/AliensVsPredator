package com.blib.internal.common.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.storage.v1.DataStore;
import com.blib.api.common.storage.v1.DataStoreType;

@ApiStatus.Internal
class BLibLevelDataStoreManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibLevelDataStoreManager.class);

    private static final String LEVELS_FOLDER = "levels";

    private final Map<ResourceKey<Level>, Map<ResourceLocation, DataStore>> stores = new HashMap<>();

    @SuppressWarnings("unchecked")
    <T extends DataStore> T get(ServerLevel level, BLibHolder<DataStoreType<T>> type) {
        var levelKey = level.dimension();
        var id = type.getResourceLocation();
        var levelStores = stores.computeIfAbsent(levelKey, k -> new HashMap<>());

        return (T) levelStores.computeIfAbsent(id, k -> loadOrCreate(level, type));
    }

    void save(ServerLevel level) {
        var levelKey = level.dimension();
        var levelStores = stores.get(levelKey);

        if (levelStores == null || levelStores.isEmpty()) {
            return;
        }

        for (var entry : levelStores.entrySet()) {
            var id = entry.getKey();
            var store = entry.getValue();

            DataStoreIO.saveStoreToFile(getStorePath(level, id), store);
        }
    }

    void clear() {
        stores.clear();
    }

    void clearLevel(ResourceKey<Level> levelKey) {
        stores.remove(levelKey);
    }

    private <T extends DataStore> T loadOrCreate(ServerLevel level, BLibHolder<DataStoreType<T>> type) {
        var id = type.getResourceLocation();
        var path = getStorePath(level, id);
        var store = type.value().createInstance();

        if (Files.exists(path)) {
            DataStoreIO.loadStoreFromFile(path, store, id);
        }

        return store;
    }

    /**
     * Gets the path for a level-specific data store file.
     * <p>
     * Structure: {@code world/blib/data_storage/{namespace}/levels/{dimension}/{path}.nbt}
     */
    private Path getStorePath(ServerLevel level, ResourceLocation id) {
        return DataStoreIO.getBlibDataPath(level.getServer())
            .resolve(id.getNamespace())
            .resolve(LEVELS_FOLDER)
            .resolve(DataStoreIO.getDimensionFolder(level))
            .resolve(id.getPath() + ".nbt");
    }
}
