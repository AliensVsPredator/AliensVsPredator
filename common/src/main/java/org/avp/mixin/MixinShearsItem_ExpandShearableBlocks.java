package org.avp.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.common.tag.AVPBlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public abstract class MixinShearsItem_ExpandShearableBlocks {
    @Inject(at = @At("RETURN"), method = "mineBlock", cancellable = true)
    void mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (blockState.is(AVPBlockTags.PADDING)) {
            callbackInfoReturnable.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "getDestroySpeed", cancellable = true)
    void getDestroySpeed(ItemStack itemStack, BlockState blockState, CallbackInfoReturnable<Float> callbackInfoReturnable) {
        if (blockState.is(AVPBlockTags.PADDING)) {
            callbackInfoReturnable.setReturnValue(5.0F);
        }
    }
}
