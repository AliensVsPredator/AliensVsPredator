package com.blib.internal.common.reputation.io;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApiStatus.Internal
public final class ReputationIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReputationIO.class);

    private static final String BLIB_DATA_FOLDER = "blib";

    private static final String DATA_STORAGE_FOLDER = "data_storage";

    private static final String GLOBAL_FOLDER = "global";

    private static final String REPUTATIONS_FOLDER = "reputations";

    private ReputationIO() {
        throw new UnsupportedOperationException();
    }

    public static Path getBlibDataPath(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(BLIB_DATA_FOLDER).resolve(DATA_STORAGE_FOLDER);
    }

    public static Path getReputationShardPath(MinecraftServer server, int shardIndex) {
        return getReputationDirectory(server).resolve(REPUTATIONS_FOLDER + "_" + shardIndex + ".nbt");
    }

    public static Path getReputationDirectory(MinecraftServer server) {
        return getBlibDataPath(server)
            .resolve(BLIB_DATA_FOLDER)
            .resolve(GLOBAL_FOLDER)
            .resolve(REPUTATIONS_FOLDER);
    }

    public static CompoundTag readCompressed(Path path) {
        try {
            return NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            LOGGER.error("Failed to read reputation data from {}", path, e);
            return new CompoundTag();
        }
    }

    public static void writeCompressed(Path path, CompoundTag tag) {
        if (tag.isEmpty()) {
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                    LOGGER.debug("Deleted empty reputation file {}", path);
                } catch (IOException e) {
                    LOGGER.error("Failed to delete empty reputation file {}", path, e);
                }
            }
            return;
        }

        try {
            Files.createDirectories(path.getParent());
            NbtIo.writeCompressed(tag, path);
            LOGGER.debug("Saved reputation data to {}", path);
        } catch (IOException e) {
            LOGGER.error("Failed to save reputation data to {}", path, e);
        }
    }
}
