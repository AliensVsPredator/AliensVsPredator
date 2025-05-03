package com.avp.common.item.gun.pipeline.step.impl;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import com.avp.common.item.AVPItems;
import com.avp.common.item.GunReloading;
import com.avp.common.item.gun.pipeline.GunShootContext;
import com.avp.common.item.gun.pipeline.GunShootResult;
import com.avp.common.item.gun.pipeline.step.GunShootStep;

public class CheckReloadingStep implements GunShootStep {

    public static final CheckReloadingStep INSTANCE = new CheckReloadingStep();

    private CheckReloadingStep() {}

    @Override
    public GunShootResult apply(GunShootContext context) {
        var currentAmmunition = context.currentAmmunition();
        var gunConfig = context.gunConfig();
        var gunItem = context.gunItem();
        var hasInfinity = context.hasInfinity();
        var isShooterImmortal = context.isShooterImmortal();
        var shooter = context.shooter();
        var supplier = gunConfig.ammunitionItemSupplier();
        // TODO: Don't hardcode old painless check here.
        var hasAmmunitionStream = gunItem == AVPItems.OLD_PAINLESS.get()
            && supplier != null
            && shooter instanceof ServerPlayer serverPlayer
            // TODO: Figure out how to remove this side-effect.
            && GunReloading.consumeItemAmountFromInventory(
                serverPlayer,
                supplier.get(),
                1
            ) == GunReloading.ItemConsumptionResult.Full.INSTANCE;
        var hasAmmunition = hasAmmunitionStream || currentAmmunition > 0;

        if (shooter instanceof Player && !isShooterImmortal && !hasInfinity && currentAmmunition <= 0 && !hasAmmunition) {
            GunReloading.reload((ServerPlayer) shooter);
            return GunShootResult.RELOADING;
        }

        return GunShootResult.CONTINUE;
    }
}
