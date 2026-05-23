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

import com.blib.api.common.faction.v1.FactionMembership;
import com.blib.internal.common.faction.serializer.FactionMembershipSerializer;
import com.blib.internal.common.util.ShardManager;

@ApiStatus.Internal
public final class FactionMembershipIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactionMembershipIO.class);

    private static final String KEY_MEMBERSHIPS = "memberships";

    private static final Pattern SHARD_FILE_PATTERN = Pattern.compile("faction_memberships_(\\d+)\\.nbt");

    private FactionMembershipIO() {
        throw new UnsupportedOperationException();
    }

    public static void loadAll(
        MinecraftServer server,
        Map<ResourceLocation, FactionMembership> memberships,
        ShardManager<ResourceLocation> shardManager
    ) {
        var membershipDir = FactionIO.getMembershipDirectory(server);

        if (!Files.isDirectory(membershipDir)) {
            return;
        }

        try (var stream = Files.list(membershipDir)) {
            stream
                .filter(p -> SHARD_FILE_PATTERN.matcher(p.getFileName().toString()).matches())
                .forEach(shardFile -> loadShard(shardFile, memberships, shardManager));
        } catch (IOException e) {
            LOGGER.error("Failed to list faction membership shard files in {}", membershipDir, e);
        }
    }

    public static void saveShard(
        MinecraftServer server,
        List<FactionMembership> entriesInShard,
        int shardIndex
    ) {
        var membershipsTag = new CompoundTag();

        for (var membership : entriesInShard) {
            membershipsTag.put(membership.getId().toString(), FactionMembershipSerializer.serialize(membership));
        }

        var rootTag = new CompoundTag();
        rootTag.put(KEY_MEMBERSHIPS, membershipsTag);

        FactionIO.writeCompressed(FactionIO.getMembershipShardPath(server, shardIndex), rootTag);
    }

    private static void loadShard(
        Path shardFile,
        Map<ResourceLocation, FactionMembership> memberships,
        ShardManager<ResourceLocation> shardManager
    ) {
        var shardIndex = parseShardIndex(shardFile);

        if (shardIndex < 0) {
            LOGGER.warn("Could not parse shard index from file: {}", shardFile);
            return;
        }

        var tag = FactionIO.readCompressed(shardFile);
        var membershipsTag = tag.getCompound(KEY_MEMBERSHIPS);

        for (var key : membershipsTag.getAllKeys()) {
            var factionTag = membershipsTag.getCompound(key);
            var membership = FactionMembershipSerializer.deserialize(factionTag);
            var factionId = membership.getId();

            membership.clearDirty();
            memberships.put(factionId, membership);
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
