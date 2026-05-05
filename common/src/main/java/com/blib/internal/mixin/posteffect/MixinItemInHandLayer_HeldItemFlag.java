package com.blib.internal.mixin.posteffect;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.posteffect.BLibHeldItemRenderState;

/**
 * Pushes the held-item flag around {@link ItemInHandLayer#render}, so third-person held items (other players,
 * mobs, your own player when in third-person) bypass thermal recoloring just like the first-person hand.
 */
@Mixin(ItemInHandLayer.class)
public abstract class MixinItemInHandLayer_HeldItemFlag {

    @Inject(method = "render", at = @At("HEAD"))
    private void blib$pushHeldItemFlag(
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        LivingEntity livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        BLibHeldItemRenderState.push();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void blib$popHeldItemFlag(
        PoseStack poseStack,
        MultiBufferSource buffer,
        int packedLight,
        LivingEntity livingEntity,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        CallbackInfo ci
    ) {
        BLibHeldItemRenderState.pop();
    }
}
