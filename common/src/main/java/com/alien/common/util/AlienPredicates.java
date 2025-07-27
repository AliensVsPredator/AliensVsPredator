package com.alien.common.util;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.model.alien.Host;
import com.alien.common.model.alien.variant.AlienVariant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import com.avp.common.registry.tag.AVPBlockTags;
import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.common.util.AVPPredicates;

public class AlienPredicates {

    public static boolean canTarget(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        // Target must be valid...
        return canContinueTargeting(alien, potentialTarget)
            // AND the target is either an enemy alien...
            && (isAlienTarget(alien, potentialTarget)
                // ...OR is hated (predator, player or entity targeting a fellow hive member).
                || isHated(alien, potentialTarget)
                // ...OR is standing on resin (any mob or monster).
                || isStandingOnResin(potentialTarget)
                // ...OR the target has an enemy variant embryo.
                || doesTargetHaveEnemyVariantEmbryo(alien.getVariant(), potentialTarget)
        );
    }

    public static boolean canContinueTargeting(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        // Target must still be valid...
        return isValidTarget(alien.getVariant(), potentialTarget)
            // AND is not an alien OR if it is an alien, is an enemy alien.
            // We add this check here because the target alien might change strain or hive membership mid-targeting.
            && (!(potentialTarget instanceof Alien targetedAlien) || areAliensEnemies(alien, targetedAlien));
    }

    public static boolean isAlienTarget(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        // If a target is tagged as an alien...
        return potentialTarget.getType().is(AVPEntityTypeTags.ALIENS)
            // AND target is a typed alien...
            && potentialTarget instanceof Alien potentialAlienTarget
            // AND aliens are enemies (either opposing hives or opposing strains).
            && areAliensEnemies(alien, potentialAlienTarget);
    }

    // This function is here for semantics reasons.
    public static boolean areAliensEnemies(Alien first, Alien second) {
        // Aliens with different strains will always attack each other.
        return areAliensDifferentStrains(first, second);
    }

    private static boolean areAliensDifferentStrains(Alien first, Alien second) {
        return !Objects.equals(first.isAberrant(), second.isAberrant())
            || !Objects.equals(first.isIrradiated(), second.isIrradiated())
            || !Objects.equals(first.isNetherAfflicted(), second.isNetherAfflicted());
    }

    public static boolean isValidTarget(AlienVariant selfVariant, @NotNull LivingEntity potentialTarget) {
        // Bats are annoying for aliens to target.
        return !(potentialTarget instanceof Bat)
            // AND creepers are foolish for aliens to target.
            && !(potentialTarget instanceof Creeper)
            // AND the target must be alive in order for it to be killed (duh).
            && potentialTarget.isAlive()
            // AND can't attack what can't be attacked (duh).
            && potentialTarget.attackable()
            // AND can't attack immortal players.
            && (!(potentialTarget instanceof Player) || !AVPPredicates.IS_IMMORTAL.test(potentialTarget))
            // AND *shouldn't* attack entities with an embryo inside them.
            && (!AVPPredicates.hasEmbryo(potentialTarget) || doesTargetHaveEnemyVariantEmbryo(selfVariant, potentialTarget))
            // AND *shouldn't* attack entities with a parasite attached.
            && !AVPPredicates.isParasiteAttached(potentialTarget);
    }

    private static boolean doesTargetHaveEnemyVariantEmbryo(AlienVariant selfVariant, @NotNull LivingEntity potentialTarget) {
        return potentialTarget instanceof Host host && host.getEmbryoType()
            .isSomeAnd(
                embryoType -> AlienVariantTypes.getFor(embryoType)
                    .isSomeAnd(alienVariantType -> !Objects.equals(selfVariant, alienVariantType.variant()))
            );
    }

    public static boolean isTargetingHiveMember(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        // Mobs hold targeting behavior...
        return potentialTarget instanceof Mob mob
            // AND the target must not be null.
            && mob.getTarget() != null
            // AND the mob's target is an alien.
            && mob.getTarget() instanceof Alien targetedAlien
            // AND the mob's targeted alien is the same hive as this alien.
            && areAliensSameHive(alien, targetedAlien);
    }

    public static boolean areAliensSameHive(@NotNull Alien alien, @NotNull Alien otherAlien) {
        var hiveSignatureOption = alien.getHiveManager().signature();
        var otherHiveSignatureOption = otherAlien.getHiveManager().signature();

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

        return baseBlockState.is(AVPBlockTags.RESIN) || belowBlockState.is(AVPBlockTags.RESIN);
    }

    private static boolean isHated(@NotNull Alien alien, @NotNull LivingEntity potentialTarget) {
        if (AVPPredicates.IS_IMMORTAL.test(potentialTarget)) {
            // If the target is immortal, then alien can't "hate" them.
            return false;
        }

        return potentialTarget.getType().is(AVPEntityTypeTags.HATED_BY_XENOMORPHS)
            || isTargetingHiveMember(alien, potentialTarget);
    }
}
