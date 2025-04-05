package com.avp.common.block.entity;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class ResonatorAnimDispatcher {

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

    public ResonatorAnimDispatcher() {}

    public void unpowered(ResonatorBE entity) {
        UNPOWERED_COMMAND.sendForBlockEntity(entity);
    }

    public void powered(ResonatorBE entity) {
        SPINNING_COMMAND.sendForBlockEntity(entity);
    }

    public void powerUp(ResonatorBE entity) {
        POWER_UP_COMMAND.sendForBlockEntity(entity);
    }
}
