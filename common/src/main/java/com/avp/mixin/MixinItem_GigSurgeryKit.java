package com.avp.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.compat.gigeresque.GigCommonCompat;
import com.avp.service.Services;

@Mixin(Item.class)
public class MixinItem_GigSurgeryKit {

    @Inject(method = "interactLivingEntity", at = @At("HEAD"))
    private void avp$removeParasiteOnEntity(
        ItemStack stack,
        Player player,
        LivingEntity interactionTarget,
        InteractionHand usedHand,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (Services.PLATFORM.isModLoaded("gigeresque")) {
            GigCommonCompat.removeParasite(player, interactionTarget, stack);
        }
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void avp$removeParasiteOnUse(
        Level level,
        Player player,
        InteractionHand usedHand,
        CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir
    ) {
        if (Services.PLATFORM.isModLoaded("gigeresque")) {
            GigCommonCompat.removeParasite(player, player, player.getItemInHand(usedHand));
        }
    }
}
