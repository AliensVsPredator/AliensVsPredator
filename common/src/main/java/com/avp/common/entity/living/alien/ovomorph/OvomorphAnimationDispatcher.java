package com.avp.common.entity.living.alien.ovomorph;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class OvomorphAnimationDispatcher {

    private static final AzCommand OPEN = AzCommand.create(
        OvomorphAnimationRefs.BASE_CONTROLLER_NAME,
        OvomorphAnimationRefs.OPEN_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private final Ovomorph ovomorph;

    public OvomorphAnimationDispatcher(Ovomorph ovomorph) {
        this.ovomorph = ovomorph;
    }

    public void open() {
        OPEN.sendForEntity(ovomorph);
    }
}
