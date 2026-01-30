package com.blib.internal.client.animation.controller.state;

import com.blib.internal.client.animation.controller.state.machine.AzAnimationControllerStateMachine;
import com.blib.internal.common.model.state_machine.State;

public abstract class AzAnimationState<T> implements State<AzAnimationControllerStateMachine.Context<T>> {

    private boolean isActive;

    protected AzAnimationState() {
        this.isActive = false;
    }

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        this.isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onExit(AzAnimationControllerStateMachine.Context<T> context) {
        this.isActive = false;
    }
}
