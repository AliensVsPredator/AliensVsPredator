package com.blib.internal.client.posteffect;

import org.jetbrains.annotations.ApiStatus;

/**
 * Render-thread flag set while a held-item draw (first-person hand or third-person {@code ItemInHandLayer}) is in
 * flight. The patched fragment shaders read a {@code BlibHeldItem} uniform, set per draw via
 * {@link BLibGbufferUniforms}, and write a special mask category {@code 0.875} for those fragments. The thermal post
 * shader treats that range as "passthrough" — outputs the original scene color rather than the thermal heat gradient —
 * so held items remain readable in IR mode regardless of what they're rendered against.
 * <p>
 * Single-threaded by design (vanilla render thread).
 */
@ApiStatus.Internal
public final class BLibHeldItemRenderState {

    private static int depth;

    private BLibHeldItemRenderState() {
        throw new UnsupportedOperationException();
    }

    /** Push: enter held-item rendering scope. Re-entrant via depth count so nested calls compose correctly. */
    public static void push() {
        depth++;
    }

    /** Pop: leave held-item rendering scope. Re-entrant via depth count. */
    public static void pop() {
        if (depth > 0) {
            depth--;
        }
    }

    public static boolean isActive() {
        return depth > 0;
    }
}
