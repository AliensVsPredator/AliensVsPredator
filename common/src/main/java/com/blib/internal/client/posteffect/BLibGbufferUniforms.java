package com.blib.internal.client.posteffect;

import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Per-shader hooks that run after every {@code ShaderInstance.apply()}:
 * <ol>
 *   <li>Sets the patcher-injected {@code BlibHeldItem} uniform to flag held-item draws (the patched fragment
 *       writes the {@code 0.875} held-item mask category when this is non-zero, which the thermal post detects
 *       and short-circuits to original color).</li>
 *   <li>Sets the patcher-injected {@code BlibBackgroundEntity} (lane A) and {@code BlibBackgroundEntity2} (lane B)
 *       uniforms to flag entity draws that should render as part of the world background. The patched fragment packs
 *       the two lanes into {@code entityMask.g} as {@code 0.25 * laneA + 0.5 * laneB}, so the four combinations land
 *       at 0.0 / 0.25 / 0.5 / 0.75. Consumer post-effects sample {@code .g} and decode to per-lane flags (or just
 *       check {@code .g > 0.5} for legacy "any background" behavior, since that matches lane B alone).</li>
 *   <li>Toggles {@code glColorMaski} for the auxiliary attachments (1-6) based on whether the bound shader is
 *       one we've categorized for the thermal pipeline. Patched shaders write valid auxiliary data and have full
 *       writes enabled. Unpatched shaders — entity shadows, the block-outline wireframe, glints, leashes,
 *       crumbling overlay, etc. — have writes to attachments 1-6 SUPPRESSED entirely. That's what stops them
 *       from blending zeros (or driver-undefined garbage) on top of the underlying terrain/entity's already-
 *       written mask byte and corrupting it. The auxiliary content the post shader reads at those pixels is
 *       whatever the underlying classified draw left there, which is exactly what we want.</li>
 * </ol>
 *
 * <p>Cache: vanilla calls {@code apply()} hundreds of times per frame, so the {@code glGetUniformLocation} lookup
 * is cached per program ID. Patched/unpatched lookup is name-based and cheap (a handful of string equals).
 */
@ApiStatus.Internal
public final class BLibGbufferUniforms {

    private static final int UNCACHED = Integer.MIN_VALUE;

    private static final ConcurrentMap<Integer, Integer> HELD_ITEM_LOC_CACHE = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Integer, Integer> BACKGROUND_ENTITY_LOC_CACHE = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Integer, Integer> BACKGROUND_ENTITY2_LOC_CACHE = new ConcurrentHashMap<>();

    /**
     * Tracks the last colorMask state we applied so we don't issue six glColorMaski calls per shader-bind when
     * the state is unchanged. State transitions are clustered (e.g. all terrain draws are patched, then a run of
     * unpatched line draws), so this collapses long runs into a single set.
     */
    private static boolean lastAuxWritesEnabled = true;

    private BLibGbufferUniforms() {
        throw new UnsupportedOperationException();
    }

    public static void apply(int programId, String shaderName) {
        if (BLibIrisCompat.isShaderModActive()) {
            return;
        }

        toggleAuxColorMask(shaderName);
        applyHeldItemUniform(programId);
        applyBackgroundEntityUniform(programId, shaderName);
        applyBackgroundEntity2Uniform(programId, shaderName);
    }

    /**
     * Resets the cached colorMask state. Call when the auxiliary attachments are (re)attached so the next
     * shader bind unconditionally re-issues the colorMask state — otherwise a stale "we already enabled writes"
     * record could skip the call after a framebuffer reattach left the GL state at the default (all enabled
     * but for a different framebuffer's draw buffers).
     */
    public static void resetColorMaskCache() {
        lastAuxWritesEnabled = true;
    }

    private static void toggleAuxColorMask(String shaderName) {
        if (!BLibMainTargetMRT.isAttached()) {
            return;
        }

        var patched = BLibEntityShaderPatcher.categoryFor(shaderName) != null;

        if (patched == lastAuxWritesEnabled) {
            return;
        }

        // Buffers 1-6 correspond to the six auxiliary color attachments of MainTarget. Setting all four
        // channel-mask bits at once gates whether fragment writes to those attachments take effect; attachment 0
        // is left untouched so the bound shader's color output always lands.
        for (int buf = 1; buf <= 6; buf++) {
            GL30.glColorMaski(buf, patched, patched, patched, patched);
        }

        lastAuxWritesEnabled = patched;
    }

    private static void applyHeldItemUniform(int programId) {
        var loc = HELD_ITEM_LOC_CACHE.computeIfAbsent(
            programId,
            p -> GL20.glGetUniformLocation(p, "BlibHeldItem")
        );

        if (loc == -1) {
            return;
        }

        GL20.glUniform1i(loc, BLibHeldItemRenderState.isActive() ? 1 : 0);
    }

    private static void applyBackgroundEntityUniform(int programId, String shaderName) {
        var loc = BACKGROUND_ENTITY_LOC_CACHE.computeIfAbsent(
            programId,
            p -> GL20.glGetUniformLocation(p, "BlibBackgroundEntity")
        );

        if (loc == -1) {
            return;
        }

        GL20.glUniform1i(loc, BLibBackgroundEntityRenderState.isActiveA() ? 1 : 0);
    }

    private static void applyBackgroundEntity2Uniform(int programId, String shaderName) {
        var loc = BACKGROUND_ENTITY2_LOC_CACHE.computeIfAbsent(
            programId,
            p -> GL20.glGetUniformLocation(p, "BlibBackgroundEntity2")
        );

        if (loc == -1) {
            return;
        }

        GL20.glUniform1i(loc, BLibBackgroundEntityRenderState.isActiveB() ? 1 : 0);
    }
}
