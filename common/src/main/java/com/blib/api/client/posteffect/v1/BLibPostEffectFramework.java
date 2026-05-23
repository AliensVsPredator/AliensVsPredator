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
     * Marks the start of a "background entity" rendering scope on lane A. Patched entity fragment shaders pack the
     * lane-A and lane-B states into {@code entityMask.g} (see {@link BLibBackgroundEntityRenderState} for the
     * encoding); consumer post-effect shaders sample that channel to render those pixels with the world (terrain)
     * coloring formula instead of the foreground-entity formula.
     * <p>
     * Re-entrant via depth count. Pair every {@link #pushBackgroundEntity()} with exactly one
     * {@link #popBackgroundEntity()} on the render thread. Unrelated to {@code MobEffects.INVISIBILITY}.
     * <p>
     * Most consumers only need lane A; lane B exists for cases where two independent classifications need to coexist
     * per-pixel (e.g. a vision wipe whose two halves of the screen apply different visibility rules).
     */
    public static void pushBackgroundEntity() {
        BLibBackgroundEntityRenderState.pushA();
    }

    /** Marks the end of a {@link #pushBackgroundEntity()} scope. Safe to call without a matching push (no-ops). */
    public static void popBackgroundEntity() {
        BLibBackgroundEntityRenderState.popA();
    }

    /**
     * Marks the start of a "background entity" rendering scope on lane B. The second independent lane lets a consumer
     * encode two per-pixel classifications simultaneously — e.g. "background under oldMode" on lane A and "background
     * under newMode" on lane B during a vision transition wipe. The shader can then pick the right flag based on which
     * side of the wipe a given pixel is on.
     * <p>
     * Re-entrant via depth count. Pair every {@link #pushBackgroundEntityB()} with exactly one
     * {@link #popBackgroundEntityB()}.
     */
    public static void pushBackgroundEntityB() {
        BLibBackgroundEntityRenderState.pushB();
    }

    /** Marks the end of a {@link #pushBackgroundEntityB()} scope. Safe to call without a matching push (no-ops). */
    public static void popBackgroundEntityB() {
        BLibBackgroundEntityRenderState.popB();
    }
}
