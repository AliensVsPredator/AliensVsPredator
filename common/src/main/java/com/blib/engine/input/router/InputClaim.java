package com.blib.engine.input.router;

import org.jetbrains.annotations.ApiStatus;

/**
 * Result of an {@link InputHandler} processing an {@link InputEvent}. {@link #CLAIMED} stops the router immediately —
 * the event is not forwarded to lower-priority handlers. {@link #CONTINUE} lets the router keep walking the chain.
 * <p>
 * Two-value enum instead of {@code boolean} so the call sites read cleanly at the dispatcher (no double-negatives like
 * "didn't claim → continue") and so a future {@code CAPTURED} state (handler claims the next drag/release too) can be
 * added without breaking signatures.
 */
@ApiStatus.Internal
public enum InputClaim {

    CLAIMED,
    CONTINUE
}
