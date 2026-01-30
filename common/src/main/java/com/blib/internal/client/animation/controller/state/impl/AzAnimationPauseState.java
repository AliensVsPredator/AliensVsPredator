package com.blib.internal.client.animation.controller.state.impl;

import com.blib.internal.client.animation.controller.state.machine.AzAnimationControllerStateMachine;

public final class AzAnimationPauseState<T> extends AzAnimationPlayState<T> {

    public AzAnimationPauseState() {}

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        // Do nothing, because the pause state shouldn't reset on enter.
    }

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        super.onUpdate(context);
        // Pause state does not need to do anything.
    }
}
