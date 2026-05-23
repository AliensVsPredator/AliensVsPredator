package com.blib.api.client.animation.v1.command.play_behavior;

import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;

public abstract class AzPlayBehavior {

    private final String name;

    protected AzPlayBehavior(String name) {
        this.name = name;
    }

    public void onUpdate(AzAnimationTrackStateMachine.Context<?> context) {}

    public void onFinish(AzAnimationTrackStateMachine.Context<?> context) {}

    public String name() {
        return name;
    }
}
