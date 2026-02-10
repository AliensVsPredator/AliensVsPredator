package com.blib.internal.common.faction.io;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.blib.api.common.faction.v1.FactionRelationships;
import com.blib.internal.common.faction.serializer.FactionRelationshipsSerializer;
import com.blib.internal.common.util.ShardManager;

@ApiStatus.Internal
public final class FactionRelationshipsIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactionRelationshipsIO.class);

    private static final String KEY_RELATIONSHIPS = "relationships";

    private static final Pattern SHARD_FILE_PATTERN = Pattern.compile("faction_relationships_(\\d+)\\.nbt");

    private FactionRelationshipsIO() {
        throw new UnsupportedOperationException();
    }

    public static void loadAll(
        MinecraftServer server,
        Map<ResourceLocation, FactionRelationships> relationships,
        ShardManager<ResourceLocation> shardManager
    ) {
        var relDir = FactionIO.getRelationshipsDirectory(server);

        if (!Files.isDirectory(relDir)) {
            return;
        }

        try (var stream = Files.list(relDir)) {
            stream
                .filter(p -> SHARD_FILE_PATTERN.matcher(p.getFileName().toString()).matches())
                .forEach(shardFile -> loadShard(shardFile, relationships, shardManager));
        } catch (IOException e) {
            LOGGER.error("Failed to list faction relationship shard files in {}", relDir, e);
        }
    }

    public static void saveShard(
        MinecraftServer server,
        List<FactionRelationships> entriesInShard,
        int shardIndex
    ) {
        var relationshipsTag = new CompoundTag();

        for (var rel : entriesInShard) {
            relationshipsTag.put(rel.getId().toString(), FactionRelationshipsSerializer.serialize(rel));
        }

        var rootTag = new CompoundTag();
        rootTag.put(KEY_RELATIONSHIPS, relationshipsTag);

        FactionIO.writeCompressed(FactionIO.getRelationshipsShardPath(server, shardIndex), rootTag);
    }

    private static void loadShard(
        Path shardFile,
        Map<ResourceLocation, FactionRelationships> relationships,
        ShardManager<ResourceLocation> shardManager
    ) {
        var shardIndex = parseShardIndex(shardFile);

        if (shardIndex < 0) {
            LOGGER.warn("Could not parse shard index from file: {}", shardFile);
            return;
        }

        var tag = FactionIO.readCompressed(shardFile);
        var relationshipsTag = tag.getCompound(KEY_RELATIONSHIPS);

        for (var key : relationshipsTag.getAllKeys()) {
            var factionTag = relationshipsTag.getCompound(key);
            var factionRelationships = FactionRelationshipsSerializer.deserialize(factionTag);
            var factionId = factionRelationships.getId();

            factionRelationships.clearDirty();
            relationships.put(factionId, factionRelationships);
            shardManager.recordShardEntry(factionId, shardIndex);
        }
    }

    private static int parseShardIndex(Path shardFile) {
        var matcher = SHARD_FILE_PATTERN.matcher(shardFile.getFileName().toString());

        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }

        return -1;
    }
}
