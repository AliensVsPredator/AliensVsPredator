package com.blib.internal.client.animation.track.state.impl;

import com.blib.internal.client.animation.track.state.AzAnimationState;
import com.blib.internal.client.animation.track.state.AzAnimationStateKind;
import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;

public final class AzAnimationStopState<T> extends AzAnimationState<T> {

    public AzAnimationStopState() {}

    @Override
    public AzAnimationStateKind kind() {
        return AzAnimationStateKind.STOP;
    }

    @Override
    public void onUpdate(AzAnimationTrackStateMachine.Context<T> context) {
        // Stop state does not need to do anything.
    }
}
