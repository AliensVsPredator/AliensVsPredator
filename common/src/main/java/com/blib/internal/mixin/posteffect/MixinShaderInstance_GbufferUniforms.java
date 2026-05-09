package com.blib.internal.mixin.posteffect;

import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.blib.internal.client.posteffect.BLibGbufferUniforms;

/**
 * After every {@link ShaderInstance#apply()} call, push patcher-injected uniforms (currently just {@code BlibHeldItem}
 * for held-item flagging) to the bound program. Vanilla MC's ShaderInstance only uploads uniforms declared in the
 * shader JSON; uniforms our patcher injects into the GLSL source aren't in the JSON, so we set them via raw GL after
 * vanilla's upload completes.
 */
@Mixin(ShaderInstance.class)
public abstract class MixinShaderInstance_GbufferUniforms {

    @Inject(method = "apply", at = @At("TAIL"))
    private void blib$applyPatcherUniforms(CallbackInfo ci) {
        var self = (ShaderInstance) (Object) this;
        BLibGbufferUniforms.apply(self.getId(), self.getName());
    }
}
