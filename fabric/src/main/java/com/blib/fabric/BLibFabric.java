package com.blib.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.world.entity.LivingEntity;

import com.blib.BLib;
import com.blib.common.network.data.DataContainer;
import com.blib.common.network.data.DataUser;

public class BLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BLib.initialize();

        EntityTrackingEvents.START_TRACKING.register(
            (trackedEntity, player) -> {
                if (trackedEntity instanceof LivingEntity livingEntity) {
                    ((DataUser) livingEntity).getDataContainer().syncToClient(livingEntity, DataContainer.SyncType.ALL);
                }
            }
        );
    }
}
