package com.blib.internal.mixin;

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

import com.blib.engine.session.EngineCameraFrame;

/**
 * Capture the projection + frustum (view) matrices and the camera position vanilla is about to render the world with,
 * so the engine's cursor-ray code can unproject the cursor through the same matrices the user is seeing. Without this
 * hook, the cursor ray has to reconstruct projection state from {@code mc.options.fov().get()} + screen aspect, which
 * misses vanilla's tick-interpolated FOV modifier (sprint, item-use, zoom, fluid) and produces a small constant angular
 * error that grows with camera distance.
 * <p>
 * HEAD is fine for capture — the matrices are immutable through the render call (they're built in
 * {@code GameRenderer.renderLevel} and passed in by reference). RETURN would also work but HEAD makes the captured
 * state available for any per-frame hover-detection code that runs later in the render call.
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer_EngineCameraFrame {

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void blib$captureFrame(
        DeltaTracker deltaTracker,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightTexture lightTexture,
        Matrix4f frustumMatrix,
        Matrix4f projectionMatrix,
        CallbackInfo ci
    ) {
        EngineCameraFrame.capture(camera.getPosition(), frustumMatrix, projectionMatrix);
    }
}
