package com.avp.common.entity.living.yautja;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

import com.avp.common.entity.living.human.marine.MarineAnimationRefs;

public class YautjaAnimationDispatcher {

    private static final AzCommand RIGHT_SHOOT = AzCommand.create(
        MarineAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        MarineAnimationRefs.RIGHT_SHOOT_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private final Yautja yautja;

    public YautjaAnimationDispatcher(Yautja yautja) {
        this.yautja = yautja;
    }

    public void rightShoot() {
        RIGHT_SHOOT.sendForEntity(yautja);
    }
}
