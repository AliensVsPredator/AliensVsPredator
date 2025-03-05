package com.avp.core.common.entity.living.alien.chestburster;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class ChestbursterAnimationDispatcher {

    private static final AzCommand IDLE_HEAD = AzCommand.create(
        ChestbursterAnimationRefs.HEAD_CONTROLLER_NAME,
        ChestbursterAnimationRefs.IDLE_HEAD_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE_TAIL = AzCommand.builder()
        .cancel(ChestbursterAnimationRefs.TAIL_CONTROLLER_NAME)
        .build();

    private static final AzCommand SLITHER_TAIL = AzCommand.create(
        ChestbursterAnimationRefs.TAIL_CONTROLLER_NAME,
        ChestbursterAnimationRefs.SLITHER_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE = AzCommand.compose(IDLE_HEAD, IDLE_TAIL);

    private static final AzCommand SLOW_SLITHER = AzCommand.compose(IDLE_HEAD, SLITHER_TAIL);

    private final Chestburster chestburster;

    public ChestbursterAnimationDispatcher(Chestburster chestburster) {
        this.chestburster = chestburster;
    }

    public void idle() {
        IDLE.sendForEntity(chestburster);
    }

    public void slowSlither() {
        SLOW_SLITHER.sendForEntity(chestburster);
    }
}
