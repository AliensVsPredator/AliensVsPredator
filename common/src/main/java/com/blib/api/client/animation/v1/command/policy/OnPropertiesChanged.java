package com.blib.api.client.animation.v1.command.policy;

/**
 * Resolution policy for {@link AzDispatchMode#PLAY_IF_NOT_PLAYING} when the dispatched sequence has the same animation
 * names as the active one but different per-stage properties (speed, easing, etc.).
 * <p>
 * Sequences are records with deep equality on their stages, so a property change makes the sequence "different" by
 * default. This enum lets callers opt into name-only matching when they want to retune a running animation rather than
 * reboot it.
 * </p>
 */
public enum OnPropertiesChanged {

    /**
     * Treat the dispatched sequence as a new sequence and restart from frame 0. This matches today's implicit behavior
     * (sequence equality includes properties).
     */
    RESTART,

    /**
     * Treat the dispatched sequence as the same sequence if the animation names match, ignoring per-stage property
     * differences. The track keeps playing without restart; any track-level setters in the same command (e.g.
     * {@code setSpeed}) still apply their changes.
     */
    UPDATE_IN_PLACE
}
