package com.avp.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.avp.common.registry.init.AVPMobEffects;
import com.avp.common.registry.tag.AVPItemTags;

@Mixin(Item.class)
public class MixinItem_RemoveRads {

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void removeRadiationEffectWhenUsingGoldenApple(
        ItemStack stack,
        Level level,
        LivingEntity livingEntity,
        CallbackInfoReturnable<ItemStack> cir
    ) {
        if (stack.is(AVPItemTags.RADIATION_CURE_ITEMS) && livingEntity.hasEffect(AVPMobEffects.RADIATION.getHolder())) {
            livingEntity.removeEffect(AVPMobEffects.RADIATION.getHolder());
        }
    }

}
