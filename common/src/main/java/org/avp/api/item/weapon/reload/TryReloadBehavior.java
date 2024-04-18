package org.avp.api.item.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.time.Duration;
import java.util.Objects;

import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.common.util.AVPPredicates;
import org.avp.server.ServerScheduler;

@FunctionalInterface
public interface TryReloadBehavior {

    TryReloadBehavior NO_OP = (l, p, i, w) -> {};

    TryReloadBehavior STANDARD = (level, player, itemStack, weaponItemData) -> {
        if (WeaponItemTagHelper.hasMaxAmmunition(itemStack, weaponItemData)) {
            return;
        }

        var ammunitionStrategy = weaponItemData.getAmmunitionStrategy();
        var reloadStrategy = weaponItemData.getReloadStrategy();
        var reloadTimeInTicks = reloadStrategy.getReloadTimeInTicks();

        var activeAmmunitionId = WeaponItemTagHelper.getOrSetActiveAmmunitionType(itemStack, weaponItemData);
        var ammunitionSupplier = ammunitionStrategy.getAmmunitionSupplierByKeyOrFirst(activeAmmunitionId);
        var ammunition = ammunitionSupplier.get();
        var ammunitionInInventory = player.getInventory().countItem(ammunition);
        var ammunitionInWeapon = WeaponItemTagHelper.getAmmunition(itemStack, weaponItemData);
        var ammunitionMissing = weaponItemData.getAmmunitionStrategy().getMaxAmmunition() - ammunitionInWeapon;
        var ammunitionCountToRestore = AVPPredicates.IS_IMMORTAL.test(player)
            ? weaponItemData.getAmmunitionStrategy().getMaxAmmunition()
            : Math.min(ammunitionInInventory, ammunitionMissing);

        if (!AVPPredicates.IS_IMMORTAL.test(player)) {
            if (ammunitionCountToRestore <= 0) {
                // TODO: Play "click" sound or reload fail sound here.
                return;
            }

            consumeAmmunitionFromPlayerInventory(player, ammunitionMissing, ammunition);
        }

        WeaponItemTagHelper.setActiveAmmunition(itemStack, weaponItemData, ammunitionInWeapon + ammunitionCountToRestore);
        player.getCooldowns().addCooldown(itemStack.getItem(), reloadTimeInTicks);

        var reloadStartSound = reloadStrategy.getReloadStartSound().get();
        level.playSound(null, player.blockPosition(), reloadStartSound, SoundSource.PLAYERS);

        reloadStrategy
            .getReloadFinishSound()
            .ifPresent(
                reloadFinishSound -> ServerScheduler.schedule(
                    () -> {
                        var interactionHand = player.getUsedItemHand();
                        var itemInHand = player.getItemInHand(interactionHand);

                        if (Objects.equals(itemStack, itemInHand)) {
                            level.playSound(null, player.blockPosition(), reloadFinishSound.get(), SoundSource.PLAYERS);
                        }
                    },
                    Duration.ofMillis(reloadTimeInTicks * 50L)
                )
            );
    };

    private static void consumeAmmunitionFromPlayerInventory(ServerPlayer player, int ammunitionCountToRestore, Item ammunition) {
        var counter = ammunitionCountToRestore;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (counter <= 0) {
                break;
            }

            var stack = player.getInventory().getItem(i);

            if (Objects.equals(stack.getItem(), ammunition)) {
                var amountToConsume = Math.min(stack.getCount(), counter);
                stack.setCount(stack.getCount() - amountToConsume);
                counter -= amountToConsume;
            }
        }
        player.getInventory().setChanged();
    }

    void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);
}
