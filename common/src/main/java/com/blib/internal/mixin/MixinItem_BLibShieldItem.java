package com.blib.internal.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.blib.api.common.shield.v1.BLibShieldItem;

/**
 * Wires vanilla-shield-style use semantics into items that implement {@link BLibShieldItem}, so each shield
 * implementation doesn't need to override {@code Item.use}, {@code Item.getUseAnimation}, and
 * {@code Item.getUseDuration} just to behave like a shield. Each injection cancels at HEAD so the implementing class
 * doesn't need to call {@code super}.
 * <p>
 * Caveat: a {@code BLibShieldItem} subclass that itself overrides any of these methods without calling {@code super}
 * will bypass these injections — that's intentional, so a custom item can opt into the shield pipeline while keeping
 * its own {@code use} behavior (e.g. a sword that throws on right-click but still blocks while held).
 */
@Mixin(Item.class)
public abstract class MixinItem_BLibShieldItem {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void blib$shieldStartUsing(
        Level level,
        Player player,
        InteractionHand hand,
        CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir
    ) {
        if (!(this instanceof BLibShieldItem))
            return;

        var stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        cir.setReturnValue(InteractionResultHolder.consume(stack));
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void blib$shieldUseAnimation(ItemStack stack, CallbackInfoReturnable<UseAnim> cir) {
        if (!(this instanceof BLibShieldItem))
            return;

        cir.setReturnValue(UseAnim.BLOCK);
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void blib$shieldUseDuration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!(this instanceof BLibShieldItem shield))
            return;

        cir.setReturnValue(shield.getShieldConfig().useDuration());
    }
}
