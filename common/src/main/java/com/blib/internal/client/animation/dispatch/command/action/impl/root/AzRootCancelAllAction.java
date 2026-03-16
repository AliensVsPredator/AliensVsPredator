package com.blib.internal.client.animation.dispatch.command.action.impl.root;

import com.blib.api.client.animation.v1.animator.AzAnimator;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public class AzRootCancelAllAction implements AzAction {

    public static final AzRootCancelAllAction INSTANCE = new AzRootCancelAllAction();

    private AzRootCancelAllAction() {}

    @Override
    public void handle(AzAnimator<?, ?> animator) {
        var trackContainer = animator.getAnimationTrackContainer();
        var tracks = trackContainer.getAll();

        tracks.forEach(track -> track.setCurrentAnimation(null));
    }
}
