package com.blib.engine.domain.selection.volume;

import org.jetbrains.annotations.ApiStatus;

/**
 * Domain wrapper for a server-pushed Move Blocks operation result. Replaces the prior pattern where
 * {@link BlockSelection} held an {@code S2CMoveSelectionResultPayload} directly — the domain layer shouldn't import
 * network DTOs, so the network listener constructs this record from the incoming payload.
 *
 * @param success    whether the server accepted the move and applied it
 * @param message    user-facing message (success summary or error reason); never {@code null}
 * @param blockCount number of blocks processed; used by the selection panel for status display
 */
@ApiStatus.Internal
public record PendingMoveResult(
    boolean success,
    String message,
    int blockCount
) {

    public PendingMoveResult {
        if (message == null)
            message = "";
    }
}
