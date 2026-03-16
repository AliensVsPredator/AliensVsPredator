package com.blib.internal.client.animation.dispatch.command.action;

import com.blib.api.client.animation.v1.animator.AzAnimator;

public interface AzAction {

    void handle(AzAnimator<?, ?> animator);
}
