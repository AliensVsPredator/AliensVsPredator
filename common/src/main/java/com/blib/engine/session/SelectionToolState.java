package com.blib.engine.session;

import org.jetbrains.annotations.ApiStatus;

/**
 * Mutable singleton holding the active {@link SelectionTool}. Mirrors the {@code JigsawPieceSelection} /
 * {@code EntitySpawnSelection} pattern — a tiny static facade so the toolbar segmented control, the Q/V hotkeys, and the
 * viewport click dispatcher all read/write through one place.
 * <p>
 * Defaults to {@link SelectionTool#INSPECT}. {@link com.blib.engine.session.EngineMode#exit} resets to the default so a
 * mode change doesn't leak into the next engine session.
 */
@ApiStatus.Internal
public final class SelectionToolState {

    private static SelectionTool tool = SelectionTool.INSPECT;

    private SelectionToolState() {}

    public static SelectionTool current() {
        return tool;
    }

    public static void set(SelectionTool newTool) {
        tool = newTool == null ? SelectionTool.INSPECT : newTool;
    }

    public static void reset() {
        tool = SelectionTool.INSPECT;
    }
}
