package org.avp.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.avp.common.item.AbstractAVPWeaponItem;

/**
 * @author Boston Vanseghi
 */
@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer {

    @Inject(
        at = @At(
            value = "HEAD",
            target = "net/minecraft/client/renderer/entity/player/PlayerRenderer.getArmPose(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;"
        ),
        method = { "getArmPose" },
        cancellable = true
    )
    private static void getArmPose(
        AbstractClientPlayer abstractClientPlayer,
        InteractionHand interactionHand,
        CallbackInfoReturnable<HumanoidModel.ArmPose> callbackInfo
    ) {
        var itemStackInHand = abstractClientPlayer.getItemInHand(interactionHand);

        if (itemStackInHand.isEmpty()) {
            return;
        }

        if (itemStackInHand.getItem() instanceof AbstractAVPWeaponItem) {
            callbackInfo.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
        }
    }
}
