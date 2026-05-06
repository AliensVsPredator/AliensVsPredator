package com.blib.api.client.posteffect.v1;

import com.blib.internal.client.posteffect.BLibBackgroundEntityRenderState;
import com.blib.internal.client.posteffect.BLibIrisCompat;

/**
 * Public utility entry-point for code that interacts with BLib's post-effect framework from outside BLib (e.g. a
 * downstream mod's mixins that need to no-op when an external shader-pack mod owns rendering, or that want to flag
 * entity draws as "render this as part of the world").
 * <p>
 * BLib itself disables its post-effect pipeline, MRT auxiliaries, and entity-shader patching whenever Iris/Oculus is
 * loaded — external mixins that participate in the post-effect data path (per-bone lighting pushes, render-type
 * substitutions, etc.) should follow the same gate so their writes don't fight the shader-pack pipeline.
 */
public final class BLibPostEffectFramework {

    private BLibPostEffectFramework() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return {@code true} if a third-party shader-pack mod (Iris on Fabric, Oculus on NeoForge) is loaded. The result
     *         is cached after the first call.
     */
    public static boolean isShaderModActive() {
        return BLibIrisCompat.isShaderModActive();
    }

    /**
     * Marks the start of a "background entity" rendering scope. Patched entity fragment shaders write {@code 1.0}
     * into {@code entityMask.g} for any draws that happen while this scope is active; consumer post-effect shaders
     * sample that channel to render those pixels with the world (terrain) coloring formula instead of the
     * foreground-entity formula.
     * <p>
     * Re-entrant via depth count. Pair every {@link #pushBackgroundEntity()} with exactly one
     * {@link #popBackgroundEntity()} on the render thread. Unrelated to {@code MobEffects.INVISIBILITY}.
     */
    public static void pushBackgroundEntity() {
        BLibBackgroundEntityRenderState.push();
    }

    /** Marks the end of a {@link #pushBackgroundEntity()} scope. Safe to call without a matching push (no-ops). */
    public static void popBackgroundEntity() {
        BLibBackgroundEntityRenderState.pop();
    }
}
