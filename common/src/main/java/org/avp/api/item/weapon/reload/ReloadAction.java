package org.avp.api.item.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

import java.time.Duration;
import java.util.Objects;

import org.avp.api.item.weapon.WeaponItemStack;
import org.avp.server.ServerScheduler;

public class ReloadAction {

    public static void perform(ServerLevel level, ServerPlayer player, WeaponItemStack weaponItemStack) {
        var fireModeData = weaponItemStack.getOrSetFireMode();
        var itemStack = weaponItemStack.getItemStack();
        var reloadData = fireModeData.reloadData();
        var reloadTimeInTicks = reloadData.reloadTimeInTicks();

        if (reloadTimeInTicks <= 0) {
            return;
        }

        player.getCooldowns().addCooldown(itemStack.getItem(), reloadTimeInTicks);
        var reloadStartSound = reloadData.reloadStartSoundHolder().get();
        level.playSound(null, player.blockPosition(), reloadStartSound, SoundSource.PLAYERS);

        reloadData
            .reloadFinishSoundHolder()
            .ifPresent(
                reloadFinishSound -> ServerScheduler.schedule(
                    () -> {
                        var interactionHand = player.getUsedItemHand();
                        var itemInHand = player.getItemInHand(interactionHand);

                        if (Objects.equals(itemStack, itemInHand)) {
                            level.playSound(null, player.blockPosition(), reloadFinishSound, SoundSource.PLAYERS);
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
