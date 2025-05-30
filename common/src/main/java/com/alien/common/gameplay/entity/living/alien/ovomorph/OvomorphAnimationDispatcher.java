package com.alien.common.gameplay.entity.living.alien.ovomorph;

import com.alien.common.constant.animation.OvomorphAnimationRefs;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class OvomorphAnimationDispatcher {

    private static final AzCommand CLOSE_HOLD = AzCommand.create(
        OvomorphAnimationRefs.BASE_CONTROLLER_NAME,
        OvomorphAnimationRefs.CLOSE_HOLD_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand OPEN = AzCommand.create(
        OvomorphAnimationRefs.BASE_CONTROLLER_NAME,
        OvomorphAnimationRefs.OPEN_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand OPEN_HOLD = AzCommand.create(
        OvomorphAnimationRefs.BASE_CONTROLLER_NAME,
        OvomorphAnimationRefs.OPEN_HOLD_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private final Ovomorph ovomorph;

    public OvomorphAnimationDispatcher(Ovomorph ovomorph) {
        this.ovomorph = ovomorph;
    }

    public void closeHold() {
        CLOSE_HOLD.sendForEntity(ovomorph);
    }

    public void open() {
        OPEN.sendForEntity(ovomorph);
    }

    public void openHold() {
        OPEN_HOLD.sendForEntity(ovomorph);
    }
}
