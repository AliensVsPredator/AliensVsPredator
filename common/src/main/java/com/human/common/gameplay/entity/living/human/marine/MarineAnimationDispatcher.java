package com.human.common.gameplay.entity.living.human.marine;

import com.human.common.gameplay.entity.living.human.AbstractHuman;
import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;

public class MarineAnimationDispatcher {

    private static final AzCommand IDLE = AzCommand.create(
        MarineAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        MarineAnimationRefs.IDLE_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand RIGHT_SHOOT = AzCommand.create(
        MarineAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        MarineAnimationRefs.RIGHT_SHOOT_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand SWIM = AzCommand.create(
        MarineAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        MarineAnimationRefs.SWIM_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK = AzCommand.create(
        MarineAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        MarineAnimationRefs.WALK_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private final AbstractHuman abstractHuman;

    public MarineAnimationDispatcher(AbstractHuman abstractHuman) {
        this.abstractHuman = abstractHuman;
    }

    public void idle() {
        IDLE.sendForEntity(abstractHuman);
    }

    public void swim() {
        SWIM.sendForEntity(abstractHuman);
    }

    public void walk() {
        WALK.sendForEntity(abstractHuman);
    }

    public void rightShoot() {
        RIGHT_SHOOT.sendForEntity(abstractHuman);
    }
}
