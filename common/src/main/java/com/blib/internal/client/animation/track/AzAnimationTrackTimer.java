package com.blib.internal.client.animation.track;

import com.blib.api.client.animation.v1.animator.AzAnimationContext;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.internal.client.animation.track.state.machine.AzAnimationTrackStateMachine;

public class AzAnimationTrackTimer<T> {

    private final AzAnimationTrack<T> animationTrack;

    private double adjustedTick;

    private double tickOffset;

    public AzAnimationTrackTimer(AzAnimationTrack<T> animationTrack) {
        this.animationTrack = animationTrack;
    }

    public void update() {
        AzAnimationTrackStateMachine<?> stateMachine = animationTrack.stateMachine();
        AzAnimationContext<?> animContext = stateMachine.getContext().animationContext();
        double animationSpeed = animationTrack.animationProperties().animationSpeed();
        double tick = animContext.timer().getAnimTime();
        double tickStartOffset = animationTrack.animationProperties().startTickOffset();
        double freezeTick = animationTrack.animationProperties().freezeTickOffset();

        if (freezeTick > 0 && adjustedTick >= freezeTick) {
            adjustedTick = freezeTick;
            return;
        }

        adjustedTick = animationSpeed * Math.max((tick + tickStartOffset) - tickOffset, tickStartOffset);
    }

    public void reset() {
        AzAnimationTrackStateMachine<?> stateMachine = animationTrack.stateMachine();
        AzAnimationContext<?> animContext = stateMachine.getContext().animationContext();
        this.tickOffset = animContext.timer().getAnimTime();
        this.adjustedTick = 0;
    }

    public double getAdjustedTick() {
        return adjustedTick;
    }

    public void addToAdjustedTick(double adjustedTick) {
        this.adjustedTick += adjustedTick;
    }
}
