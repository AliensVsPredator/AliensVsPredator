package com.avp.fabric.common.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import com.avp.fabric.common.entity.living.yautja.Yautja;
import com.avp.common.item.AVPItemTags;

public class YautjaPredicates {

    private YautjaPredicates() {}

    public static boolean isThreateningTarget(@NotNull Yautja yautja, @NotNull LivingEntity potentialTarget) {
        return isValidTarget(yautja, potentialTarget);
    }

    public static boolean isValidTarget(@NotNull Yautja yautja, @NotNull LivingEntity potentialTarget) {
        if (potentialTarget instanceof Yautja) {
            return false;
        }

        if (potentialTarget instanceof Creeper) {
            return false;
        }

        if (potentialTarget instanceof Player player) {
            return !AVPPredicates.IS_IMMORTAL.test(player)
                && (player.getMainHandItem().is(AVPItemTags.HOSTILE_WEAPON)
                    || (yautja.getLastAttacker() != null && yautja.getLastAttacker().is(player)));
        }

        if (potentialTarget instanceof Mob || potentialTarget instanceof Monster) {
            return potentialTarget.getMainHandItem().is(AVPItemTags.HOSTILE_WEAPON)
                || (yautja.getLastAttacker() != null && yautja.getLastAttacker().is(potentialTarget));
        }

        return yautja.getLastAttacker() != null && yautja.getLastAttacker().is(potentialTarget);
    }
}
