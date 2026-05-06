package com.blib.internal.client.posteffect;

import org.jetbrains.annotations.ApiStatus;

/**
 * Render-thread flag set by downstream mods around entity draws that should be rendered as if they were part of the
 * world background rather than as foreground entities. The patched fragment shaders read a {@code BlibBackgroundEntity}
 * uniform set per draw via {@link BLibGbufferUniforms} and write its 0/1 value into {@code entityMask.g}; consumer
 * post-effect shaders sample that channel to route the entity's pixels through the world (terrain) coloring formula
 * instead of the foreground-entity formula.
 * <p>
 * Use case: a vision post-effect that wants entities not in its visibility tag to still render but blend with the
 * world (dark blue thermal world / dark green EM world / etc.) rather than be culled entirely.
 * <p>
 * Single-threaded by design (vanilla render thread). Does not relate to {@code MobEffects.INVISIBILITY} — vanilla
 * still handles potion invisibility separately.
 */
@ApiStatus.Internal
public final class BLibBackgroundEntityRenderState {

    private static int depth;

    private BLibBackgroundEntityRenderState() {
        throw new UnsupportedOperationException();
    }

    /** Push: enter background-entity rendering scope. Re-entrant via depth count so nested calls compose correctly. */
    public static void push() {
        depth++;
    }

    /** Pop: leave background-entity rendering scope. Re-entrant via depth count. */
    public static void pop() {
        if (depth > 0) {
            depth--;
        }
    }

    public static boolean isActive() {
        return depth > 0;
    }
}
