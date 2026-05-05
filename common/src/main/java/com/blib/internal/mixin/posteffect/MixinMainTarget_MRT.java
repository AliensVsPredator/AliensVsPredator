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
 * Hooks {@link MainTarget#createFrameBuffer(int, int)} (the constructor's framebuffer setup) to allocate the MRT
 * auxiliary attachments after vanilla's color + depth setup completes. The companion mixin on {@link RenderTarget}
 * handles the resize path.
 * <p>
 * No-op when a shader mod (Iris/Oculus) is loaded — that mod owns the framebuffer in that case.
 */
@Mixin(MainTarget.class)
public abstract class MixinMainTarget_MRT {

    @Inject(method = "createFrameBuffer", at = @At("TAIL"))
    private void blib$attachAuxAttachments(int width, int height, CallbackInfo ci) {
        if (BLibIrisCompat.isShaderModActive()) {
            return;
        }

        var self = (RenderTarget) (Object) this;
        BLibMainTargetMRT.attach(self.frameBufferId, self.viewWidth, self.viewHeight);
    }
}
