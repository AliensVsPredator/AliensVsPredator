package com.avp.core.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.core.common.item.ArmorItems;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_ApplyArmorEffects extends Entity {

    @Shadow
    protected abstract int increaseAirSupply(int airSupply);

    protected MixinLivingEntity_ApplyArmorEffects(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var self = LivingEntity.class.cast(this);

        var supplyAir = false;

        if (isWearingFullMK50SuitArmor(self)) {
            self.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 0, true, false, true));
            supplyAir = true;
        } else if (isWearingFullPressureSuitArmor(self)) {
            supplyAir = true;
        } else if (isWearingFullNetherChitinArmor(self)) {
            self.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 5, 0, true, false, true));
        }

        if (supplyAir) {
            setAirSupply(increaseAirSupply(getAirSupply()));
        }
    }

    @Unique
    private boolean isWearingFullNetherChitinArmor(LivingEntity self) {
        var head = self.getItemBySlot(EquipmentSlot.HEAD);
        var chest = self.getItemBySlot(EquipmentSlot.CHEST);
        var legs = self.getItemBySlot(EquipmentSlot.LEGS);
        var feet = self.getItemBySlot(EquipmentSlot.FEET);

        return (head.is(ArmorItems.NETHER_CHITIN_HELMET.get()) || head.is(ArmorItems.PLATED_NETHER_CHITIN_HELMET.get()))
            && (chest.is(ArmorItems.NETHER_CHITIN_CHESTPLATE.get()) || chest.is(ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get()))
            && (legs.is(ArmorItems.NETHER_CHITIN_LEGGINGS.get()) || legs.is(ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get()))
            && (feet.is(ArmorItems.NETHER_CHITIN_BOOTS.get()) || feet.is(ArmorItems.PLATED_NETHER_CHITIN_BOOTS.get()));
    }

    @Unique
    private boolean isWearingFullMK50SuitArmor(LivingEntity self) {
        return self.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.MK50_HELMET.get()) &&
            self.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.MK50_CHESTPLATE.get()) &&
            self.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.MK50_LEGGINGS.get()) &&
            self.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.MK50_BOOTS.get());
    }

    @Unique
    private boolean isWearingFullPressureSuitArmor(LivingEntity self) {
        return self.getItemBySlot(EquipmentSlot.HEAD).is(ArmorItems.PRESSURE_HELMET.get()) &&
            self.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.PRESSURE_CHESTPLATE.get()) &&
            self.getItemBySlot(EquipmentSlot.LEGS).is(ArmorItems.PRESSURE_LEGGINGS.get()) &&
            self.getItemBySlot(EquipmentSlot.FEET).is(ArmorItems.PRESSURE_BOOTS.get());
    }
}
