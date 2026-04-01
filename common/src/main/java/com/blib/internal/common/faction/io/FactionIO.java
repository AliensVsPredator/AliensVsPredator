package com.blib.internal.common.faction.io;

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
public final class FactionIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactionIO.class);

    private static final String BLIB_DATA_FOLDER = "blib";

    private static final String DATA_STORAGE_FOLDER = "data_storage";

    private static final String GLOBAL_FOLDER = "global";

    private static final String MEMBERSHIPS_FOLDER = "faction_memberships";

    private static final String DATA_FOLDER = "faction_data";

    private static final String RELATIONSHIPS_FILE = "faction_relationships.nbt";

    private FactionIO() {
        throw new UnsupportedOperationException();
    }

    public static Path getBlibDataPath(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT).resolve(BLIB_DATA_FOLDER).resolve(DATA_STORAGE_FOLDER);
    }

    public static Path getMembershipShardPath(MinecraftServer server, int shardIndex) {
        return getMembershipDirectory(server).resolve(MEMBERSHIPS_FOLDER + "_" + shardIndex + ".nbt");
    }

    public static Path getRelationshipsPath(MinecraftServer server) {
        return getBlibDataPath(server)
            .resolve(BLIB_DATA_FOLDER)
            .resolve(GLOBAL_FOLDER)
            .resolve(RELATIONSHIPS_FILE);
    }

    public static Path getDataShardPath(MinecraftServer server, String namespace, int shardIndex) {
        return getDataDirectory(server, namespace).resolve(DATA_FOLDER + "_" + shardIndex + ".nbt");
    }

    public static Path getMembershipDirectory(MinecraftServer server) {
        return getBlibDataPath(server)
            .resolve(BLIB_DATA_FOLDER)
            .resolve(GLOBAL_FOLDER)
            .resolve(MEMBERSHIPS_FOLDER);
    }

    public static Path getDataDirectory(MinecraftServer server, String namespace) {
        return getBlibDataPath(server)
            .resolve(namespace)
            .resolve(GLOBAL_FOLDER)
            .resolve(DATA_FOLDER);
    }

    public static CompoundTag readCompressed(Path path) {
        try {
            return NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            LOGGER.error("Failed to read faction data from {}", path, e);
            return new CompoundTag();
        }
    }

    public static void writeCompressed(Path path, CompoundTag tag) {
        if (tag.isEmpty()) {
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                    LOGGER.debug("Deleted empty faction file {}", path);
                } catch (IOException e) {
                    LOGGER.error("Failed to delete empty faction file {}", path, e);
                }
            }
            return;
        }

        try {
            Files.createDirectories(path.getParent());
            NbtIo.writeCompressed(tag, path);
            LOGGER.debug("Saved faction data to {}", path);
        } catch (IOException e) {
            LOGGER.error("Failed to save faction data to {}", path, e);
        }
    }
}
