package com.avp.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.avp.common.item.GunItem;
import com.avp.common.item.gun.GunData;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer_AdjustArmPoseForGun {

    @Inject(method = "getArmPose", at = @At(value = "TAIL"), cancellable = true)
    private static void tryItemPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> ci) {
        var itemstack = player.getItemInHand(hand);
        if (
            itemstack.getItem() instanceof GunItem gunItem &&
                (gunItem.getGunConfig() == GunData.OLD_PAINLESS ||
                    gunItem.getGunConfig() == GunData.FLAMETHROWER_SEVASTOPOL ||
                    gunItem.getGunConfig() == GunData.M56_SMARTGUN)
        ) {
            ci.setReturnValue(HumanoidModel.ArmPose.BOW_AND_ARROW);
        } else if (itemstack.getItem() instanceof GunItem gunItem && gunItem.getGunConfig() != GunData.OLD_PAINLESS && player.isUsingItem()) {
            ci.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
        }
    }
}
