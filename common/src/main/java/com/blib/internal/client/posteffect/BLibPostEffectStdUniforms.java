package com.blib.internal.client.posteffect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

/**
 * Resolves and applies the framework's standard uniforms to an effect's shader before its draw. Each uniform is looked
 * up by name; missing uniforms (i.e. ones the effect's GLSL doesn't declare) are silently skipped. The effect's GLSL
 * declares whatever subset it actually wants via {@code #include "/blib_post/std.glsl"}.
 */
@ApiStatus.Internal
public final class BLibPostEffectStdUniforms {

    private static final long SESSION_START_MS = Util.getMillis();

    private BLibPostEffectStdUniforms() {
        throw new UnsupportedOperationException();
    }

    public static void apply(ShaderInstance shader, DeltaTracker deltaTracker, int width, int height) {
        var mc = Minecraft.getInstance();
        var partial = deltaTracker.getGameTimeDeltaPartialTick(false);

        setFloat(shader, "time", (Util.getMillis() - SESSION_START_MS) / 1000.0F);
        setFloat(shader, "partialTick", partial);
        setFloat2(shader, "outSize", (float) width, (float) height);

        var camera = mc.gameRenderer.getMainCamera();

        if (camera.isInitialized()) {
            var p = camera.getPosition();
            setFloat3(shader, "cameraPos", (float) p.x, (float) p.y, (float) p.z);
        } else {
            setFloat3(shader, "cameraPos", 0.0F, 0.0F, 0.0F);
        }

        var level = mc.level;
        setFloat(shader, "sunAngle", level == null ? 0.0F : level.getSunAngle(partial));

        // Dimension ambient block-light floor in [0, 1] — drives a thermal baseline so ultra-warm dimensions
        // (Nether by default; mods can declare additional ultra-warm dims) read as green-ish ambient even where
        // there's no actual block light. 7/15 ≈ 0.467 lands roughly on the green band of the gradient.
        var ultraWarm = level != null && level.dimensionType().ultraWarm();
        setFloat(shader, "ultrawarmAmbient", ultraWarm ? 7.0F / 15.0F : 0.0F);

        var player = mc.player;
        setFloat(shader, "nightVision", nightVisionScale(player, partial));
        setFloat(shader, "blindness", effectStrength(player, MobEffects.BLINDNESS));
        setFloat(shader, "darkness", effectStrength(player, MobEffects.DARKNESS));

        var proj = RenderSystem.getProjectionMatrix();
        if (proj != null) {
            var inv = new Matrix4f(proj).invert();
            setMatrix4(shader, "invProjMat", inv);
        }

        // Camera view matrix + its inverse, captured by MixinLevelRenderer_BLibState at the top of every level
        // render. Post shaders reconstruct world-relative-to-camera position from depth via:
        // ndc → invProjMat → view-space → gbufferModelViewInverse → world-relative-to-camera
        if (BLibLevelRenderState.isCaptured()) {
            setMatrix4(shader, "gbufferModelView", BLibLevelRenderState.viewMatrix());
            setMatrix4(shader, "gbufferModelViewInverse", BLibLevelRenderState.viewMatrixInverse());
        }
        setInt(shader, "frameCounter", BLibLevelRenderState.frameCounter());
    }

    private static void setInt(ShaderInstance shader, String name, int value) {
        var u = shader.getUniform(name);

        if (u != null) {
            u.set(value);
        }
    }

    private static float nightVisionScale(@org.jetbrains.annotations.Nullable LivingEntity entity, float partial) {
        if (entity == null || !entity.hasEffect(MobEffects.NIGHT_VISION)) {
            return 0.0F;
        }

        return GameRenderer.getNightVisionScale(entity, partial);
    }

    private static float effectStrength(
        @org.jetbrains.annotations.Nullable LivingEntity entity,
        net.minecraft.core.Holder<MobEffect> effect
    ) {
        if (entity == null) {
            return 0.0F;
        }

        var instance = entity.getEffect(effect);

        if (instance == null) {
            return 0.0F;
        }

        return Math.min((instance.getAmplifier() + 1) / 4.0F, 1.0F);
    }

    private static void setFloat(ShaderInstance shader, String name, float value) {
        var u = shader.getUniform(name);

        if (u != null) {
            u.set(value);
        }
    }

    private static void setFloat2(ShaderInstance shader, String name, float a, float b) {
        var u = shader.getUniform(name);

        if (u != null) {
            u.set(a, b);
        }
    }

    private static void setFloat3(ShaderInstance shader, String name, float a, float b, float c) {
        var u = shader.getUniform(name);

        if (u != null) {
            u.set(a, b, c);
        }
    }

    private static void setMatrix4(ShaderInstance shader, String name, Matrix4f m) {
        var u = shader.getUniform(name);

        if (u != null) {
            u.set(m);
        }
    }
}
