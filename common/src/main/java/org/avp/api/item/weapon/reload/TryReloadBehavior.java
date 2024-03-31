package org.avp.api.item.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.server.ServerScheduler;

import java.time.Duration;
import java.util.Objects;

@FunctionalInterface
public interface TryReloadBehavior {
    TryReloadBehavior NO_OP = (l, p, i, w) -> {};

    TryReloadBehavior STANDARD = (level, player, itemStack, weaponItemData) -> {
        if (WeaponItemTagHelper.hasMaxAmmunition(itemStack, weaponItemData)) {
            return;
        }

        var reloadStrategy = weaponItemData.getReloadStrategy();
        var reloadTimeInTicks = reloadStrategy.getReloadTimeInTicks();
        player.getCooldowns().addCooldown(itemStack.getItem(), reloadTimeInTicks);

        // TODO: Try and consume ammunition item.
        WeaponItemTagHelper.restoreAmmunition(itemStack, weaponItemData);

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

    void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);
}
