package com.human.common.gameplay.item.gun.pipeline.step.impl;

import com.human.common.gameplay.item.gun.GunReloading;
import com.human.common.gameplay.item.gun.pipeline.GunShootContext;
import com.human.common.gameplay.item.gun.pipeline.GunShootResult;
import com.human.common.gameplay.item.gun.pipeline.step.GunShootStep;
import com.human.common.registry.init.item.HumanGunItems;
import net.minecraft.world.entity.player.Player;

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
        var level = shooter.level();
        var supplier = gunConfig.ammunitionItemSupplier();

        if (
            // If shooter is not a player...
            !(shooter instanceof Player player)
                // OR player is immortal...
                || isShooterImmortal
                // OR player has infinity on the weapon...
                || hasInfinity
        ) {
            // Then don't bother checking for reload requirements, just continue.
            return GunShootResult.CONTINUE;
        }

        // TODO: Don't hardcode old painless check here.
        if (
            gunItem == HumanGunItems.OLD_PAINLESS.get()
                && supplier != null
        ) {
            var ammunitionItem = supplier.get();
            var amountToConsume = gunConfig.getDefaultFireMode().consumedAmmunitionPerShot();
            // Run a simulation to see if we can succeed in consuming a single bullet.
            var itemConsumptionResult = GunReloading.consumeItemAmountFromInventory(player, ammunitionItem, amountToConsume, false);

            // This check should be able to pass on both the client side and the server side.
            if (itemConsumptionResult == GunReloading.ItemConsumptionResult.Full.INSTANCE) {
                // TODO: Figure out how to remove this side-effect.
                // Run an actual consume process instead of a simulation.
                if (!level.isClientSide) {
                    // Only consume the ammunition server-side.
                    GunReloading.consumeItemAmountFromInventory(player, ammunitionItem, amountToConsume, true);
                }
            } else {
                // Otherwise if we could not consume any ammunition, return a failure.
                return GunShootResult.FAILURE;
            }
        } else if (currentAmmunition <= 0) {
            // NO-OP client-side, this step should run on both sides if set up correctly.

            if (!level.isClientSide) {
                // Already server-side, so perform the reload immediately.
                GunReloading.reload(player);
            }

            return GunShootResult.RELOADING;
        }

        return GunShootResult.CONTINUE;
    }
}
