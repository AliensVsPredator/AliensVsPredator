package com.blib.internal.client.animation.track.state.impl;

import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;

public final class AzAnimationPauseState<T> extends AzAnimationPlayState<T> {

    public AzAnimationPauseState() {}

    @Override
    public void onEnter(AzAnimationTrackStateMachine.Context<T> context) {
        // Do nothing, because the pause state shouldn't reset on enter.
    }

    @Override
    public void onUpdate(AzAnimationTrackStateMachine.Context<T> context) {
        super.onUpdate(context);
        // Pause state does not need to do anything.
    }
}
