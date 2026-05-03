package com.blib.internal.client.animation.track.state;

/**
 * The four canonical animation track states. Used as a tag for legal-transition checks and as the
 * carrier of "from" / "to" / "current" state in {@link AzTransitionResult}.
 */
public enum AzAnimationStateKind {
    STOP,
    TRANSITION,
    PLAY,
    PAUSE
}
