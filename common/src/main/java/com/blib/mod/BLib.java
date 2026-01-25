package com.blib.mod;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blib.api.BLibAPI;
import com.blib.api.common.block.v1.BlockBreakProgressManager;
import com.blib.api.common.data_sync.v1.DataContainer;
import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.api.common.mod.v1.BLibMod;
import com.blib.api.common.server.v1.ServerScheduler;
import com.blib.azurelib.AzureLib;
import com.blib.mod.common.network.BLibPacketDirections;
import com.blib.mod.common.network.BLibServerPacketHandlers;
import com.blib.mod.common.registry.init.BLibDataComponents;
import com.blib.mod.common.registry.init.BLibDataSyncKeys;
import com.blib.mod.common.registry.init.BLibLootItemConditionTypes;
import com.blib.mod.common.registry.init.BLibReloadListeners;

@ApiStatus.Internal
public class BLib {

    public static final Logger LOGGER = LoggerFactory.getLogger(BLib.class);

    public static final String MOD_ID = "blib";

    public static final BLibMod MOD = BLibAPI.createMod(MOD_ID);

    public static void initialize() {
        AzureLib.initialize();

        LOGGER.info("Initializing BLib for platform '{}'", BLibAPI.getModLoaderType());

        BLib.MOD.initialize(() -> {
            BLibReloadListeners.initialize();
            BLibDataComponents.initialize();
            BLibDataSyncKeys.initialize();
            BLibLootItemConditionTypes.initialize();
            BLibPacketDirections.initialize();
            BLibServerPacketHandlers.initialize();

            BLib.MOD.events().onPlayerStartTrackingEntity().register(BLib::syncDataForTrackedEntity);
            // TODO: There's a small bug here. This runs for both client and server levels!
            BLib.MOD.events().postLevelTick().register(ServerScheduler::tick);
            // TODO: There's a small bug here. This runs for both client and server levels!
            BLib.MOD.events().postLevelTick().register(BlockBreakProgressManager::tick);
        });
    }

    private static void syncDataForTrackedEntity(Entity trackedEntity, Player player) {
        if (trackedEntity instanceof LivingEntity livingEntity) {
            ((DataUser) livingEntity).getDataContainer().syncToClient(livingEntity, DataContainer.SyncType.ALL);
        }
    }
}
