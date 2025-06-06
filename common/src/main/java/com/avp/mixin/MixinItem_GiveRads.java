package com.avp.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.common.gameplay.effect.RadiationStatusEffect;
import com.avp.common.registry.init.AVPMobEffects;
import com.avp.common.registry.tag.AVPItemTags;
import com.avp.common.util.AVPPredicates;

@Mixin(Item.class)
public class MixinItem_GiveRads {

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void giveRads(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected, CallbackInfo ci) {
        if (
            // Only want to run this logic server-side.
            level.isClientSide()
                // Only run this logic for radiation-emitting items.
                || !stack.is(AVPItemTags.RADIOACTIVE_ITEMS)
                // Only run this logic if the entity can be irradiated.
                || !AVPPredicates.canBeIrradiated(entity)
                // Sanity check + allow compiler to assert entity type to get livingEntity ref access.
                || !(entity instanceof LivingEntity livingEntity)
        ) {
            return;
        }

        // Apply the radiation effect.
        var mobEffectInstance = new MobEffectInstance(
            AVPMobEffects.RADIATION.getHolder(),
            RadiationStatusEffect.EFFECT_DURATION_IN_TICKS,
            0
        );
        livingEntity.addEffect(mobEffectInstance);
    }
}
