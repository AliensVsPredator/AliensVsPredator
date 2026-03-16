package com.blib.internal.client.animation.track.state.impl;

import com.blib.internal.client.animation.track.state.AzAnimationState;
import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;

public final class AzAnimationStopState<T> extends AzAnimationState<T> {

    public AzAnimationStopState() {}

    @Override
    public void onUpdate(AzAnimationTrackStateMachine.Context<T> context) {
        // Stop state does not need to do anything.
    }
}
