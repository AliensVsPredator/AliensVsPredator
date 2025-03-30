package com.avp.common.block.entity;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class ResonatorAnimDispatcher {

    private static final AzCommand IDLE_COMMAND = AzCommand.create(
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

    private static final AzCommand POWERED_COMMAND = AzCommand.compose(IDLE_COMMAND, SPINNING_COMMAND);

    public ResonatorAnimDispatcher() {}

    public void unpowered(ResonatorBE entity) {
        UNPOWERED_COMMAND.sendForBlockEntity(entity);
    }

    public void powered(ResonatorBE entity) {
        POWERED_COMMAND.sendForBlockEntity(entity);
    }
}
