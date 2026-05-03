package com.blib.internal.client.animation.track.state;

/**
 * The outcome of an attempted state machine transition. Three variants:
 *
 * <ul>
 *   <li>{@link Applied} — the transition was legal and side-effects ran (onExit on the previous state,
 *       onEnter on the new state). For self-transitions in the legal set (e.g. TRANSITION → TRANSITION
 *       to re-target a transition), the kind is unchanged but the side-effects still ran.</li>
 *   <li>{@link AlreadyInState} — the requested target equals the current state and is not a legal
 *       self-transition. No side-effects ran. The caller's intent ("be in state X") is already
 *       satisfied.</li>
 *   <li>{@link Rejected} — the requested transition is not in the legal graph from the source state.
 *       No side-effects ran. The caller asked for something the state machine refuses.</li>
 * </ul>
 *
 * <p>Use {@link #isApplied()} for the boolean shortcut: true when the caller's target state is now
 * the current state (i.e., {@link Applied} or {@link AlreadyInState}).</p>
 */
public sealed interface AzTransitionResult {

    /**
     * True if the requested target state is now (or already was) the current state.
     */
    boolean isApplied();

    /**
     * The state the machine ended up in after this attempt.
     */
    AzAnimationStateKind currentState();

    record Applied(AzAnimationStateKind from, AzAnimationStateKind to) implements AzTransitionResult {

        @Override
        public boolean isApplied() {
            return true;
        }

        @Override
        public AzAnimationStateKind currentState() {
            return to;
        }
    }

    record AlreadyInState(AzAnimationStateKind state) implements AzTransitionResult {

        @Override
        public boolean isApplied() {
            return true;
        }

        @Override
        public AzAnimationStateKind currentState() {
            return state;
        }
    }

    record Rejected(AzAnimationStateKind from, AzAnimationStateKind requested) implements AzTransitionResult {

        @Override
        public boolean isApplied() {
            return false;
        }

        @Override
        public AzAnimationStateKind currentState() {
            return from;
        }
    }
}
