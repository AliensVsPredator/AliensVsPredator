package com.blib.internal.client.posteffect;

import org.jetbrains.annotations.ApiStatus;

/**
 * Render-thread flag set by downstream mods around entity draws that should be rendered as if they were part of the
 * world background rather than as foreground entities. The patched fragment shaders read uniforms set per draw via
 * {@link BLibGbufferUniforms} and pack them into {@code entityMask.g}; consumer post-effect shaders sample that channel
 * to route the entity's pixels through the world (terrain) coloring formula instead of the foreground-entity formula.
 * <p>
 * Two independent lanes (A and B) are exposed so consumers can encode per-side classifications during a transition
 * (e.g. predator vision's wipe between two vision modes — "background under the old mode" goes in one lane, "background
 * under the new mode" in the other). Each lane has its own depth count; their states are packed into {@code mask.g} as
 * {@code 0.25 * laneA + 0.5 * laneB} (so 0.0/0.25/0.5/0.75 cover the four combinations cleanly under NEAREST sampling
 * of the RG8 attachment). When only a single lane matters, callers can stick to lane A and ignore lane B — the encoding
 * still resolves to {@code 0.0} (visible) or {@code 0.25} (background) which the consumer shader can decode as its
 * legacy {@code mask.g >= 0.5 ? 1 : 0} flag would have, OR via the proper per-lane decode.
 * <p>
 * Use case: a vision post-effect that wants entities not in its visibility tag to still render but blend with the world
 * (dark blue thermal world / dark green EM world / etc.) rather than be culled entirely. Single-threaded by design
 * (vanilla render thread). Does not relate to {@code MobEffects.INVISIBILITY} — vanilla still handles potion
 * invisibility separately.
 */
@ApiStatus.Internal
public final class BLibBackgroundEntityRenderState {

    private static int depthA;

    private static int depthB;

    private BLibBackgroundEntityRenderState() {
        throw new UnsupportedOperationException();
    }

    /** Push lane A. Re-entrant via depth count so nested calls compose correctly. */
    public static void pushA() {
        depthA++;
    }

    /** Pop lane A. Safe to call without a matching push (no-ops). */
    public static void popA() {
        if (depthA > 0) {
            depthA--;
        }
    }

    /** Push lane B. Re-entrant via depth count. */
    public static void pushB() {
        depthB++;
    }

    /** Pop lane B. Safe to call without a matching push (no-ops). */
    public static void popB() {
        if (depthB > 0) {
            depthB--;
        }
    }

    public static boolean isActiveA() {
        return depthA > 0;
    }

    public static boolean isActiveB() {
        return depthB > 0;
    }
}
