package com.blib.internal.common.entityreference;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
public final class EntityReferenceIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityReferenceIO.class);

    private static final String BLIB_DATA_FOLDER = "blib";

    private static final String DATA_STORAGE_FOLDER = "data_storage";

    private static final String GLOBAL_FOLDER = "global";

    private static final String ENTITY_LAST_SEEN_FILE = "entity_last_seen.nbt";

    private static final String LEGACY_FACTION_MEMBER_LOCATIONS_FILE = "faction_member_locations.nbt";

    private static final String KEY_ENTITIES = "entities";

    private static final String KEY_LEGACY_MEMBERS = "members";

    private static final String KEY_UUID = "uuid";

    private static final String KEY_DIMENSION = "dimension";

    private static final String KEY_CHUNK_X = "chunkX";

    private static final String KEY_CHUNK_Z = "chunkZ";

    private static final String KEY_TICK = "tick";

    private EntityReferenceIO() {
        throw new UnsupportedOperationException();
    }

    public static List<EntityLastSeen> load(MinecraftServer server) {
        var path = getEntityLastSeenPath(server);
        var listKey = KEY_ENTITIES;
        if (!Files.exists(path)) {
            path = getLegacyFactionMemberLocationsPath(server);
            listKey = KEY_LEGACY_MEMBERS;

            if (!Files.exists(path)) {
                return List.of();
            }
        }

        var root = readCompressed(path);
        var list = root.getList(listKey, Tag.TAG_COMPOUND);
        var entries = new ArrayList<EntityLastSeen>();

        for (var i = 0; i < list.size(); i++) {
            var tag = list.getCompound(i);
            if (!tag.contains(KEY_UUID) || !tag.contains(KEY_DIMENSION)) {
                continue;
            }

            var uuid = UUIDUtil.uuidFromIntArray(tag.getIntArray(KEY_UUID));
            var dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString(KEY_DIMENSION)));
            entries.add(
                new EntityLastSeen(
                    uuid,
                    dimension,
                    tag.getInt(KEY_CHUNK_X),
                    tag.getInt(KEY_CHUNK_Z),
                    tag.getLong(KEY_TICK)
                )
            );
        }

        return entries;
    }

    public static boolean hasLegacyData(MinecraftServer server) {
        return Files.exists(getLegacyFactionMemberLocationsPath(server));
    }

    public static void save(MinecraftServer server, List<EntityLastSeen> entries) {
        if (entries.isEmpty()) {
            writeCompressed(getEntityLastSeenPath(server), new CompoundTag());
            deleteLegacyFactionMemberLocations(server);
            return;
        }

        var list = new ListTag();
        for (var entry : entries) {
            var tag = new CompoundTag();
            tag.put(KEY_UUID, new IntArrayTag(UUIDUtil.uuidToIntArray(entry.uuid())));
            tag.putString(KEY_DIMENSION, entry.dimension().location().toString());
            tag.putInt(KEY_CHUNK_X, entry.chunkX());
            tag.putInt(KEY_CHUNK_Z, entry.chunkZ());
            tag.putLong(KEY_TICK, entry.tick());
            list.add(tag);
        }

        var root = new CompoundTag();
        root.put(KEY_ENTITIES, list);
        writeCompressed(getEntityLastSeenPath(server), root);
        deleteLegacyFactionMemberLocations(server);
    }

    private static Path getEntityLastSeenPath(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT)
            .resolve(BLIB_DATA_FOLDER)
            .resolve(DATA_STORAGE_FOLDER)
            .resolve(BLIB_DATA_FOLDER)
            .resolve(GLOBAL_FOLDER)
            .resolve(ENTITY_LAST_SEEN_FILE);
    }

    private static Path getLegacyFactionMemberLocationsPath(MinecraftServer server) {
        return server.getWorldPath(LevelResource.ROOT)
            .resolve(BLIB_DATA_FOLDER)
            .resolve(DATA_STORAGE_FOLDER)
            .resolve(BLIB_DATA_FOLDER)
            .resolve(GLOBAL_FOLDER)
            .resolve(LEGACY_FACTION_MEMBER_LOCATIONS_FILE);
    }

    private static CompoundTag readCompressed(Path path) {
        try {
            return NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
        } catch (IOException e) {
            LOGGER.error("Failed to read entity reference data from {}", path, e);
            return new CompoundTag();
        }
    }

    private static void writeCompressed(Path path, CompoundTag tag) {
        if (tag.isEmpty()) {
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                    LOGGER.debug("Deleted empty entity reference file {}", path);
                } catch (IOException e) {
                    LOGGER.error("Failed to delete empty entity reference file {}", path, e);
                }
            }
            return;
        }

        try {
            Files.createDirectories(path.getParent());
            NbtIo.writeCompressed(tag, path);
            LOGGER.debug("Saved entity reference data to {}", path);
        } catch (IOException e) {
            LOGGER.error("Failed to save entity reference data to {}", path, e);
        }
    }

    private static void deleteLegacyFactionMemberLocations(MinecraftServer server) {
        var path = getLegacyFactionMemberLocationsPath(server);
        if (!Files.exists(path)) {
            return;
        }

        try {
            Files.delete(path);
            LOGGER.debug("Deleted legacy faction member location file {}", path);
        } catch (IOException e) {
            LOGGER.error("Failed to delete legacy faction member location file {}", path, e);
        }
    }
}
