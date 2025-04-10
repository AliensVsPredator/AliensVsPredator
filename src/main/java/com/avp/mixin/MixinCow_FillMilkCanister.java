package com.avp.mixin;

import com.avp.common.component.DataComponents;
import com.avp.common.item.AVPItems;
import com.avp.common.item.CanisterItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
public abstract class MixinCow_FillMilkCanister extends Animal {

    protected MixinCow_FillMilkCanister(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void mobInteractMixin(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(AVPItems.CANISTER) || (itemStack.is(AVPItems.MILK_CANISTER) && !this.isBaby())) {
            int contentAmount = itemStack.getOrDefault(DataComponents.CANISTER_CONTENT_AMOUNT, 0);

            ItemStack updatedStack;
            if (contentAmount == 0)
                updatedStack = ItemUtils.createFilledResult(itemStack, player, AVPItems.MILK_CANISTER.getDefaultInstance());

            else if (contentAmount < 8)
                updatedStack = CanisterItem.updateContentAmount(itemStack, 1);

            else {
                cir.setReturnValue(InteractionResult.PASS);
                return;
            }

            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            player.setItemInHand(hand, updatedStack);
            cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
        }
    }
}
