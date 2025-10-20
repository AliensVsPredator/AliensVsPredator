package com.human.common.gameplay.entity.machine;

import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.world.entity.Entity;

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

    private final Entity entity;

    public SentryTurretAnimDispatcher(Entity entity) {
        this.entity = entity;
    }

    public void idle() {
        IDLE_COMMAND.sendForEntity(entity);
    }

    public void unpowered() {
        UNPOWERED_COMMAND.sendForEntity(entity);
    }

    public void firing() {
        FIRING_COMMAND.sendForEntity(entity);
    }

}
