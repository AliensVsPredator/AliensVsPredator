package com.blib.internal.client.animation.dispatch.command.stage;

import com.blib.internal.client.animation.property.AzAnimationStageProperties;

public record AzAnimationStage(
    String name,
    AzAnimationStageProperties properties
) {}
