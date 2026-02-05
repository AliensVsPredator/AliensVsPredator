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

import com.blib.api.common.faction.v1.FactionRelationships;
import com.blib.internal.common.faction.serializer.FactionRelationshipsSerializer;
import com.blib.internal.common.util.ShardUtil;

@ApiStatus.Internal
public final class FactionRelationshipsIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactionRelationshipsIO.class);

    private static final String KEY_RELATIONSHIPS = "relationships";

    private FactionRelationshipsIO() {
        throw new UnsupportedOperationException();
    }

    public static void loadAll(MinecraftServer server, Map<ResourceLocation, FactionRelationships> relationships) {
        var relDir = FactionIO.getRelationshipsDirectory(server);

        if (!Files.isDirectory(relDir)) {
            return;
        }

        try (var stream = Files.list(relDir)) {
            stream
                .filter(p -> p.getFileName().toString().startsWith("faction_relationships_") && p.getFileName().toString().endsWith(".nbt"))
                .forEach(shardFile -> loadShard(shardFile, relationships));
        } catch (IOException e) {
            LOGGER.error("Failed to list faction relationship shard files in {}", relDir, e);
        }
    }

    public static void saveShard(
        MinecraftServer server,
        Map<ResourceLocation, FactionRelationships> relationships,
        List<ResourceLocation> idList,
        int shardIndex,
        int shardSize
    ) {
        int startIndex = ShardUtil.shardStart(shardIndex, shardSize);
        int endIndex = ShardUtil.shardEnd(shardIndex, shardSize, idList.size());

        var relationshipsTag = new CompoundTag();
        for (int i = startIndex; i < endIndex; i++) {
            var factionId = idList.get(i);
            var rel = relationships.get(factionId);

            if (rel != null) {
                relationshipsTag.put(factionId.toString(), FactionRelationshipsSerializer.serialize(rel));
            }
        }

        var rootTag = new CompoundTag();
        rootTag.put(KEY_RELATIONSHIPS, relationshipsTag);

        FactionIO.writeCompressed(FactionIO.getRelationshipsShardPath(server, shardIndex), rootTag);
    }

    private static void loadShard(Path shardFile, Map<ResourceLocation, FactionRelationships> relationships) {
        var tag = FactionIO.readCompressed(shardFile);
        var relationshipsTag = tag.getCompound(KEY_RELATIONSHIPS);

        for (var key : relationshipsTag.getAllKeys()) {
            var factionTag = relationshipsTag.getCompound(key);
            var factionRelationships = FactionRelationshipsSerializer.deserialize(factionTag);
            // Clear dirty since we just loaded.
            factionRelationships.clearDirty();
            relationships.put(factionRelationships.getId(), factionRelationships);
        }
    }
}
