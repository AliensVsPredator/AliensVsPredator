package com.avp.common.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.avp.common.block.AVPBlockTags;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.human.marine.Marine;
import com.avp.common.entity.living.yautja.Yautja;

public class AlienPredicates {

    public static boolean canTarget(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        // Target must be valid...
        return canContinueTargeting(alien, potentialTarget)
            // AND target is either an enemy alien...
            && (isAlienTarget(alien, potentialTarget)
                // ...OR is hated (predator, player or entity targeting a fellow hive member).
                || isHated(alien, potentialTarget)
                // ...OR is standing on resin (any mob or monster).
                || isStandingOnResin(potentialTarget));
    }

    public static boolean canContinueTargeting(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        // Target must still be valid...
        return isValidTarget(potentialTarget)
            // AND is not an alien OR if it is an alien, is an enemy alien.
            // We add this check here because the target alien might change strain or hive membership mid-targeting.
            && (!(potentialTarget instanceof Alien targetedAlien) || areAliensEnemies(alien, targetedAlien));
    }

    public static boolean isAlienTarget(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        // If target is tagged as an alien...
        return potentialTarget.getType().is(AVPEntityTypeTags.ALIENS)
            // AND target is a typed alien...
            && potentialTarget instanceof Alien potentialAlienTarget
            // AND aliens are enemies (either opposing hives or opposing strains).
            && areAliensEnemies(alien, potentialAlienTarget);
    }

    public static boolean areAliensEnemies(Alien first, Alien second) {
        // Aliens with different strains will always attack each other.
        // OR if aliens are the same strain, then they still might have different hives, in which case they should
        // attack each other.
        return areAliensDifferentStrains(first, second) || areAliensRivalHiveMembers(first, second);
    }

    private static boolean areAliensRivalHiveMembers(Alien first, Alien second) {
        var firstHiveSignatureOption = first.hiveManager().signature();
        var secondHiveSignatureOption = second.hiveManager().signature();

        if (firstHiveSignatureOption.isNone() || secondHiveSignatureOption.isNone()) {
            // Aliens are neutral towards other aliens that have no hive.
            return false;
        }

        // If hive signatures do not match, then aliens can attack each other.
        return !Objects.equals(firstHiveSignatureOption, secondHiveSignatureOption);
    }

    private static boolean areAliensDifferentStrains(Alien first, Alien second) {
        return !Objects.equals(first.isAberrant(), second.isAberrant())
            || !Objects.equals(first.isIrradiated(), second.isIrradiated())
            || !Objects.equals(first.isNetherAfflicted(), second.isNetherAfflicted());
    }

    public static boolean isValidTarget(@NotNull LivingEntity potentialTarget) {
        // Bats are annoying for aliens to target.
        return !(potentialTarget instanceof Bat)
            // AND creepers are foolish for aliens to target.
            && !(potentialTarget instanceof Creeper)
            // AND target must be alive in order for it to be killed (duh).
            && potentialTarget.isAlive()
            // AND can't attack what can't be attacked (duh).
            && potentialTarget.attackable()
            // AND can't attack immortal players.
            && (!(potentialTarget instanceof Player) || !AVPPredicates.IS_IMMORTAL.test(potentialTarget))
            // AND *shouldn't* attack entities with an embryo inside of them.
            // TODO: There's a bug here, what if it's an embryo from an enemy hive or enemy strain?
            && !AVPPredicates.hasEmbryo(potentialTarget)
            // AND *shouldn't* attack entities with a parasite attached.
            // TODO: There's a bug here, what if it's a parasite from an enemy hive or enemy strain?
            && !AVPPredicates.isParasiteAttached(potentialTarget);
    }

    public static boolean isTargetingHiveMember(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        // Mobs hold targeting behavior...
        return potentialTarget instanceof Mob mob
            // AND target must not be null.
            && mob.getTarget() != null
            // AND mob's target is an alien.
            && mob.getTarget() instanceof Alien targetedAlien
            // AND the mob's targeted alien is the same hive as this alien.
            // TODO: There's a bug here, what if the targeted alien is not a friendly strain to this alien?
            && areAliensSameHive(alien, targetedAlien);
    }

    public static boolean areAliensSameHive(@NotNull Alien alien, @NotNull Alien otherAlien) {
        var hiveSignatureOption = alien.hiveManager().signature();
        var otherHiveSignatureOption = otherAlien.hiveManager().signature();

        // Two aliens with null/missing hives are not considered part of the same hive.
        return hiveSignatureOption.isSome()
            && otherHiveSignatureOption.isSome()
            && Objects.equals(hiveSignatureOption, otherHiveSignatureOption);
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

    private static boolean isHated(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        return (potentialTarget instanceof Player && !AVPPredicates.IS_IMMORTAL.test(potentialTarget))
            || potentialTarget instanceof Yautja
            || potentialTarget instanceof Marine
            || isTargetingHiveMember(alien, potentialTarget);
    }

    /**
     * Find nearby threatening targets for the Alien within a specified range.
     *
     * @param alien The alien entity.
     * @param range The range to search for targets.
     * @return A sorted list of valid targets, closest first.
     */
    public static List<LivingEntity> findTargets(Alien alien, double range) {
        var searchArea = alien.getBoundingBox().inflate(range);

        List<LivingEntity> targetsInRange = alien.level()
            .getEntitiesOfClass(
                LivingEntity.class,
                searchArea,
                potentialTarget -> canTarget(alien, potentialTarget)
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
        var targets = findTargets(alien, range);

        if (targets.isEmpty()) {
            alien.setTarget(null);
            return;
        }

        var closestTarget = targets.get(0);
        alien.setTarget(closestTarget);

        for (var target : targets) {
            if (alien.isWithinMeleeAttackRange(target)) {
                alien.doHurtTarget(target);
            }
        }
    }

}
