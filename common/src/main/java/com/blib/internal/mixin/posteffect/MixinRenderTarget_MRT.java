package com.blib.internal.mixin.posteffect;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.posteffect.BLibIrisCompat;
import com.blib.internal.client.posteffect.BLibMainTargetMRT;

/**
 * Picks up the resize path: when {@link RenderTarget#createBuffers(int, int, boolean)} runs on a {@link MainTarget}
 * instance (which happens during window resize, since {@code MainTarget} doesn't override {@code createBuffers}),
 * re-allocate the MRT auxiliary attachments. {@code destroyBuffers} similarly cleans up our auxiliaries so resize
 * doesn't leak GL textures.
 * <p>
 * {@code instanceof MainTarget} guards against firing on the framework's own ping-pong targets and any other non-main
 * render targets in the JVM (entity outline, particle target, etc.).
 */
@Mixin(RenderTarget.class)
public abstract class MixinRenderTarget_MRT {

    @Inject(method = "createBuffers", at = @At("TAIL"))
    private void blib$attachAuxOnResize(int width, int height, boolean clearError, CallbackInfo ci) {
        if (BLibIrisCompat.isShaderModActive()) {
            return;
        }

        var self = (RenderTarget) (Object) this;

        if (self instanceof MainTarget) {
            BLibMainTargetMRT.attach(self.frameBufferId, self.viewWidth, self.viewHeight);
        }
    }

    @Inject(method = "destroyBuffers", at = @At("HEAD"))
    private void blib$destroyAuxOnDestroy(CallbackInfo ci) {
        var self = (RenderTarget) (Object) this;

        if (self instanceof MainTarget) {
            BLibMainTargetMRT.destroy();
        }
    }

    @Inject(
        method = "clear",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clear(IZ)V",
            shift = At.Shift.AFTER
        )
    )
    private void blib$clearAuxiliaryAttachments(boolean clearError, CallbackInfo ci) {
        if (BLibIrisCompat.isShaderModActive()) {
            return;
        }

        var self = (RenderTarget) (Object) this;

        if (self instanceof MainTarget) {
            BLibMainTargetMRT.restoreDrawBuffers();
            BLibMainTargetMRT.clearAuxiliaryAttachments();
        }
    }
}
