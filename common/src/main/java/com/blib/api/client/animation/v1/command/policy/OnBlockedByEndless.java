package com.blib.api.client.animation.v1.command.policy;

/**
 * Resolution policy for {@link AzDispatchMode#ENQUEUE} when the currently-playing animation will never finish (e.g. a
 * {@code LOOP}). An enqueued sequence behind an endless animation is unreachable, so the caller declares what they want
 * to happen.
 */
public enum OnBlockedByEndless {

    /**
     * Append the sequence anyway. The queued entry is unreachable until something stops the current animation, but no
     * warning is emitted.
     */
    APPEND_ANYWAY,

    /**
     * Reject the dispatch. Emits a warning and drops the action.
     */
    REJECT,

    /**
     * Give up on enqueueing and restart instead — equivalent to dispatching the sequence with
     * {@link AzDispatchMode#REPLAY}.
     */
    PROMOTE_TO_REPLAY
}
