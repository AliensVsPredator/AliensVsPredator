package com.blib.internal.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.engine.EngineMode;

/**
 * Suppress the first-person hand/held-item render in engine mode. The freecam is detached from the player's eyes; the
 * hand following the camera looks wrong and obscures the editor HUD.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer_EngineMode {

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void blib$skipInEngineMode(
        float partialTicks,
        PoseStack poseStack,
        MultiBufferSource.BufferSource buffer,
        LocalPlayer playerEntity,
        int combinedLight,
        CallbackInfo ci
    ) {
        if (EngineMode.get().isActive()) {
            ci.cancel();
        }
    }
}
