package com.blib.internal.mixin.posteffect;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.posteffect.BLibLevelRenderState;

/**
 * Captures the camera's frustum matrix (= pure view matrix) at the top of every level render so post-effect shaders can
 * reconstruct world-relative-to-camera positions and so the per-bone lighting mixin can undo the camera rotation to
 * extract bone world positions from PoseStack matrices.
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer_BLibState {

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void blib$captureCameraState(
        DeltaTracker deltaTracker,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightTexture lightTexture,
        Matrix4f frustumMatrix,
        Matrix4f projectionMatrix,
        CallbackInfo ci
    ) {
        BLibLevelRenderState.capture(frustumMatrix, projectionMatrix);
    }
}
