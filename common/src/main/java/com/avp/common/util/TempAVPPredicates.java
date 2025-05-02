package com.avp.common.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Predicate;

import com.avp.common.effect.AVPMobEffects;
import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.item.AVPItemTags;

// TODO: Rename this once multi-loader migration is finished.
public class TempAVPPredicates {

    public static final Predicate<LivingEntity> IS_IMMORTAL = livingEntity -> livingEntity instanceof Player player && (player.isCreative()
        || player.isSpectator());

    public static <T> Predicate<T> alwaysTrue() {
        return $ -> true;
    }

    public static boolean canBeIrradiated(Entity entity) {
        if (
            // If this is not a living entity...
            !(entity instanceof LivingEntity livingEntity)
                // Or if the entity is radiation-resistant...
                || livingEntity.getType().is(AVPEntityTypeTags.RADIATION_RESISTANT)
                // Or if the living entity is immortal...
                || IS_IMMORTAL.test(livingEntity)
                // Or if the living entity already has the radiation effect...
                || livingEntity.hasEffect(AVPMobEffects.RADIATION)
                // Or if the entity is no longer alive...
                || !livingEntity.isAlive()
        ) {
            // Then we don't want to or can't reasonably apply the radiation effect. Abort.
            return false;
        }

        var hasFullRadiationResistantArmor = hasFullArmorSetMatching(
            livingEntity,
            itemStack -> itemStack.is(AVPItemTags.RADIATION_RESISTANT_ARMOR)
        );

        // Entity should not have a full set of radiation-resistant armor.
        return !hasFullRadiationResistantArmor;
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

    public static boolean isLiving(Entity target) {
        return target instanceof LivingEntity livingEntity && livingEntity.isAlive() && !livingEntity.isDeadOrDying();
    }
}
