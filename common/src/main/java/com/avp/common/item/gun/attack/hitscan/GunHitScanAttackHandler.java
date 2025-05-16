package com.avp.common.item.gun.attack.hitscan;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import com.avp.common.item.GunItem;
import com.avp.common.item.gun.attack.GunAttackConfig;
import com.avp.common.item.gun.attack.GunHitResult;
import com.avp.common.network.packet.C2SGunHitResultsPayload;

public class GunHitScanAttackHandler {

    public static void handle(C2SGunHitResultsPayload payload, LivingEntity shooter) {
        if (shooter == null || shooter.level().isClientSide) {
            // Shooter is null or level is client-side, nothing we can do beyond this point.
            return;
        }

        var level = (ServerLevel) shooter.level();
        var usedItemHand = shooter.getUsedItemHand();
        var itemStack = shooter.getItemInHand(usedItemHand);
        var item = itemStack.getItem();

        if (!(item instanceof GunItem gunItem)) {
            return;
        }

        var gunConfig = gunItem.getGunConfig();

        var gunAttackConfig = new GunAttackConfig(gunConfig, gunConfig.getDefaultFireMode(), shooter, itemStack);

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
