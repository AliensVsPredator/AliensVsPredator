package com.avp.fabric;

import com.lib.common.network.DataContainer;
import com.lib.common.network.DataUser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.world.entity.LivingEntity;

import com.avp.AVP;

public class AVPFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        AVP.initialize();

        EntityTrackingEvents.START_TRACKING.register(
            (trackedEntity, player) -> {
                if (trackedEntity instanceof LivingEntity livingEntity) {
                    ((DataUser) livingEntity).getDataContainer().syncToClient(livingEntity, DataContainer.SyncType.ALL);
                }
            }
        );
    }
}
