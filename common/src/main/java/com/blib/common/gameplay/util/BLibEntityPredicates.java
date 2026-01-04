package com.blib.common.gameplay.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

public class BLibEntityPredicates {

    public static <T> Predicate<T> alwaysTrue() {
        return $ -> true;
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

    public static boolean isAlive(Entity target) {
        return target instanceof LivingEntity livingEntity && livingEntity.isAlive() && !livingEntity.isDeadOrDying();
    }

    public static boolean isBaby(Entity target) {
        return target instanceof LivingEntity livingEntity && livingEntity.isBaby();
    }

    public static boolean isInvulnerable(Entity target) {
        return target.isInvulnerable() || (target instanceof Player player && (player.isCreative() || player.isSpectator()));
    }
}
