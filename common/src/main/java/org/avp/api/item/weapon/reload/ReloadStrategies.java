package org.avp.api.item.weapon.reload;

import net.minecraft.sounds.SoundSource;
import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.server.ServerScheduler;

import java.time.Duration;
import java.util.Objects;

/**
 * @author Boston Vanseghi
 */
public class ReloadStrategies {

    public static final ReloadStrategy NO_OP = (l, p, i, w) -> {};

    public static final ReloadStrategy STANDARD = (level, player, itemStack, weaponItemData) -> {
        var reloadTimeInTicks = weaponItemData.getReloadTimeInTicks();
        player.getCooldowns().addCooldown(itemStack.getItem(), reloadTimeInTicks);

        // TODO: Try and consume ammunition item.
        WeaponItemTagHelper.restoreAmmunition(itemStack, weaponItemData);

        var reloadStartSound = weaponItemData.getReloadStartSound().get();
        level.playSound(null, player.blockPosition(), reloadStartSound, SoundSource.PLAYERS);

        weaponItemData
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

    private ReloadStrategies() {
        throw new UnsupportedOperationException();
    }
}
