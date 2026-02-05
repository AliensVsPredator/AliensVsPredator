package com.blib.internal.common.reputation.io;

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
import java.util.UUID;
import java.util.regex.Pattern;

import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationKey;
import com.blib.internal.common.reputation.serializer.ReputationDataSerializer;
import com.blib.internal.common.util.ShardManager;

@ApiStatus.Internal
public final class ReputationDataIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReputationDataIO.class);

    private static final String KEY_FACTIONS = "factions";

    private static final String KEY_ENTITIES = "entities";

    private static final Pattern SHARD_FILE_PATTERN = Pattern.compile("reputations_(\\d+)\\.nbt");

    private ReputationDataIO() {
        throw new UnsupportedOperationException();
    }

    public static void loadAll(
        MinecraftServer server,
        Map<ReputationKey, ReputationData> data,
        ShardManager<ReputationKey> shardManager
    ) {
        var reputationDir = ReputationIO.getReputationDirectory(server);

        if (!Files.isDirectory(reputationDir)) {
            return;
        }

        try (var stream = Files.list(reputationDir)) {
            stream
                .filter(p -> SHARD_FILE_PATTERN.matcher(p.getFileName().toString()).matches())
                .forEach(shardFile -> loadShard(shardFile, data, shardManager));
        } catch (IOException e) {
            LOGGER.error("Failed to list reputation shard files in {}", reputationDir, e);
        }
    }

    public static void saveShard(
        MinecraftServer server,
        List<ReputationData> entriesInShard,
        int shardIndex
    ) {
        var factionsTag = new CompoundTag();
        var entitiesTag = new CompoundTag();

        for (var reputationData : entriesInShard) {
            switch (reputationData.getKey()) {
                case ReputationKey.Faction(var factionId) ->
                    factionsTag.put(factionId.toString(), ReputationDataSerializer.serialize(reputationData));
                case ReputationKey.Entity(var uuid) ->
                    entitiesTag.put(uuid.toString(), ReputationDataSerializer.serialize(reputationData));
            }
        }

        var rootTag = new CompoundTag();

        if (!factionsTag.isEmpty()) {
            rootTag.put(KEY_FACTIONS, factionsTag);
        }

        if (!entitiesTag.isEmpty()) {
            rootTag.put(KEY_ENTITIES, entitiesTag);
        }

        ReputationIO.writeCompressed(ReputationIO.getReputationShardPath(server, shardIndex), rootTag);
    }

    private static void loadShard(
        Path shardFile,
        Map<ReputationKey, ReputationData> data,
        ShardManager<ReputationKey> shardManager
    ) {
        var shardIndex = parseShardIndex(shardFile);

        if (shardIndex < 0) {
            LOGGER.warn("Could not parse shard index from file: {}", shardFile);
            return;
        }

        var tag = ReputationIO.readCompressed(shardFile);

        if (tag.contains(KEY_FACTIONS)) {
            var factionsTag = tag.getCompound(KEY_FACTIONS);

            for (var key : factionsTag.getAllKeys()) {
                var factionId = ResourceLocation.parse(key);
                var reputationKey = new ReputationKey.Faction(factionId);
                var entryTag = factionsTag.getCompound(key);
                var reputationData = ReputationDataSerializer.deserialize(reputationKey, entryTag);

                reputationData.clearDirty();
                data.put(reputationKey, reputationData);
                shardManager.recordShardEntry(reputationKey, shardIndex);
            }
        }

        if (tag.contains(KEY_ENTITIES)) {
            var entitiesTag = tag.getCompound(KEY_ENTITIES);

            for (var key : entitiesTag.getAllKeys()) {
                var uuid = UUID.fromString(key);
                var reputationKey = new ReputationKey.Entity(uuid);
                var entryTag = entitiesTag.getCompound(key);
                var reputationData = ReputationDataSerializer.deserialize(reputationKey, entryTag);

                reputationData.clearDirty();
                data.put(reputationKey, reputationData);
                shardManager.recordShardEntry(reputationKey, shardIndex);
            }
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
