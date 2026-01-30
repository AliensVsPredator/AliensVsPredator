package com.blib.internal.client.animation.primitive;

import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehavior;

public record AzQueuedAnimation(
    AzBakedAnimation animation,
    AzPlayBehavior playBehavior
) {}
