package com.blib.api.client.animation.v1.command.policy;

/**
 * How a track should interpret a dispatched play command relative to whatever it is currently doing.
 * <p>
 * Today's legacy behavior (when no policy is set on a command) is behavior-tag-driven: same-sequence re-dispatch
 * restarts when the current animation is {@code PLAY_ONCE}, and no-ops for settled behaviors ({@code LOOP},
 * {@code HOLD_ON_LAST_FRAME}, {@code FREEZE_ON_FRAME}). The modes below replace that implicit policy with an explicit
 * one chosen at the command's construction.
 * </p>
 */
public enum AzDispatchMode {

    /**
     * Always restart from frame 0, regardless of what's currently playing or queued. Clears the queue.
     */
    REPLAY,

    /**
     * If the dispatched sequence is already the active one, no-op. Otherwise behave like {@link #REPLAY}. The "already
     * active" check is governed by {@link OnPropertiesChanged}.
     */
    PLAY_IF_NOT_PLAYING,

    /**
     * Append the dispatched sequence to the end of the queue without interrupting the current animation. If the track
     * is stopped or idle, start the sequence immediately. The behavior when the current animation is endless
     * ({@code LOOP}) is governed by {@link OnBlockedByEndless}.
     */
    ENQUEUE
}
