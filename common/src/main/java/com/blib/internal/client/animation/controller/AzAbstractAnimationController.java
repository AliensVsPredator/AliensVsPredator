package com.blib.internal.client.animation.controller;

import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequence;
import com.blib.internal.client.animation.dispatch.AzDispatchSide;

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
