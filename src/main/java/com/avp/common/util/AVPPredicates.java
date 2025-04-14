package com.avp.common.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;
import java.util.function.Predicate;

import com.avp.common.effect.AVPEffects;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.living.Host;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.yautja.Yautja;
import com.avp.common.item.AVPItemTags;

public class AVPPredicates {

    public static <T> Predicate<T> alwaysTrue() {
        return $ -> true;
    }

    public static boolean areAliensEnemies(Alien first, Alien second) {
        var isSecondAberrant = second.isAberrant();
        var isSecondIrradiated = second.isIrradiated();
        var isSecondNetherAfflicted = second.isNetherAfflicted();
        var firstHiveSignatureOption = first.hiveManager().signature();
        var secondHiveSignatureOption = second.hiveManager().signature();

        if (
            !Objects.equals(first.isAberrant(), isSecondAberrant)
                || !Objects.equals(first.isIrradiated(), isSecondIrradiated)
                || !Objects.equals(first.isNetherAfflicted(), isSecondNetherAfflicted)
        ) {
            return true;
        }

        if (firstHiveSignatureOption.isNone() || secondHiveSignatureOption.isNone()) {
            return false;
        }

        // Only attack other aliens under these conditions.
        return !Objects.equals(firstHiveSignatureOption, secondHiveSignatureOption);
    }

    public static boolean canBeIrradiated(Entity entity) {
        if (
            // If this is not a living entity...
            !(entity instanceof LivingEntity livingEntity)
                // Or if the entity is radiation-resistant...
                || livingEntity.getType().is(AVPEntityTypeTags.RADIATION_RESISTANT)
                // Or if the living entity is immortal...
                || AVPPredicates.IS_IMMORTAL.test(livingEntity)
                // Or if the living entity already has the radiation effect...
                || livingEntity.hasEffect(AVPEffects.RADIATION_EFFECT)
                // Or if the entity is no longer alive...
                || !livingEntity.isAlive()
        ) {
            // Then we don't want to or can't reasonably apply the radiation effect. Abort.
            return false;
        }

        var hasFullRadiationResistantArmor = AVPPredicates.hasFullArmorSetMatching(
            livingEntity,
            itemStack -> itemStack.is(AVPItemTags.RADIATION_RESISTANT_ARMOR)
        );

        // Entity should not have a full set of radiation-resistant armor.
        return !hasFullRadiationResistantArmor;
    }

    public static boolean containsIrradiatedItems(Container container) {
        for (var i = 0; i < container.getContainerSize(); i++) {
            var itemStack = container.getItem(i);

            if (!itemStack.isEmpty() && itemStack.is(AVPItemTags.RADIATION_ITEMS)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasEmbryo(Entity target) {
        return target instanceof Host host && host.parasiteType() != null;
    }

    public static boolean hasFullArmorSetMatching(LivingEntity livingEntity, Predicate<ItemStack> itemStackPredicate) {
        return itemStackPredicate.test(livingEntity.getItemBySlot(EquipmentSlot.HEAD))
            && itemStackPredicate.test(livingEntity.getItemBySlot(EquipmentSlot.CHEST))
            && itemStackPredicate.test(livingEntity.getItemBySlot(EquipmentSlot.LEGS))
            && itemStackPredicate.test(livingEntity.getItemBySlot(EquipmentSlot.FEET));
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
            !isSelfOrOtherParasiteAttached(parasite, hostTarget)
            && !HAS_MASK.test((LivingEntity) hostTarget)
            && !HAS_FACE_MASK.test((LivingEntity) hostTarget);
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

    public static final Predicate<LivingEntity> HAS_FACE_MASK = livingEntity -> livingEntity.getItemBySlot(
        EquipmentSlot.HEAD
    ).is(AVPItemTags.FACEHUGGER_PROTECTION_HELMET);

    public static final Predicate<LivingEntity> HAS_MASK = livingEntity -> livingEntity instanceof Yautja yautja && yautja.yautjaMaskManager
        .hasMask();
}
