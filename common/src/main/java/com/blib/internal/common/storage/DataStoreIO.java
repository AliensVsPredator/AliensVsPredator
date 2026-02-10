package com.blib.internal.common.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.blib.api.common.storage.v1.DataStore;

@ApiStatus.Internal
final class DataStoreIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataStoreIO.class);

    private static final String BLIB_DATA_FOLDER = "blib";

    private static final String DATA_STORAGE_FOLDER = "data_storage";

    private DataStoreIO() {
        throw new UnsupportedOperationException();
    }

    static Path getBlibDataPath(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(BLIB_DATA_FOLDER).resolve(DATA_STORAGE_FOLDER);
    }

    /**
     * Gets the dimension folder name for a level (e.g. {@code minecraft_overworld}).
     */
    static String getDimensionFolder(ServerLevel level) {
        var dimensionId = level.dimension().location();
        return dimensionId.getNamespace() + "_" + dimensionId.getPath();
    }

    static void loadStoreFromFile(Path path, DataStore store, ResourceLocation id) {
        try {
            var tag = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
            store.load(tag);
            LOGGER.debug("Loaded data store '{}' from {}", id, path);
        } catch (IOException e) {
            LOGGER.error("Failed to load data store '{}' from {}", id, path, e);
        }
    }

    static void saveStoreToFile(Path path, DataStore store) {
        var tag = new CompoundTag();
        store.save(tag);

        // Don't create empty files
        if (tag.isEmpty()) {
            // If file exists but data is now empty, delete it
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                    LOGGER.debug("Deleted empty data store file {}", path);
                } catch (IOException e) {
                    LOGGER.error("Failed to delete empty data store file {}", path, e);
                }
            }
            return;
        }

        try {
            Files.createDirectories(path.getParent());
            NbtIo.writeCompressed(tag, path);
            LOGGER.debug("Saved data store to {}", path);
        } catch (IOException e) {
            LOGGER.error("Failed to save data store to {}", path, e);
        }
    }
}
