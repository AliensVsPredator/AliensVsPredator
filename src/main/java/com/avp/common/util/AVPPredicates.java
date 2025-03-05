package com.avp.common.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import java.util.Objects;
import java.util.function.Predicate;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.Host;
import com.avp.common.entity.living.alien.Alien;

public class AVPPredicates {

    public static <T> Predicate<T> alwaysTrue() {
        return $ -> true;
    }

    public static boolean areAliensEnemies(Alien first, Alien second) {
        var isSecondAberrant = second.isAberrant();
        var isSecondNetherAfflicted = second.isNetherAfflicted();
        var secondHiveSignature = second.hiveManager().signatureOrNull();

        if (
            !Objects.equals(first.isNetherAfflicted(), isSecondNetherAfflicted) ||
                !Objects.equals(first.isAberrant(), isSecondAberrant)
        ) {
            return true;
        }

        if (first.hiveManager().signatureOrNull() == null || secondHiveSignature == null) {
            return false;
        }

        // Only attack other aliens under these conditions.
        return !Objects.equals(first.hiveManager().signatureOrNull(), secondHiveSignature);
    }

    public static boolean hasEmbryo(Entity target) {
        return target instanceof Host host && host.parasiteType() != null;
    }

    public static boolean hasShield(Entity target) {
        return target instanceof LivingEntity livingEntity && livingEntity.getUseItem().is(Items.SHIELD);
    }

    public static boolean isBaby(Entity target) {
        return target instanceof LivingEntity livingEntity && livingEntity.isBaby();
    }

    public static boolean isFreeHost(Alien parasite, Entity hostTarget) {
        return isLiving(hostTarget) &&
            isHost(hostTarget) &&
            !hasEmbryo(hostTarget) &&
            !isSelfOrOtherParasiteAttached(parasite, hostTarget);
    }

    public static boolean isHost(Entity target) {
        return target.getType().is(AVPEntityTypeTags.HOSTS) &&
            isLiving(target) &&
            !isBaby(target) &&
            !IS_IMMORTAL.test((LivingEntity) target);
    }

    public static boolean isLiving(Entity target) {
        return target instanceof LivingEntity livingEntity && livingEntity.isAlive() && !livingEntity.isDeadOrDying();
    }

    public static boolean isParasiteAttached(Entity target) {
        return target.hasPassenger(passenger -> passenger.getType().is(AVPEntityTypeTags.PARASITES));
    }

    public static boolean isSelfOrOtherParasiteAttached(Alien parasite, Entity target) {
        return target.hasPassenger(
            passenger -> passenger.equals(parasite) || passenger.getType().is(AVPEntityTypeTags.PARASITES)
        );
    }

    public static final Predicate<LivingEntity> IS_IMMORTAL = livingEntity -> livingEntity instanceof Player player && (player.isCreative()
        || player.isSpectator());
}
