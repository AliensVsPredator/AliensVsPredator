package com.blib.mod;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import com.blib.api.BLibAPI;
import com.blib.api.common.block.v1.BlockBreakProgressManager;
import com.blib.api.common.data_sync.v1.DataContainer;
import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.api.common.faction.v1.FactionMember;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.reputation.v1.ReputationKey;
import com.blib.api.common.server.v1.ServerScheduler;
import com.blib.internal.client.render.armor.compat.ShoulderSurfingCompat;
import com.blib.internal.common.faction.BLibFactionManager;
import com.blib.internal.common.reputation.BLibReputationManager;
import com.blib.internal.common.storage.BLibDataStoreManager;
import com.blib.mod.common.gameplay.goap.GOAPDebugTracker;
import com.blib.mod.common.network.BLibPacketDirections;
import com.blib.mod.common.network.BLibServerPacketHandlers;
import com.blib.mod.common.property.BLibModPropertyAccess;
import com.blib.mod.common.registry.init.BLibBlockEntityTypes;
import com.blib.mod.common.registry.init.BLibBlocks;
import com.blib.mod.common.registry.init.BLibCommands;
import com.blib.mod.common.registry.init.BLibDataComponents;
import com.blib.mod.common.registry.init.BLibDataStoreTypes;
import com.blib.mod.common.registry.init.BLibDataSyncKeys;
import com.blib.mod.common.registry.init.BLibFactionDataTypes;
import com.blib.mod.common.registry.init.BLibLootItemConditionTypes;
import com.blib.mod.common.registry.init.BLibReloadListeners;

@ApiStatus.Internal
public class BLib {

    public static final Logger LOGGER = LoggerFactory.getLogger(BLib.class);

    public static final String MOD_ID = "blib";

    public static final BLibMod MOD = BLibAPI.createMod(MOD_ID);

    public static void initialize() {
        ShoulderSurfingCompat.init();

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
        BLibFactionDataTypes.initialize();
        BLibLootItemConditionTypes.initialize();
        BLibPacketDirections.initialize();
        BLibReloadListeners.initialize();
        BLibServerPacketHandlers.initialize();

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
        BLib.MOD.events().onLevelSave().register(BLibDataStoreManager.INSTANCE::saveLevelData);
        BLib.MOD.events().onServerStopped().register(BLibDataStoreManager.INSTANCE::onServerStopped);
        BLib.MOD.events().onServerStopped().register(GOAPDebugTracker.INSTANCE::clear);

        BLib.MOD.events().onServerStarted().register(BLibFactionManager.INSTANCE::load);
        BLib.MOD.events().onServerSave().register(BLibFactionManager.INSTANCE::save);
        BLib.MOD.events().onServerStopped().register(BLibFactionManager.INSTANCE::clear);

        BLib.MOD.events().onServerStarted().register(BLibReputationManager.INSTANCE::load);
        BLib.MOD.events().onServerSave().register(BLibReputationManager.INSTANCE::save);
        BLib.MOD.events().onServerStopped().register(BLibReputationManager.INSTANCE::clear);

        BLib.MOD.events()
            .onFactionRemove()
            .register(factionId -> BLibReputationManager.INSTANCE.removeReputation(ReputationKey.faction(factionId)));

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
                            BLibFactionManager.INSTANCE.getRelationships(factionId).removeMember(member);
                        }

                        BLibReputationManager.INSTANCE.removeReputation(ReputationKey.entity(uuid));
                    }
                    case UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER, CHANGED_DIMENSION -> {}
                }
            });
    }

    private static void syncDataForTrackedEntity(Entity trackedEntity, Player player) {
        if (trackedEntity instanceof LivingEntity livingEntity) {
            ((DataUser) livingEntity).getDataContainer().syncToClient(livingEntity, DataContainer.SyncType.ALL);
        }
    }
}
