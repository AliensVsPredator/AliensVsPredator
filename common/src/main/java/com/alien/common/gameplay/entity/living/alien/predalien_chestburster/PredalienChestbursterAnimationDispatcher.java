package com.alien.common.gameplay.entity.living.alien.predalien_chestburster;

import com.alien.common.constant.animation.ChestbursterAnimationRefs;
import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;

public class PredalienChestbursterAnimationDispatcher {

    private static final AzCommand IDLE_HEAD = AzCommand.create(
        ChestbursterAnimationRefs.HEAD_CONTROLLER_NAME,
        ChestbursterAnimationRefs.IDLE_HEAD_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE_TAIL = AzCommand.controllerBuilder()
        .cancel(ChestbursterAnimationRefs.TAIL_CONTROLLER_NAME)
        .build();

    private static final AzCommand SLITHER_TAIL = AzCommand.create(
        ChestbursterAnimationRefs.TAIL_CONTROLLER_NAME,
        ChestbursterAnimationRefs.SLITHER_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE = AzCommand.compose(IDLE_HEAD, IDLE_TAIL);

    private static final AzCommand SLOW_SLITHER = AzCommand.compose(IDLE_HEAD, SLITHER_TAIL);

    private final PredalienChestburster predalienChestburster;

    public PredalienChestbursterAnimationDispatcher(PredalienChestburster predalienChestburster) {
        this.predalienChestburster = predalienChestburster;
    }

    public void idle() {
        IDLE.sendForEntity(predalienChestburster);
    }

    public void slowSlither() {
        SLOW_SLITHER.sendForEntity(predalienChestburster);
    }
}
