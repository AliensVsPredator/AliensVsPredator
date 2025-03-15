package com.avp.common.entity.living.human.marine;

import com.avp.common.entity.living.human.AbstractHumanMob;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

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

    private final AbstractHumanMob abstractHumanMob;

    public MarineAnimationDispatcher(AbstractHumanMob abstractHumanMob) {
        this.abstractHumanMob = abstractHumanMob;
    }

    public void idle() {
        IDLE.sendForEntity(abstractHumanMob);
    }

    public void swim() {
        SWIM.sendForEntity(abstractHumanMob);
    }

    public void walk() {
        WALK.sendForEntity(abstractHumanMob);
    }

    public void rightShoot() {
        RIGHT_SHOOT.sendForEntity(abstractHumanMob);
    }
}
