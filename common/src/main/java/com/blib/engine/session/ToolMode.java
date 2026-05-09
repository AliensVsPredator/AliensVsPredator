package com.blib.engine.session;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.jigsaw.JigsawPieceSelection;

/**
 * High-level "what is the user doing right now" mode for the engine workspace. Read-only seed for a future tool system
 * — derived from {@link JigsawPieceSelection#hasSelection()}, so it always reflects whether the user has a piece on the
 * cursor. Status bars and cursor logic query this through {@link EngineSession#toolMode()} rather than poking at the
 * jigsaw selection directly.
 * <p>
 * When tool selection becomes explicit (terrain brush, gizmos, etc.), this enum grows new values and the derivation
 * gives way to a stored field on {@link EngineSession} with a setter.
 */
@ApiStatus.Internal
public enum ToolMode {

    SELECT,
    PLACE;

    public static ToolMode from(boolean hasSelection) {
        return hasSelection ? PLACE : SELECT;
    }
}
