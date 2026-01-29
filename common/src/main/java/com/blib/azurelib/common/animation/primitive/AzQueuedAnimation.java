package com.blib.azurelib.common.animation.primitive;

import com.blib.azurelib.common.animation.play_behavior.AzPlayBehavior;

public record AzQueuedAnimation(
    AzBakedAnimation animation,
    AzPlayBehavior playBehavior
) {}
