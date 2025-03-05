package com.avp.common.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.yautja.Yautja;

public class AlienPredicates {

    public static boolean isThreateningTarget(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        if (!isValidTarget(potentialTarget)) {
            return false;
        }

        if (potentialTarget.getType().is(AVPEntityTypeTags.ALIENS) && potentialTarget instanceof Alien potentialAlienTarget) {
            return AVPPredicates.areAliensEnemies(alien, potentialAlienTarget);
        }

        var resinStanceCheck = potentialTarget instanceof Player || isStandingOnResin(potentialTarget);

        return potentialTarget instanceof Yautja || resinStanceCheck || isTargetingHiveMember(alien, potentialTarget);
    }

    public static boolean isValidTarget(@NotNull LivingEntity potentialTarget) {
        // Bats are annoying for aliens to target.
        return !(potentialTarget instanceof Bat)
            && potentialTarget.isAlive()
            && potentialTarget.attackable()
            && (!(potentialTarget instanceof Player) || !AVPPredicates.IS_IMMORTAL.test(potentialTarget))
            && !AVPPredicates.hasEmbryo(potentialTarget)
            && !AVPPredicates.isParasiteAttached(potentialTarget);
    }

    public static boolean isTargetingHiveMember(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        return potentialTarget instanceof Mob mob
            && mob.getTarget() != null
            && mob.getTarget() instanceof Alien targetedAlien
            && areAliensSameHive(alien, targetedAlien);
    }

    public static boolean areAliensSameHive(@NotNull Alien alien, @NotNull Alien otherAlien) {
        var hiveSignature = alien.hiveManager().signatureOrNull();
        var otherHiveSignature = otherAlien.hiveManager().signatureOrNull();

        return hiveSignature != null
            && otherHiveSignature != null
            && Objects.equals(hiveSignature, otherHiveSignature);
    }

    public static boolean isStandingOnResin(@NotNull LivingEntity potentialTarget) {
        var basePos = potentialTarget.blockPosition();
        var belowPos = basePos.below();
        var baseBlockState = potentialTarget.level().getBlockState(basePos);
        var belowBlockState = potentialTarget.level().getBlockState(belowPos);

        // Attack targets that are standing on resin.
        // TODO: Eventually remove this once hive mechanics are added.
        return baseBlockState.is(AVPBlockTags.RESIN) || belowBlockState.is(AVPBlockTags.RESIN);
    }
}
