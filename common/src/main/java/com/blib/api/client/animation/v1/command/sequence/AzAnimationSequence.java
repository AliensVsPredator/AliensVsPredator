package com.blib.api.client.animation.v1.command.sequence;

import java.util.List;

import com.blib.internal.client.animation.dispatch.command.stage.AzAnimationStage;

public record AzAnimationSequence(
    List<AzAnimationStage> stages
) {}
