package com.blib.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.Function;

import com.blib.api.BLibAPI;
import com.blib.api.common.block.v1.BlockBreakProgressManager;
import com.blib.api.common.data_sync.v1.DataContainer;
import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.api.common.dismemberment.v1.builtin.BuiltInLimbDrops;
import com.blib.api.common.dismemberment.v1.builtin.BuiltInSpawnFunctions;
import com.blib.api.common.faction.v1.Faction;
import com.blib.api.common.faction.v1.FactionMember;
import com.blib.api.common.faction.v1.ProtectionMode;
import com.blib.api.common.faction.v1.RelationshipState;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.reputation.v1.ReputationKey;
import com.blib.api.common.server.v1.ServerScheduler;
import com.blib.internal.client.faction.ClientFactionCache;
import com.blib.internal.client.render.armor.compat.ShoulderSurfingCompat;
import com.blib.internal.client.territory.ClientTerritoryCache;
import com.blib.internal.client.territory.compat.XaeroWorldMapCompat;
import com.blib.internal.common.faction.BLibFactionManager;
import com.blib.internal.common.property.BLibPropertyContainerSaveHandler;
import com.blib.internal.common.reputation.BLibReputationManager;
import com.blib.internal.common.storage.BLibDataStoreManager;
import com.blib.internal.common.storage.ProjectDraftStore;
import com.blib.internal.common.territory.BLibTerritoryManager;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.gameplay.history.ActionHistory;
import com.blib.mod.common.network.BLibPacketDirections;
import com.blib.mod.common.network.BLibServerListener;
import com.blib.mod.common.network.BLibServerPacketHandlers;
import com.blib.mod.common.network.packet.S2CLimbDefinitionsSyncPayload;
import com.blib.mod.common.property.BLibModPropertyAccess;
import com.blib.mod.common.registry.init.BLibBlockEntityTypes;
import com.blib.mod.common.registry.init.BLibBlocks;
import com.blib.mod.common.registry.init.BLibCommands;
import com.blib.mod.common.registry.init.BLibDataComponents;
import com.blib.mod.common.registry.init.BLibDataStoreTypes;
import com.blib.mod.common.registry.init.BLibDataSyncKeys;
import com.blib.mod.common.registry.init.BLibEntityTypes;
import com.blib.mod.common.registry.init.BLibFactionDataTypes;
import com.blib.mod.common.registry.init.BLibJigsawDataStoreTypes;
import com.blib.mod.common.registry.init.BLibLootItemConditionTypes;
import com.blib.mod.common.registry.init.BLibPropertyContainerTypes;
import com.blib.mod.common.registry.init.BLibReloadListeners;
import com.blib.mod.common.registry.init.BLibTerritoryDataStoreTypes;

@ApiStatus.Internal
public class BLib {

    public static final Logger LOGGER = LoggerFactory.getLogger(BLib.class);

    public static final String MOD_ID = "blib";

    public static final BLibMod MOD = BLibAPI.createMod(MOD_ID);

    public static void initialize() {
        ShoulderSurfingCompat.init();
        XaeroWorldMapCompat.init();

        LOGGER.info("Initializing BLib for platform '{}'", BLibAPI.getModLoaderType());

        BLibModPropertyAccess.INSTANCE.save();

        BLib.MOD.initialize(BLib::runInitialization);
    }

    private static void runInitialization() {
        BLibBlockEntityTypes.initialize();
        BLibBlocks.initialize();
        BLibCommands.initialize();
        BLibDataComponents.initialize();
        BLibDataSyncKeys.initialize();
        BLibDataStoreTypes.initialize();
        BLibEntityTypes.initialize();
        BLibFactionDataTypes.initialize();
        BLibJigsawDataStoreTypes.initialize();
        BLibLootItemConditionTypes.initialize();
        BLibTerritoryDataStoreTypes.initialize();
        BLibPacketDirections.initialize();
        BLibPropertyContainerTypes.initialize();
        BLibReloadListeners.initialize();
        BLibServerPacketHandlers.initialize();

        // Depends on entity types being registered. Built-in limb definitions are now data-driven (see
        // resources/data/minecraft/blib_limbs and resources/assets/minecraft/blib_limb_visuals); this call only
        // registers the function-typed spawn offsets that can't be expressed in JSON, keyed by limb id.
        BuiltInSpawnFunctions.register();
        BuiltInLimbDrops.register();

        BLib.MOD.events().onPlayerStartTrackingEntity().register(BLib::syncDataForTrackedEntity);
        // TODO: There's a small bug here. This runs for both client and server levels!
        BLib.MOD.events().postLevelTick().register(ServerScheduler::tick);
        // TODO: There's a small bug here. This runs for both client and server levels!
        BLib.MOD.events().postLevelTick().register(BlockBreakProgressManager::tick);

        BLib.MOD.events().postLevelTick().register(level -> {
            if (!level.isClientSide && level.dimension() == Level.OVERWORLD) {
                GOAPDebugTracker.INSTANCE.tick(level.getServer());
            }
        });

        BLib.MOD.events().onChunkSave().register(BLibDataStoreManager.INSTANCE::saveChunkData);
        BLib.MOD.events().onChunkUnload().register(BLibDataStoreManager.INSTANCE::onChunkUnload);
        BLib.MOD.events().onServerSave().register(BLibDataStoreManager.INSTANCE::saveGlobalData);
        BLib.MOD.events().onServerSave().register(BLibPropertyContainerSaveHandler.INSTANCE::save);
        BLib.MOD.events().onLevelSave().register(BLibDataStoreManager.INSTANCE::saveLevelData);
        BLib.MOD.events().onServerStopped().register(BLibDataStoreManager.INSTANCE::onServerStopped);
        BLib.MOD.events().onServerStopped().register(GOAPDebugTracker.INSTANCE::clear);
        // Unified ActionHistory: register the broadcast listener at server-start (so notify hooks have a
        // MinecraftServer
        // reference to send packets through), and clear stacks + drop the listener on shutdown.
        BLib.MOD.events()
            .onServerStarted()
            .register(server -> ActionHistory.setChangeListener(() -> BLibServerListener.broadcastActionHistorySync(server)));
        BLib.MOD.events()
            .onServerStopped()
            .register(server -> {
                ActionHistory.setChangeListener(null);
                ActionHistory.clear();
            });
        BLib.MOD.events().onServerStopped().register(server -> ClientTerritoryCache.INSTANCE.clear());
        BLib.MOD.events().onServerStopped().register(server -> ClientFactionCache.INSTANCE.clear());
        BLib.MOD.events().onServerStopped().register(ProjectDraftStore.INSTANCE::onServerStopped);

        BLib.MOD.events().onServerStarted().register(BLibFactionManager.INSTANCE::load);
        BLib.MOD.events().onServerSave().register(BLibFactionManager.INSTANCE::save);
        BLib.MOD.events().onServerStopped().register(BLibFactionManager.INSTANCE::clear);

        BLib.MOD.events().onServerStarted().register(BLibReputationManager.INSTANCE::load);
        BLib.MOD.events().onServerSave().register(BLibReputationManager.INSTANCE::save);
        BLib.MOD.events().onServerStopped().register(BLibReputationManager.INSTANCE::clear);

        BLib.MOD.events().onServerStarted().register(BLibTerritoryManager.INSTANCE::onServerStarted);
        BLib.MOD.events().onServerStopped().register(BLibTerritoryManager.INSTANCE::onServerStopped);
        BLib.MOD.events().onChunkLoad().register(BLibTerritoryManager.INSTANCE::onChunkLoaded);
        BLib.MOD.events().onChunkUnload().register(BLibTerritoryManager.INSTANCE::onChunkUnloaded);

        BLib.MOD.events().onChunkLoad().register((level, chunk) -> {
            var pos = chunk.getPos();
            var claimants = BLibTerritoryManager.INSTANCE.getClaimants(level, pos);

            if (!claimants.isEmpty()) {
                for (var player : level.getServer().getPlayerList().getPlayers()) {
                    if (player.connection != null) {
                        var payload = BLibTerritoryManager.INSTANCE.buildSyncPayloadForPlayer(level, pos, player);

                        if (!payload.factionIds().isEmpty()) {
                            BLib.MOD.networking().sendToClient(player, payload);
                        }
                    }
                }
            }
        });

        BLib.MOD.events()
            .onChunkClaimAdded()
            .register(BLibTerritoryManager.INSTANCE::onClaimAdded);

        BLib.MOD.events()
            .onChunkClaimRemoved()
            .register(BLibTerritoryManager.INSTANCE::onClaimRemoved);

        BLib.MOD.events()
            .onChunkClaimAdded()
            .register((level, pos, claimant) -> {
                for (var player : level.getServer().getPlayerList().getPlayers()) {
                    var payload = BLibTerritoryManager.INSTANCE.buildSyncPayloadForPlayer(level, pos, player);
                    BLib.MOD.networking().sendToClient(player, payload);
                }
            });

        BLib.MOD.events()
            .onChunkClaimRemoved()
            .register((level, pos, claimant) -> {
                for (var player : level.getServer().getPlayerList().getPlayers()) {
                    var payload = BLibTerritoryManager.INSTANCE.buildSyncPayloadForPlayer(level, pos, player);
                    BLib.MOD.networking().sendToClient(player, payload);
                }
            });

        BLib.MOD.events()
            .preBlockBreak()
            .register(BLib::handleBlockBreakProtection);

        BLib.MOD.events()
            .onFactionRemove()
            .register(factionId -> BLibReputationManager.INSTANCE.removeReputation(ReputationKey.faction(factionId)));

        BLib.MOD.events()
            .onFactionRemove()
            .register(BLibTerritoryManager.INSTANCE::onFactionRemoved);

        BLib.MOD.events()
            .onEntityLoad()
            .register(entity -> {
                if (entity instanceof ServerPlayer player) {
                    player.server.tell(
                        new TickTask(
                            player.server.getTickCount() + 20,
                            () -> {
                                if (player.connection != null) {
                                    BLibFactionManager.INSTANCE.syncAllFactionMetadataToPlayer(player);
                                    BLibTerritoryManager.INSTANCE.syncAllClaimsToPlayer(player);
                                    BLib.MOD.networking()
                                        .sendToClient(
                                            player,
                                            S2CLimbDefinitionsSyncPayload
                                                .snapshotFromRegistry()
                                        );
                                }
                            }
                        )
                    );
                }
            });

        BLib.MOD.events()
            .onEntityLoad()
            .register(entity -> {
                var uuid = entity.getUUID();
                var factionIds = BLibFactionManager.INSTANCE.getFactionIds(uuid);

                for (var factionId : factionIds) {
                    var faction = BLibFactionManager.INSTANCE.get(factionId);

                    if (faction != null && faction.data() != null) {
                        faction.data().onMemberLoaded(entity);
                    }
                }
            });

        BLib.MOD.events()
            .onEntityRemove()
            .register((entity, reason) -> {
                switch (reason) {
                    case KILLED, DISCARDED -> {
                        if (entity instanceof Player) {
                            return;
                        }

                        var uuid = entity.getUUID();
                        var factionIds = Set.copyOf(BLibFactionManager.INSTANCE.getFactionIds(uuid));
                        var member = new FactionMember.Entity(uuid);

                        for (var factionId : factionIds) {
                            var faction = BLibFactionManager.INSTANCE.get(factionId);

                            if (faction != null) {
                                faction.membership().removeMember(member);
                            }
                        }

                        BLibReputationManager.INSTANCE.removeReputation(ReputationKey.entity(uuid));

                    }
                    case UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER, CHANGED_DIMENSION -> {
                        var uuid = entity.getUUID();
                        var factionIds = BLibFactionManager.INSTANCE.getFactionIds(uuid);

                        for (var factionId : factionIds) {
                            var faction = BLibFactionManager.INSTANCE.get(factionId);

                            if (faction != null && faction.data() != null) {
                                faction.data().onMemberUnloaded(entity);
                            }
                        }
                    }
                }
            });
    }

    private static boolean handleBlockBreakProtection(
        Level level,
        Player player,
        BlockPos blockPos,
        BlockState blockState
    ) {
        return checkProtection(level, player, blockPos, Faction::blockBreakProtection);
    }

    public static boolean checkBlockInteractProtection(
        Level level,
        Player player,
        BlockPos blockPos
    ) {
        return checkProtection(level, player, blockPos, Faction::blockInteractProtection);
    }

    public static boolean checkEntityInteractProtection(
        Level level,
        Player player,
        BlockPos entityPos
    ) {
        return checkProtection(level, player, entityPos, Faction::entityInteractProtection);
    }

    public static boolean checkNonLivingEntityAttackProtection(
        Level level,
        Player player,
        BlockPos entityPos
    ) {
        return checkProtection(level, player, entityPos, Faction::nonLivingEntityAttackProtection);
    }

    public static boolean checkPvpProtection(
        Level level,
        BlockPos pos
    ) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return true;
        }

        var chunkPos = new ChunkPos(pos);
        var claimants = BLibTerritoryManager.INSTANCE.getClaimants(serverLevel, chunkPos);

        for (var factionId : claimants) {
            var faction = BLibFactionManager.INSTANCE.get(factionId);

            if (faction != null && !faction.allowPvp()) {
                return false;
            }
        }

        return true;
    }

    private static boolean checkProtection(
        Level level,
        Player player,
        BlockPos blockPos,
        Function<Faction<?>, ProtectionMode> protectionGetter
    ) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return true;
        }

        var chunkPos = new ChunkPos(blockPos);
        var claimants = BLibTerritoryManager.INSTANCE.getClaimants(serverLevel, chunkPos);

        if (claimants.isEmpty()) {
            return true;
        }

        var playerUuid = player.getUUID();
        var playerFactionIds = BLibFactionManager.INSTANCE.getFactionIds(playerUuid);

        for (var claimantFactionId : claimants) {
            var faction = BLibFactionManager.INSTANCE.get(claimantFactionId);

            if (faction == null) {
                continue;
            }

            var protection = protectionGetter.apply(faction);

            if (protection == ProtectionMode.PUBLIC) {
                continue;
            }

            if (playerFactionIds.contains(claimantFactionId)) {
                continue;
            }

            if (protection == ProtectionMode.ALLIED) {
                var isAllied = false;

                for (var playerFactionId : playerFactionIds) {
                    if (
                        BLibFactionManager.INSTANCE.getRelationship(
                            playerFactionId,
                            claimantFactionId
                        ) == RelationshipState.ALLIED
                    ) {
                        isAllied = true;
                        break;
                    }
                }

                if (isAllied) {
                    continue;
                }
            }

            return false;
        }

        return true;
    }

    private static void syncDataForTrackedEntity(Entity trackedEntity, Player player) {
        if (trackedEntity instanceof LivingEntity livingEntity) {
            ((DataUser) livingEntity).getDataContainer().syncToClient(livingEntity, DataContainer.SyncType.ALL);
        }
    }
}
