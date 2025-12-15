package com.blib.neoforge.internal;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;

import com.blib.BLib;
import com.blib.common.network.data.DataContainer;
import com.blib.common.network.data.DataUser;

@ApiStatus.Internal
@Mod(BLib.MOD_ID)
public class BLibNeoForge {

    public BLibNeoForge() {
        BLib.initialize();

        // Game bus events.
        NeoForge.EVENT_BUS.addListener(BLibNeoForge::registerPlayerTrackingEntityHandler);
    }

    public static void registerPlayerTrackingEntityHandler(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof LivingEntity livingEntity) {
            ((DataUser) livingEntity).getDataContainer().syncToClient(livingEntity, DataContainer.SyncType.ALL);
        }
    }
}
