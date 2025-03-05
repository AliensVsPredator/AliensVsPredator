package com.avp.core.common.entity.living.alien.parasite.facehugger;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class FacehuggerAnimationDispatcher {

    private static final AzCommand FLAIL = AzCommand.create(
        FacehuggerAnimationRefs.TAIL_CONTROLLER_NAME,
        FacehuggerAnimationRefs.TAIL_FLAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE = AzCommand.compose(
        AzCommand.create(
            FacehuggerAnimationRefs.LEGS_CONTROLLER_NAME,
            FacehuggerAnimationRefs.IDLE_ANIMATION_NAME,
            AzPlayBehaviors.LOOP
        ),
        AzCommand.create(
            FacehuggerAnimationRefs.TAIL_CONTROLLER_NAME,
            FacehuggerAnimationRefs.IDLE_ANIMATION_NAME,
            AzPlayBehaviors.LOOP
        )
    );

    private static final AzCommand LUNGE = AzCommand.compose(
        AzCommand.create(
            FacehuggerAnimationRefs.LEGS_CONTROLLER_NAME,
            FacehuggerAnimationRefs.LEAP_ANIMATION_NAME,
            AzPlayBehaviors.PLAY_ONCE
        ),
        AzCommand.create(
            FacehuggerAnimationRefs.TAIL_CONTROLLER_NAME,
            FacehuggerAnimationRefs.LEAP_ANIMATION_NAME,
            AzPlayBehaviors.PLAY_ONCE
        )
    );

    private static final AzCommand RUN = AzCommand.create(
        FacehuggerAnimationRefs.LEGS_CONTROLLER_NAME,
        FacehuggerAnimationRefs.RUN_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SACK = AzCommand.create(
        FacehuggerAnimationRefs.LUNGS_CONTROLLER_NAME,
        FacehuggerAnimationRefs.SACK_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SWAY = AzCommand.create(
        FacehuggerAnimationRefs.TAIL_CONTROLLER_NAME,
        FacehuggerAnimationRefs.TAIL_SWAY_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand HUG = AzCommand.compose(
        AzCommand.create(
            FacehuggerAnimationRefs.LEGS_CONTROLLER_NAME,
            FacehuggerAnimationRefs.FACEHUG_ANIMATION_NAME,
            AzPlayBehaviors.HOLD_ON_LAST_FRAME
        ),
        AzCommand.create(
            FacehuggerAnimationRefs.TAIL_CONTROLLER_NAME,
            FacehuggerAnimationRefs.FACEHUG_ANIMATION_NAME,
            AzPlayBehaviors.HOLD_ON_LAST_FRAME
        ),
        SACK
    );

    private static final AzCommand RUN_AND_FLAIL = AzCommand.compose(RUN, FLAIL);

    private static final AzCommand IDLE_AND_SWAY = AzCommand.compose(IDLE, SWAY);

    private final Facehugger facehugger;

    public FacehuggerAnimationDispatcher(Facehugger facehugger) {
        this.facehugger = facehugger;
    }

    public void hug() {
        HUG.sendForEntity(facehugger);
    }

    public void idle() {
        IDLE_AND_SWAY.sendForEntity(facehugger);
    }

    public void lunge() {
        LUNGE.sendForEntity(facehugger);
    }

    public void infertile() {
        IDLE.sendForEntity(facehugger);
    }

    public void run() {
        RUN_AND_FLAIL.sendForEntity(facehugger);
    }

    public void sack() {
        SACK.sendForEntity(facehugger);
    }

    public void tailFlail() {
        FLAIL.sendForEntity(facehugger);
    }

    public void tailSway() {
        SWAY.sendForEntity(facehugger);
    }
}
