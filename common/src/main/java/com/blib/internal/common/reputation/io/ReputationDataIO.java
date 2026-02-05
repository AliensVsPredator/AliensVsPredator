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

import com.blib.api.common.reputation.v1.ReputationData;
import com.blib.api.common.reputation.v1.ReputationSubject;
import com.blib.internal.common.reputation.serializer.ReputationDataSerializer;
import com.blib.internal.common.util.ShardUtil;

@ApiStatus.Internal
public final class ReputationDataIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReputationDataIO.class);

    private static final String KEY_DATA = "data";

    private ReputationDataIO() {
        throw new UnsupportedOperationException();
    }

    public static void loadAll(MinecraftServer server, Map<ReputationSubject, ReputationData> data) {
        var reputationDir = ReputationIO.getReputationDirectory(server);

        if (!Files.isDirectory(reputationDir)) {
            return;
        }

        try (var stream = Files.list(reputationDir)) {
            stream
                .filter(p -> p.getFileName().toString().startsWith("reputation_") && p.getFileName().toString().endsWith(".nbt"))
                .forEach(shardFile -> loadShard(shardFile, data));
        } catch (IOException e) {
            LOGGER.error("Failed to list reputation shard files in {}", reputationDir, e);
        }
    }

    public static void saveShard(
        MinecraftServer server,
        Map<ReputationSubject, ReputationData> data,
        List<ReputationSubject> subjectList,
        int shardIndex,
        int shardSize
    ) {
        int startIndex = ShardUtil.shardStart(shardIndex, shardSize);
        int endIndex = ShardUtil.shardEnd(shardIndex, shardSize, subjectList.size());

        var dataTag = new CompoundTag();
        for (int i = startIndex; i < endIndex; i++) {
            var subject = subjectList.get(i);
            var reputationData = data.get(subject);

            if (reputationData != null) {
                var subjectKey = ReputationDataSerializer.serializeSubjectKey(subject);
                dataTag.put(subjectKey, ReputationDataSerializer.serialize(reputationData));
            }
        }

        var rootTag = new CompoundTag();
        rootTag.put(KEY_DATA, dataTag);

        ReputationIO.writeCompressed(ReputationIO.getReputationShardPath(server, shardIndex), rootTag);
    }

    private static void loadShard(Path shardFile, Map<ReputationSubject, ReputationData> data) {
        var tag = ReputationIO.readCompressed(shardFile);
        var dataTag = tag.getCompound(KEY_DATA);

        for (var key : dataTag.getAllKeys()) {
            var entryTag = dataTag.getCompound(key);
            var reputationData = ReputationDataSerializer.deserialize(key, entryTag);
            reputationData.clearDirty();
            data.put(reputationData.getSubject(), reputationData);
        }
    }
}
