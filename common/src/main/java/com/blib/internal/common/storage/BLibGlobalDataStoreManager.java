package com.blib.internal.common.storage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
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
import com.blib.internal.common.util.BLibSaveTiming;

@ApiStatus.Internal
class BLibGlobalDataStoreManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BLibGlobalDataStoreManager.class);

    private static final String GLOBAL_FOLDER = "global";

    private final Map<ResourceLocation, DataStore> stores = new HashMap<>();

    @SuppressWarnings("unchecked")
    <T extends DataStore> T get(MinecraftServer server, BLibHolder<DataStoreType<T>> type) {
        var id = type.getResourceLocation();

        return (T) stores.computeIfAbsent(id, k -> loadOrCreate(server, type));
    }

    void save(MinecraftServer server) {
        LOGGER.info("[BLib save timing] global data stores count={}", stores.size());

        for (var entry : stores.entrySet()) {
            var id = entry.getKey();
            var store = entry.getValue();

            BLibSaveTiming.time(
                LOGGER,
                "global store " + id,
                () -> DataStoreIO.saveStoreToFile(getStorePath(server, id), store)
            );
        }
    }

    void clear() {
        stores.clear();
    }

    private <T extends DataStore> T loadOrCreate(MinecraftServer server, BLibHolder<DataStoreType<T>> type) {
        var id = type.getResourceLocation();
        var path = getStorePath(server, id);
        var store = type.value().createInstance();

        if (Files.exists(path)) {
            DataStoreIO.loadStoreFromFile(path, store, id);
        }

        return store;
    }

    /**
     * Gets the path for a global data store file.
     * <p>
     * Structure: {@code world/blib/data_storage/{namespace}/global/{path}.nbt}
     */
    private Path getStorePath(MinecraftServer server, ResourceLocation id) {
        return DataStoreIO.getBlibDataPath(server)
            .resolve(id.getNamespace())
            .resolve(GLOBAL_FOLDER)
            .resolve(id.getPath() + ".nbt");
    }
}
