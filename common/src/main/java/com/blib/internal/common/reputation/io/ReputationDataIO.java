package com.blib.internal.common.reputation.io;

import net.minecraft.nbt.CompoundTag;
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

import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationSubject;
import com.blib.internal.common.reputation.serializer.ReputationDataSerializer;
import com.blib.internal.common.util.ShardManager;

@ApiStatus.Internal
public final class ReputationDataIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReputationDataIO.class);

    private static final String KEY_DATA = "data";

    private static final Pattern SHARD_FILE_PATTERN = Pattern.compile("reputation_(\\d+)\\.nbt");

    private ReputationDataIO() {
        throw new UnsupportedOperationException();
    }

    public static void loadAll(
        MinecraftServer server,
        Map<ReputationSubject, ReputationData> data,
        ShardManager<ReputationSubject> shardManager
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
        var dataTag = new CompoundTag();

        for (var reputationData : entriesInShard) {
            var subjectKey = ReputationDataSerializer.serializeSubjectKey(reputationData.getSubject());
            dataTag.put(subjectKey, ReputationDataSerializer.serialize(reputationData));
        }

        var rootTag = new CompoundTag();
        rootTag.put(KEY_DATA, dataTag);

        ReputationIO.writeCompressed(ReputationIO.getReputationShardPath(server, shardIndex), rootTag);
    }

    private static void loadShard(
        Path shardFile,
        Map<ReputationSubject, ReputationData> data,
        ShardManager<ReputationSubject> shardManager
    ) {
        var shardIndex = parseShardIndex(shardFile);

        if (shardIndex < 0) {
            LOGGER.warn("Could not parse shard index from file: {}", shardFile);
            return;
        }

        var tag = ReputationIO.readCompressed(shardFile);
        var dataTag = tag.getCompound(KEY_DATA);

        for (var key : dataTag.getAllKeys()) {
            var entryTag = dataTag.getCompound(key);
            var reputationData = ReputationDataSerializer.deserialize(key, entryTag);
            var reputationSubject = reputationData.getSubject();

            reputationData.clearDirty();
            data.put(reputationSubject, reputationData);
            shardManager.recordShardEntry(reputationSubject, shardIndex);
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
