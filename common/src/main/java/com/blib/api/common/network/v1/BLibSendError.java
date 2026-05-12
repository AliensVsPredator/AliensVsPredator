package com.blib.api.common.network.v1;

/**
 * Reasons a client-to-server packet send can fail at the BLib service layer, surfaced through the
 * {@link com.just.core.functional.result.Result} returned by
 * {@link com.blib.api.common.mod.v1.model.access.BLibNetworkAccess#sendToServer}.
 * <p>
 * Modelled as a sealed interface so new variants (e.g. {@code SendFailed(Throwable)}) can be added without breaking
 * existing pattern-match consumers. Each variant is a record so it composes nicely with switch expressions and
 * {@code instanceof} patterns.
 */
public sealed interface BLibSendError {

    /**
     * No active server connection at send time — e.g. the engine workspace is open from the main menu (menu-overlay
     * mode) and a panel tried to send a fetch packet before any world is loaded. Recovery: load a world / wait for the
     * player to enter one, then re-trigger the send.
     */
    record NotConnected() implements BLibSendError {

        public static final NotConnected INSTANCE = new NotConnected();
    }
}
