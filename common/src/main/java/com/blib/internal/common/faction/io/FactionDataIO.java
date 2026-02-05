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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.internal.common.faction.serializer.FactionDataSerializer;
import com.blib.internal.common.util.ShardUtil;

@ApiStatus.Internal
public final class FactionDataIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactionDataIO.class);

    private static final String KEY_DATA = "data";

    private FactionDataIO() {
        throw new UnsupportedOperationException();
    }

    public static void loadAll(
        MinecraftServer server,
        Set<ResourceLocation> knownFactionIds,
        Map<ResourceLocation, FactionData> data,
        Map<ResourceLocation, ResourceLocation> factionIdToTypeId
    ) {
        var baseDir = FactionIO.getBlibDataPath(server);

        if (!Files.isDirectory(baseDir)) {
            return;
        }

        try (var namespaceDirs = Files.list(baseDir)) {
            namespaceDirs
                .filter(Files::isDirectory)
                .forEach(namespaceDir -> {
                    var globalDir = namespaceDir.resolve("global");

                    if (!Files.isDirectory(globalDir)) {
                        return;
                    }

                    try (var files = Files.list(globalDir)) {
                        files
                            .filter(
                                p -> p.getFileName().toString().startsWith("faction_data_") && p.getFileName().toString().endsWith(".nbt")
                            )
                            .forEach(shardFile -> loadShard(shardFile, knownFactionIds, data, factionIdToTypeId));
                    } catch (IOException e) {
                        LOGGER.error("Failed to list faction data shard files in {}", globalDir, e);
                    }
                });
        } catch (IOException e) {
            LOGGER.error("Failed to list namespace directories in {}", baseDir, e);
        }
    }

    public static void saveShard(
        MinecraftServer server,
        Map<ResourceLocation, FactionData> data,
        Map<ResourceLocation, ResourceLocation> factionIdToTypeId,
        List<ResourceLocation> idList,
        int shardIndex,
        int shardSize
    ) {
        int startIndex = ShardUtil.shardStart(shardIndex, shardSize);
        int endIndex = ShardUtil.shardEnd(shardIndex, shardSize, idList.size());

        Map<String, CompoundTag> namespaceToFactionsTag = new LinkedHashMap<>();
        for (int i = startIndex; i < endIndex; i++) {
            var factionId = idList.get(i);
            var factionData = data.get(factionId);
            var typeId = factionIdToTypeId.get(factionId);

            if (factionData != null && typeId != null) {
                var namespace = typeId.getNamespace();
                var factionsTag = namespaceToFactionsTag.computeIfAbsent(namespace, k -> new CompoundTag());

                factionsTag.put(factionId.toString(), FactionDataSerializer.serialize(factionData, typeId));
            }
        }

        for (var entry : namespaceToFactionsTag.entrySet()) {
            var rootTag = new CompoundTag();
            rootTag.put(KEY_DATA, entry.getValue());

            FactionIO.writeCompressed(FactionIO.getDataShardPath(server, entry.getKey(), shardIndex), rootTag);
        }
    }

    private static void loadShard(
        Path shardFile,
        Set<ResourceLocation> knownFactionIds,
        Map<ResourceLocation, FactionData> data,
        Map<ResourceLocation, ResourceLocation> factionIdToTypeId
    ) {
        var tag = FactionIO.readCompressed(shardFile);
        var factionsTag = tag.getCompound(KEY_DATA);

        for (var key : factionsTag.getAllKeys()) {
            var factionId = ResourceLocation.parse(key);

            if (!knownFactionIds.contains(factionId)) {
                continue;
            }

            var entryTag = factionsTag.getCompound(key);
            var typeId = FactionDataSerializer.deserializeTypeId(entryTag);

            factionIdToTypeId.put(factionId, typeId);

            var factionType = BLibBuiltInRegistries.FACTION_TYPES.get(typeId);

            if (factionType != null) {
                var factionData = factionType.createInstance();
                factionData.load(FactionDataSerializer.deserializeData(entryTag));
                // Clear dirty since we just loaded.
                factionData.clearDirty();
                data.put(factionId, factionData);
            } else {
                LOGGER.warn("Unknown faction type '{}' for faction '{}', skipping data creation", typeId, factionId);
            }
        }
    }
}
