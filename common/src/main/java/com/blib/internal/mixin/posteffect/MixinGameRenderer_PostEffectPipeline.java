package com.blib.internal.mixin.posteffect;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.posteffect.BLibPostEffectPipeline;

/**
 * Drives the post-effect pipeline once per frame: injects right after vanilla rebinds the main render target (the call
 * following {@code postEffect.process} / {@code doEntityOutline}), so the MainTarget is the active draw FBO and the
 * level pass + any vanilla post chain are complete. The pipeline runs, re-targets MainTarget, and lets vanilla proceed
 * into GUI rendering.
 */
@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer_PostEffectPipeline {

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V",
            shift = At.Shift.AFTER
        )
    )
    private void blib$runPostEffectPipeline(DeltaTracker deltaTracker, boolean tick, CallbackInfo ci) {
        BLibPostEffectPipeline.run(deltaTracker);
    }
}
