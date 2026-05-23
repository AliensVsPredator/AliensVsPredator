package com.blib.mod.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedHashMap;
import java.util.Map;

import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.api.common.dismemberment.v1.LimbCategory;
import com.blib.api.common.dismemberment.v1.LimbDefinition;
import com.blib.api.common.dismemberment.v1.LimbDefinitionRegistry;
import com.blib.api.common.dismemberment.v1.LimbPoseOption;
import com.blib.api.common.dismemberment.v1.SpawnFunctionRegistry;
import com.blib.internal.client.faction.ClientFactionCache;
import com.blib.internal.client.territory.ClientTerritoryCache;
import com.blib.mod.client.render.debug.PathfindingDebugState;
import com.blib.mod.client.render.goap.GOAPDebugState;
import com.blib.mod.common.network.packet.S2CChunkClaimsSyncPayload;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CFactionMetadataSyncPayload;
import com.blib.mod.common.network.packet.S2CGOAPDebugPayload;
import com.blib.mod.common.network.packet.S2CLimbDefinitionsSyncPayload;
import com.blib.mod.common.network.packet.S2CPathfindingNavDebugPayload;
import com.blib.mod.common.network.packet.S2CPathfindingSearchDebugPayload;

@ApiStatus.Internal
public final class BLibClientListener {

    public static void handleChunkClaimsSync(S2CChunkClaimsSyncPayload payload, Player player) {
        if (payload.replaceArea()) {
            ClientTerritoryCache.INSTANCE.replaceArea(
                payload.dimension(),
                payload.minChunkX(),
                payload.minChunkZ(),
                payload.maxChunkX(),
                payload.maxChunkZ(),
                payload.entries()
            );
        } else {
            ClientTerritoryCache.INSTANCE.updateChunks(payload.dimension(), payload.entries());
        }
    }

    public static void handleFactionMetadataSync(S2CFactionMetadataSyncPayload payload, Player player) {
        ClientFactionCache.INSTANCE.update(payload.factionId(), payload.name(), payload.color());
    }

    public static void handleLimbDefinitionsSync(S2CLimbDefinitionsSyncPayload payload, Player player) {
        var next =
            new LinkedHashMap<ResourceLocation, Map<ResourceLocation, LimbDefinition>>();
        var parents = new LinkedHashMap<ResourceLocation, ResourceLocation>();
        for (var bucket : payload.entries()) {
            var perEntity = new LinkedHashMap<ResourceLocation, LimbDefinition>();
            for (var entry : bucket.limbs()) {
                var poses = entry.poses()
                    .stream()
                    .map(pose -> new LimbPoseOption(pose.id(), pose.weight()))
                    .toList();
                var def = new LimbDefinition(
                    entry.limbId(),
                    new LimbCategory(entry.categoryId()),
                    SpawnFunctionRegistry.DEFAULT_PROVIDER,
                    entry.fatal(),
                    poses
                );
                perEntity.put(entry.limbId(), def);
            }
            next.put(bucket.entityTypeId(), perEntity);
            readResourceLocation(bucket.parentTemplateId()).ifPresent(parent -> parents.put(bucket.entityTypeId(), parent));
        }

        var templates =
            new LinkedHashMap<ResourceLocation, Map<ResourceLocation, LimbDefinition>>();
        var templateParents = new LinkedHashMap<ResourceLocation, ResourceLocation>();
        for (var bucket : payload.templates()) {
            var perTemplate = new LinkedHashMap<ResourceLocation, LimbDefinition>();
            for (var entry : bucket.limbs()) {
                var poses = entry.poses()
                    .stream()
                    .map(pose -> new LimbPoseOption(pose.id(), pose.weight()))
                    .toList();
                var def = new LimbDefinition(
                    entry.limbId(),
                    new LimbCategory(entry.categoryId()),
                    SpawnFunctionRegistry.DEFAULT_PROVIDER,
                    entry.fatal(),
                    poses
                );
                perTemplate.put(entry.limbId(), def);
            }
            templates.put(bucket.templateId(), perTemplate);
            readResourceLocation(bucket.parentTemplateId()).ifPresent(parent -> templateParents.put(bucket.templateId(), parent));
        }
        LimbDefinitionRegistry.replaceTier2(next, parents, templates, templateParents);
    }

    private static java.util.Optional<ResourceLocation> readResourceLocation(String value) {
        if (value == null || value.isBlank()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.ofNullable(ResourceLocation.tryParse(value));
    }

    public static void handleEntityDataSync(S2CEntityDataSyncPayload entityDataSyncPayload, Player player) {
        var targetEntity = player.level().getEntity(entityDataSyncPayload.entityId());

        if (targetEntity == null) {
            return;
        }

        var dataContainer = ((DataUser) targetEntity).getDataContainer();

        entityDataSyncPayload.rawDataSyncMap()
            .rawDataById()
            .forEach(dataContainer::set);
    }

    public static void handleGOAPDebug(S2CGOAPDebugPayload payload, Player player) {
        GOAPDebugState.INSTANCE.update(payload);
    }

    public static void handlePathfindingSearchDebug(S2CPathfindingSearchDebugPayload payload, Player player) {
        PathfindingDebugState.INSTANCE.updateSearch(payload);
    }

    public static void handlePathfindingNavDebug(S2CPathfindingNavDebugPayload payload, Player player) {
        PathfindingDebugState.INSTANCE.update(payload);
    }

    private BLibClientListener() {
        throw new UnsupportedOperationException();
    }
}
