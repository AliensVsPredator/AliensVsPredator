package com.avp.common.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
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

        return potentialTarget instanceof Yautja || isTargetingHiveMember(alien, potentialTarget) || isValidTarget(potentialTarget);
    }

    public static boolean isValidTarget(@NotNull LivingEntity potentialTarget) {
        // Bats and Creepers are annoying for aliens to target.
        return !(potentialTarget instanceof Bat)
            && !(potentialTarget instanceof Creeper)
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

        if (potentialTarget instanceof Creeper && baseBlockState.is(AVPBlockTags.RESIN) || belowBlockState.is(AVPBlockTags.RESIN)) {
            return false;
        }

        // Attack targets that are standing on resin.
        // TODO: Eventually remove this once hive mechanics are added.
        return baseBlockState.is(AVPBlockTags.RESIN) || belowBlockState.is(AVPBlockTags.RESIN);
    }

    /**
     * Find nearby threatening targets for the Alien within a specified range.
     *
     * @param alien The alien entity.
     * @param range The range to search for targets.
     * @return A sorted list of valid targets, closest first.
     */
    public static List<LivingEntity> findThreateningTargets(Alien alien, double range) {
        var searchArea = alien.getBoundingBox().inflate(range);

        List<LivingEntity> targetsInRange = alien.level().getEntitiesOfClass(
                LivingEntity.class,
                searchArea,
                potentialTarget -> isThreateningTarget(alien, potentialTarget)
        );

        targetsInRange.sort(Comparator.comparingDouble(alien::distanceTo));

        return targetsInRange;
    }

    /**
     * Prioritize and attack nearby targets based on given conditions.
     *
     * @param alien The alien entity.
     * @param range The range to detect threats.
     */
    public static void prioritizeAndAttack(Alien alien, double range) {
        var targets = findThreateningTargets(alien, range);

        if (targets.isEmpty()) {
            alien.setTarget(null);
            return;
        }

        var closestTarget = targets.get(0);
        alien.setTarget(closestTarget);

        for (LivingEntity target : targets) {
            if (alien.isWithinMeleeAttackRange(target)) {
                alien.doHurtTarget(target);
            }
        }
    }

}
