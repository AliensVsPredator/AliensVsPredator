package com.avp.common.item.gun.attack.hitscan;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import com.avp.common.item.GunItem;
import com.avp.common.item.gun.attack.GunAttackConfig;
import com.avp.common.item.gun.attack.GunHitResult;
import com.avp.common.network.packet.C2SGunHitResultsPayload;

public class GunHitScanAttackHandler {

    public static void handle(C2SGunHitResultsPayload payload, Player player) {
        if (player == null || player.level().isClientSide) {
            // Player is null or level is client-side, nothing we can do beyond this point.
            return;
        }

        var level = (ServerLevel) player.level();
        var usedItemHand = player.getUsedItemHand();
        var itemStack = player.getItemInHand(usedItemHand);
        var item = itemStack.getItem();

        if (!(item instanceof GunItem gunItem)) {
            return;
        }

        var gunConfig = gunItem.getGunConfig();

        var gunAttackConfig = new GunAttackConfig(gunConfig, gunConfig.getDefaultFireMode(), player, itemStack);

        payload.gunHitResults().forEach(gunHitResult -> {
            switch (gunHitResult) {
                case GunHitResult.Block result -> BlockGunHitResultHandler.handle(gunAttackConfig, result);
                case GunHitResult.Entity result -> {
                    var entityUUID = result.entityUUID();
                    var entity = level.getEntity(entityUUID);

                    if (entity != null) {
                        EntityGunHitResultHandler.handle(gunAttackConfig, entity);
                    }
                }
            }
        });
    }
}
