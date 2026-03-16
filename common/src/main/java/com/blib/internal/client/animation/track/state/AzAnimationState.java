package com.blib.internal.client.animation.track.state;

import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;
import com.blib.internal.common.model.state_machine.State;

public abstract class AzAnimationState<T> implements State<AzAnimationTrackStateMachine.Context<T>> {

    private boolean isActive;

    protected AzAnimationState() {
        this.isActive = false;
    }

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
