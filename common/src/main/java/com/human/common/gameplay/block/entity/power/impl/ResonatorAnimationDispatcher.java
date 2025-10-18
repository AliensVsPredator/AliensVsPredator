package com.human.common.gameplay.block.entity.power.impl;

import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;

public class ResonatorAnimationDispatcher {

    private static final AzCommand POWER_UP_COMMAND = AzCommand.create(
        "base_controller",
        "animation.activate",
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand UNPOWERED_COMMAND = AzCommand.create(
        "base_controller",
        "animation.deactivate",
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand SPINNING_COMMAND = AzCommand.create(
        "base_controller",
        "animation.spinning",
        AzPlayBehaviors.LOOP
    );

    public ResonatorAnimationDispatcher() {}

    public void unpowered(ResonatorBlockEntity entity) {
        UNPOWERED_COMMAND.sendForBlockEntity(entity);
    }

    public void powered(ResonatorBlockEntity entity) {
        SPINNING_COMMAND.sendForBlockEntity(entity);
    }

    public void powerUp(ResonatorBlockEntity entity) {
        POWER_UP_COMMAND.sendForBlockEntity(entity);
    }
}
