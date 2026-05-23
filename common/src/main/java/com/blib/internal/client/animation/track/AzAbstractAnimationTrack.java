package com.blib.internal.client.animation.track;

import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequence;

// TODO: This will eventually be usable in common-side code once animations are moved from assets to data.
public class AzAbstractAnimationTrack {

    private final String name;

    protected AzAnimationSequence currentSequence;

    protected AzAbstractAnimationTrack(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public boolean hasAnimationFinished() {
        return currentSequence != null;
    }
}
