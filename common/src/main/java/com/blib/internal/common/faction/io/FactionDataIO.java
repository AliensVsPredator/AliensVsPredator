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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.blib.api.common.faction.v1.FactionData;
import com.blib.api.common.registry.v1.BLibBuiltInRegistries;
import com.blib.internal.common.faction.serializer.FactionDataSerializer;

@ApiStatus.Internal
public final class FactionDataIO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FactionDataIO.class);

    private static final String KEY_DATA = "data";

    private static final Pattern SHARD_FILE_PATTERN = Pattern.compile("faction_data_(\\d+)\\.nbt");

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
                    var namespace = namespaceDir.getFileName().toString();
                    var dataDir = FactionIO.getDataDirectory(server, namespace);

                    if (!Files.isDirectory(dataDir)) {
                        return;
                    }

                    try (var files = Files.list(dataDir)) {
                        files
                            .filter(p -> SHARD_FILE_PATTERN.matcher(p.getFileName().toString()).matches())
                            .forEach(shardFile -> loadShard(shardFile, knownFactionIds, data, factionIdToTypeId));
                    } catch (IOException e) {
                        LOGGER.error("Failed to list faction data shard files in {}", dataDir, e);
                    }
                });
        } catch (IOException e) {
            LOGGER.error("Failed to list namespace directories in {}", baseDir, e);
        }
    }

    public static void saveShard(
        MinecraftServer server,
        List<ResourceLocation> factionIdsInShard,
        Map<ResourceLocation, FactionData> data,
        Map<ResourceLocation, ResourceLocation> factionIdToTypeId,
        int shardIndex
    ) {
        var namespaceToFactionsTag = new HashMap<String, CompoundTag>();

        for (var factionId : factionIdsInShard) {
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
                factionData.clearDirty();
                data.put(factionId, factionData);
            } else {
                LOGGER.warn("Unknown faction type '{}' for faction '{}', skipping data creation", typeId, factionId);
            }
        }
    }
}
