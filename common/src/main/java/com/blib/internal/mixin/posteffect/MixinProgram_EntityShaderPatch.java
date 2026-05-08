package com.blib.internal.mixin.posteffect;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

import com.blib.internal.client.posteffect.BLibEntityShaderPatcher;

/**
 * Intercepts the source passed to {@code glShaderSource} during shader compilation and runs it through
 * {@link BLibEntityShaderPatcher} when the shader name matches the entity family. The patcher injects MRT auxiliary
 * outputs so vanilla entity draws populate the entity-mask + entity-lightmap framebuffer attachments automatically,
 * with no re-render pass.
 * <p>
 * Targets the lone {@code GlStateManager.glShaderSource(int, List)} call inside {@code Program.compileShaderInternal} —
 * at that point the source has already been split by {@code GlslPreprocessor.process} into one entry per logical chunk
 * and the {@code #moj_import} expansions are resolved, so we join, patch, and pass back as a single-entry list.
 */
@Mixin(Program.class)
public abstract class MixinProgram_EntityShaderPatch {

    @ModifyArg(
        method = "compileShaderInternal",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/GlStateManager;glShaderSource(ILjava/util/List;)V"
        ),
        index = 1
    )
    private static List<String> blib$patchEntityShaderSource(
        List<String> source,
        @Local(argsOnly = true) Program.Type type,
        @Local(argsOnly = true, ordinal = 0) String name
    ) {
        var joined = String.join("", source);
        var patched = BLibEntityShaderPatcher.transform(name, type, joined);

        if (patched == joined) {
            return source;
        }

        return List.of(patched);
    }
}
