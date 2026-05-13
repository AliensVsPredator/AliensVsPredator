package com.blib.engine.domain.selection.volume;

import org.jetbrains.annotations.ApiStatus;

/**
 * Domain wrapper for a server-pushed capture operation result. Replaces the prior pattern where {@link BlockSelection}
 * held an {@code S2CProjectOpResultPayload} directly — the domain layer shouldn't import network DTOs, so the network
 * listener constructs this record from the incoming payload.
 *
 * @param success     whether the server accepted the capture and persisted it
 * @param projectName the project the capture was created in
 * @param message     user-facing message (success summary or error reason); never {@code null}
 */
@ApiStatus.Internal
public record PendingCaptureResult(
    boolean success,
    String projectName,
    String message
) {

    public PendingCaptureResult {
        if (projectName == null)
            projectName = "";
        if (message == null)
            message = "";
    }
}
