package com.blib.engine.history.api;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

import com.blib.mod.common.network.packet.ActionDescriptor;

/**
 * Unified read-side contract for the editor's undo/redo histories. Two backends implement this: the server-mirrored
 * {@link com.blib.engine.history.ClientActionHistory} (fed by S2C sync packets) and the local
 * {@link com.blib.engine.modeler.history.ModelerActionHistory} (driven by client-only authoring gestures). UI panels
 * select which one to read by domain — the contract itself is identical.
 * <p>
 * The view-side methods are intentionally read-only; mutation lives on the concrete classes since the two backends are
 * driven from very different sources (network packet vs. local push).
 */
@ApiStatus.Internal
public interface HistoryService {

    /**
     * Concatenation of the undo stack (newest first) and the redo stack (oldest first). Indices {@code [0, undoCursor)}
     * are undoable; indices {@code [undoCursor, size())} are redoable.
     */
    List<ActionDescriptor> entries();

    int undoCursor();
}
