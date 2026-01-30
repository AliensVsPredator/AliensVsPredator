package com.blib.internal.client.animation.controller;

import com.blib.api.client.animation.v1.animator.AzAnimationContext;
import com.blib.api.client.animation.v1.controller.AzAnimationController;
import com.blib.internal.client.animation.controller.state.machine.AzAnimationControllerStateMachine;

public class AzAnimationControllerTimer<T> {

    private final AzAnimationController<T> animationController;

    private double adjustedTick;

    private double tickOffset;

    public AzAnimationControllerTimer(AzAnimationController<T> animationController) {
        this.animationController = animationController;
    }

    public void update() {
        AzAnimationControllerStateMachine<?> stateMachine = animationController.stateMachine();
        AzAnimationContext<?> animContext = stateMachine.getContext().animationContext();
        double animationSpeed = animationController.animationProperties().animationSpeed();
        double tick = animContext.timer().getAnimTime();
        double tickStartOffset = animationController.animationProperties().startTickOffset();
        double freezeTick = animationController.animationProperties().freezeTickOffset();

        if (freezeTick > 0 && adjustedTick >= freezeTick) {
            adjustedTick = freezeTick;
            return;
        }

        adjustedTick = animationSpeed * Math.max((tick + tickStartOffset) - tickOffset, tickStartOffset);
    }

    public void reset() {
        AzAnimationControllerStateMachine<?> stateMachine = animationController.stateMachine();
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
