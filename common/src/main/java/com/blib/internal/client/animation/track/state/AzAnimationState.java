package com.blib.internal.client.animation.track.state;

import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;
import com.blib.internal.common.model.state_machine.State;

public abstract class AzAnimationState<T> implements State<AzAnimationTrackStateMachine.Context<T>> {

    private boolean isActive;

    protected AzAnimationState() {
        this.isActive = false;
    }

    /**
     * The canonical kind of this state. Used by the state machine for legal-transition checks.
     */
    public abstract AzAnimationStateKind kind();

    @Override
    public void onEnter(AzAnimationTrackStateMachine.Context<T> context) {
        this.isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onExit(AzAnimationTrackStateMachine.Context<T> context) {
        this.isActive = false;
    }
}
