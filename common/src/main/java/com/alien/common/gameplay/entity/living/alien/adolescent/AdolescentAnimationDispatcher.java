package com.alien.common.gameplay.entity.living.alien.adolescent;

import com.alien.common.constant.animation.AdolescentAnimationRefs;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class AdolescentAnimationDispatcher {

    private static final AzCommand IDLE_HEAD = AzCommand.create(
        AdolescentAnimationRefs.HEAD_CONTROLLER_NAME,
        AdolescentAnimationRefs.IDLE_HEAD_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE_TAIL = AzCommand.builder()
        .cancel(AdolescentAnimationRefs.TAIL_CONTROLLER_NAME)
        .build();

    private static final AzCommand SLITHER_TAIL = AzCommand.create(
        AdolescentAnimationRefs.TAIL_CONTROLLER_NAME,
        AdolescentAnimationRefs.SLITHER_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE = AzCommand.compose(IDLE_HEAD, IDLE_TAIL);

    private static final AzCommand SLOW_SLITHER = AzCommand.compose(IDLE_HEAD, SLITHER_TAIL);

    private final Adolescent adolescent;

    public AdolescentAnimationDispatcher(Adolescent adolescent) {
        this.adolescent = adolescent;
    }

    public void idle() {
        IDLE.sendForEntity(adolescent);
    }

    public void slowSlither() {
        SLOW_SLITHER.sendForEntity(adolescent);
    }
}
