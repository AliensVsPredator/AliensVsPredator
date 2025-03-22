package com.avp.common.block.entity;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SentryTurretAnimDispatcher {

    private static final AzCommand IDLE_COMMAND = AzCommand.create(
            "base_controller",
            "animation.idle",
            AzPlayBehaviors.LOOP
    );

    private static final AzCommand UNPOWERED_COMMAND = AzCommand.create(
            "base_controller",
            "animation.unpowered",
            AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand FIRING_COMMAND = AzCommand.create(
            "base_controller",
            "animation.firing",
            AzPlayBehaviors.LOOP
    );

    public void idle(BlockEntity entity) {
        IDLE_COMMAND.sendForBlockEntity(entity);
    }

    public void unpowered(BlockEntity entity) {
        UNPOWERED_COMMAND.sendForBlockEntity(entity);
    }

    public void firing(BlockEntity entity) {
        FIRING_COMMAND.sendForBlockEntity(entity);
    }

}
