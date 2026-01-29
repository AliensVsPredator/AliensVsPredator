package com.blib.azurelib.common.animation.controller.state.impl;

import com.blib.azurelib.common.animation.controller.state.AzAnimationState;
import com.blib.azurelib.common.animation.controller.state.machine.AzAnimationControllerStateMachine;

public final class AzAnimationStopState<T> extends AzAnimationState<T> {

    public AzAnimationStopState() {}

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        // Stop state does not need to do anything.
    }
}
