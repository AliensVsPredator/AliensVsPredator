package com.blib.internal.mixin.posteffect;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.posteffect.BLibHeldItemRenderState;

/**
 * Pushes the held-item flag around {@link GameRenderer}'s first-person {@code renderItemInHand}, so the patched
 * fragment shaders write the held-item mask category and the thermal post pass leaves those pixels untouched.
 */
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer_HeldItemFlag {

    @Inject(method = "renderItemInHand", at = @At("HEAD"))
    private void blib$pushHeldItemFlag(Camera camera, float partialTick, Matrix4f projectionMatrix, CallbackInfo ci) {
        BLibHeldItemRenderState.push();
    }

    @Inject(method = "renderItemInHand", at = @At("RETURN"))
    private void blib$popHeldItemFlag(Camera camera, float partialTick, Matrix4f projectionMatrix, CallbackInfo ci) {
        BLibHeldItemRenderState.pop();
    }
}
