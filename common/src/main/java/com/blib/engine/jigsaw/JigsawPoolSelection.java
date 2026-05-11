package com.blib.engine.jigsaw;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Mutable singleton holding a one-shot request to switch the {@code PoolEditorPanel}'s active pool. When non-null, the
 * editor's next {@code render()} consumes the request, updates its internal {@code SearchableSelect}, and clears this
 * field. Modeled on {@link JigsawPieceSelection}'s static-singleton style so both pieces of cross-panel state share the
 * same shape.
 * <p>
 * The request is consumed synchronously the next time the editor renders — there's no queue. If two callers stack
 * requests in the same frame, the later one wins, matching the user-intuition that "open this pool" is a final action,
 * not an ordered list. If the pool editor isn't currently visible (e.g. user closed the tab), the request lingers until
 * the user opens the editor via the Window menu, at which point its first render consumes the latched value.
 * <p>
 * Cleared on engine workspace close so a stale request doesn't bleed into a later session.
 */
@ApiStatus.Internal
public final class JigsawPoolSelection {

    private static @Nullable ResourceLocation requested;

    private JigsawPoolSelection() {}

    public static @Nullable ResourceLocation requested() {
        return requested;
    }

    public static void request(ResourceLocation poolId) {
        requested = poolId;
    }

    public static void clear() {
        requested = null;
    }
}
