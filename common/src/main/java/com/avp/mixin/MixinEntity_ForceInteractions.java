package com.avp.mixin;

import com.lib.common.registry.GeneBonusDataRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.avp.common.registry.init.item.AVPItems;

@Mixin(Entity.class)
public abstract class MixinEntity_ForceInteractions {

    @Inject(at = @At("HEAD"), method = "interact", cancellable = true)
    public void tick(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        var self = Entity.class.cast(this);

        var itemstack = player.getItemInHand(hand);

        if (self instanceof LivingEntity livingSelf) {

            if (avp$syringeCheck(itemstack, self) || avp$geneReaderCheck(itemstack)) {
                itemstack.getItem().interactLivingEntity(itemstack, player, livingSelf, hand);
                cir.setReturnValue(InteractionResult.sidedSuccess(self.level().isClientSide));
            }
        }
    }

    @Unique
    private static boolean avp$syringeCheck(ItemStack itemstack, Entity self) {
        return itemstack.is(AVPItems.SYRINGE.get()) && GeneBonusDataRegistry.has(self.getType());
    }

    @Unique
    private static boolean avp$geneReaderCheck(ItemStack itemstack) {
        return itemstack.is(AVPItems.GENE_READER.get());
    }
}
