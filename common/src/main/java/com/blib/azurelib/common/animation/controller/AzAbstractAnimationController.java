package com.blib.azurelib.common.animation.controller;

import com.blib.azurelib.common.animation.dispatch.AzDispatchSide;
import com.blib.azurelib.common.animation.dispatch.command.sequence.AzAnimationSequence;

// TODO: This will eventually be usable in common-side code once animations are moved from assets to data.
public class AzAbstractAnimationController {

    private final String name;

    protected AzAnimationSequence currentSequence;

    protected AzDispatchSide currentSequenceOrigin;

    protected AzAbstractAnimationController(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public boolean hasAnimationFinished() {
        return currentSequence != null;
    }
}
