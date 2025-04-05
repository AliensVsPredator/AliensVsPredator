package com.avp.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.item.AVPItemTags;
import com.avp.common.util.AVPPredicates;

@Mixin(Item.class)
public class MixinItem_GiveRads {

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void giveRads(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected, CallbackInfo ci) {
        if (
            !level.isClientSide() && stack.is(AVPItemTags.RADIATION_ITEMS) && entity instanceof LivingEntity livingEntity
                && livingEntity.isAlive() && !livingEntity.getType().is(AVPEntityTypeTags.RADIATION_RESISTANT)
        ) {
            var armorCheck = livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.LEGS).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.FEET).is(AVPItemTags.RADIATION_RESISTANT_ARMOR);
            if (!armorCheck && !AVPPredicates.IS_IMMORTAL.test(livingEntity)) {
                livingEntity.addEffect(
                    new net.minecraft.world.effect.MobEffectInstance(
                        com.avp.common.effect.AVPEffects.RADIATION_EFFECT,
                        Integer.MAX_VALUE,
                        0
                    )
                );
            }
        }
    }
}
