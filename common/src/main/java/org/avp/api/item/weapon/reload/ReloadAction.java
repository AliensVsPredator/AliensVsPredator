package org.avp.api.item.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import java.time.Duration;
import java.util.Objects;

import org.avp.api.item.weapon.WeaponItemData;
import org.avp.server.ServerScheduler;

public class ReloadAction {

    public static void perform(ServerLevel level, ServerPlayer player, ItemStack itemStack, WeaponItemData weaponItemData) {
        var reloadStrategy = weaponItemData.getReloadStrategy();
        var reloadTimeInTicks = reloadStrategy.getReloadTimeInTicks();

        if (reloadTimeInTicks <= 0) {
            return;
        }

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
    }

    private ReloadAction() {
        throw new UnsupportedOperationException();
    }
}
