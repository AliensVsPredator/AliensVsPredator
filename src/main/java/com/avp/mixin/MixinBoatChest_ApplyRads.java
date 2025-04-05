package com.avp.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.avp.common.effect.AVPEffects;
import com.avp.common.item.AVPItemTags;
import com.avp.common.util.AVPPredicates;

@Mixin(ChestBoat.class)
public abstract class MixinBoatChest_ApplyRads extends Entity {

    public MixinBoatChest_ApplyRads(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        super.tick();
        var chest = (ChestBoat) (Object) this;

        if (containsRadiationItems(chest)) {
            applyRadiationEffect(chest);
        }
    }

    @Unique
    private boolean containsRadiationItems(ChestBoat chest) {
        for (var i = 0; i < chest.getContainerSize(); i++) {
            var itemStack = chest.getItem(i);
            if (!itemStack.isEmpty() && itemStack.is(AVPItemTags.RADIATION_ITEMS)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private void applyRadiationEffect(ChestBoat chest) {
        var effectRadius = chest.getBoundingBox().inflate(3);

        var nearbyEntities = chest.level()
            .getEntitiesOfClass(
                LivingEntity.class,
                effectRadius,
                Entity::isAlive
            );

        for (var livingEntity : nearbyEntities) {
            var armorCheck = livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.LEGS).is(AVPItemTags.RADIATION_RESISTANT_ARMOR) &&
                livingEntity.getItemBySlot(EquipmentSlot.FEET).is(AVPItemTags.RADIATION_RESISTANT_ARMOR);
            if (!armorCheck || !AVPPredicates.IS_IMMORTAL.test(livingEntity)) {
                livingEntity.addEffect(new MobEffectInstance(AVPEffects.RADIATION_EFFECT, Integer.MAX_VALUE, 0));
            }
        }
    }
}
