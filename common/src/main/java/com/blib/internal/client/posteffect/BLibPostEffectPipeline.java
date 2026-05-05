package com.blib.internal.client.posteffect;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.Comparator;

import com.blib.api.client.shader.v1.BLibPostEffectInput;
import com.blib.api.client.shader.v1.BLibPostEffectUniform;

/**
 * Per-frame runner. Walks {@link BLibPostEffectRegistry#ALL}, filters by active state, ping-pongs source/dest across
 * two {@link BLibPostEffectFramebuffers} targets while compositing, and blits the final result back to the MainTarget's
 * color attachment.
 * <p>
 * No-ops when Iris is active (Iris owns post-processing) or when no effects are active.
 */
@ApiStatus.Internal
public final class BLibPostEffectPipeline {

    private BLibPostEffectPipeline() {
        throw new UnsupportedOperationException();
    }

    public static void run(DeltaTracker deltaTracker) {
        if (BLibIrisCompat.isShaderModActive()) {
            return;
        }

        var registry = BLibPostEffectRegistry.ALL;

        if (registry.isEmpty()) {
            return;
        }

        var active = registry.stream()
            .filter(BLibPostEffectImpl::isActive)
            .filter(e -> e.shaderInstance() != null)
            .sorted(Comparator.comparingInt(e -> e.spec().priority()))
            .toList();

        if (active.isEmpty()) {
            return;
        }

        var mc = Minecraft.getInstance();
        var mainTarget = mc.getMainRenderTarget();

        if (mainTarget == null) {
            return;
        }

        var fbs = BLibPostEffectFramebuffers.INSTANCE;
        fbs.ensureSize(mainTarget.width, mainTarget.height);

        var targetA = fbs.targetA();
        var targetB = fbs.targetB();

        if (targetA == null || targetB == null) {
            return;
        }

        // Save GL state we plan to perturb. (We rely on MC's RenderSystem state being consistent on the way in.)
        GlStateManager._disableDepthTest();
        GlStateManager._depthMask(false);
        GlStateManager._disableBlend();
        GlStateManager._colorMask(true, true, true, true);

        // Stage 0: copy MainTarget color → targetA via an FBO blit.
        blitColor(mainTarget, targetA);

        var source = targetA;
        var dest = targetB;

        for (var effect : active) {
            applyEffect(effect, source, dest, mainTarget, deltaTracker);
            var swap = source;
            source = dest;
            dest = swap;
        }

        // Final stage: copy whatever ended up in `source` back to MainTarget color.
        blitColor(source, mainTarget);

        // Restore main framebuffer binding for subsequent GUI render.
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, mainTarget.frameBufferId);
        RenderSystem.viewport(0, 0, mainTarget.width, mainTarget.height);

        GlStateManager._depthMask(true);
        GlStateManager._enableDepthTest();
    }

    private static void applyEffect(
        BLibPostEffectImpl effect,
        RenderTarget source,
        RenderTarget dest,
        RenderTarget mainTarget,
        DeltaTracker deltaTracker
    ) {
        var shader = effect.shaderInstance();

        if (shader == null) {
            return;
        }

        dest.bindWrite(true);

        var inputs = effect.spec().inputs();

        if (inputs.contains(BLibPostEffectInput.COLOR_TEXTURE)) {
            shader.setSampler("DiffuseSampler", source.getColorTextureId());
        }

        if (inputs.contains(BLibPostEffectInput.DEPTH_TEXTURE)) {
            shader.setSampler("depthtex0", mainTarget.getDepthTextureId());
        }

        if (inputs.contains(BLibPostEffectInput.LIGHTMAP_TEXTURE)) {
            var lightmap = mc().gameRenderer.lightTexture();
            var loc = ((com.blib.internal.mixin.posteffect.LightTextureAccessor) lightmap).blib$getLightTextureLocation();
            var tex = mc().getTextureManager().getTexture(loc);

            if (tex != null) {
                shader.setSampler("lightmap", tex.getId());
            }
        }

        if (inputs.contains(BLibPostEffectInput.ENTITY_MASK) && BLibMainTargetMRT.isAttached()) {
            shader.setSampler("entityMask", BLibMainTargetMRT.entityMaskTextureId());
        }

        if (inputs.contains(BLibPostEffectInput.ENTITY_LIGHTMAP) && BLibMainTargetMRT.isAttached()) {
            shader.setSampler("entityLightmap", BLibMainTargetMRT.entityLightmapTextureId());
        }

        BLibPostEffectStdUniforms.apply(shader, deltaTracker, dest.width, dest.height);
        applyEffectUniforms(shader, effect.spec().uniforms());

        shader.apply();

        drawFullscreenQuad();

        shader.clear();
    }

    private static void applyEffectUniforms(ShaderInstance shader, java.util.List<BLibPostEffectUniform> uniforms) {
        for (var u : uniforms) {
            var slot = shader.getUniform(u.name());

            if (slot == null) {
                continue;
            }

            switch (u) {
                case BLibPostEffectUniform.Float1 f -> slot.set(f.value().getAsFloat());
                case BLibPostEffectUniform.Float2 f -> {
                    var v = f.value().get();
                    slot.set(v.x, v.y);
                }
                case BLibPostEffectUniform.Float3 f -> {
                    var v = f.value().get();
                    slot.set(v.x, v.y, v.z);
                }
                case BLibPostEffectUniform.Float4 f -> {
                    var v = f.value().get();
                    slot.set(v.x, v.y, v.z, v.w);
                }
                case BLibPostEffectUniform.Int1 i -> slot.set(i.value().getAsInt());
                case BLibPostEffectUniform.Matrix4 m -> slot.set(m.value().get());
                case BLibPostEffectUniform.Texture t -> {
                    var loc = t.value().get();
                    var tex = Minecraft.getInstance().getTextureManager().getTexture(loc);
                    if (tex != null) {
                        shader.setSampler(t.name(), tex.getId());
                    }
                }
            }
        }
    }

    private static void drawFullscreenQuad() {
        var bb = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bb.addVertex(0.0F, 0.0F, 0.0F);
        bb.addVertex(1.0F, 0.0F, 0.0F);
        bb.addVertex(1.0F, 1.0F, 0.0F);
        bb.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(bb.buildOrThrow());
    }

    private static void blitColor(RenderTarget src, RenderTarget dst) {
        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, src.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, dst.frameBufferId);
        GL30.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GL30.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);
        GL30.glBlitFramebuffer(
            0,
            0,
            src.width,
            src.height,
            0,
            0,
            dst.width,
            dst.height,
            GL11.GL_COLOR_BUFFER_BIT,
            GL11.GL_NEAREST
        );
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    private static Minecraft mc() {
        return Minecraft.getInstance();
    }
}
