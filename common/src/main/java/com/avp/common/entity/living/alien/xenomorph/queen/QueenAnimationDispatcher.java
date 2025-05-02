package com.avp.common.entity.living.alien.xenomorph.queen;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class QueenAnimationDispatcher {

    private static final AzCommand ATTACK_CLAW = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.ATTACK_CLAW_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_CLAW_DOWN = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.ATTACK_CLAW_DOWN_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_TAIL = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.ATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_SCREAM = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.SCREAM_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_CHARGE = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.CHARGE_ANIMATION_NAME
    );

    private static final AzCommand FLAIL_TAIL = AzCommand.create(
        QueenAnimationRefs.TAIL_CONTROLLER_NAME,
        QueenAnimationRefs.FLAIL_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE_BODY = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.IDLE_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE_TAIL = AzCommand.create(
        QueenAnimationRefs.TAIL_CONTROLLER_NAME,
        QueenAnimationRefs.IDLE_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand RUN_BODY = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.RUN_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SWIM = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.SWIM_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK_BODY = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.WALK_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    // Composed Animations

    private static final AzCommand IDLE = AzCommand.compose(IDLE_BODY, IDLE_TAIL);

    private static final AzCommand RUN = AzCommand.compose(RUN_BODY, FLAIL_TAIL);

    private static final AzCommand WALK = AzCommand.compose(WALK_BODY, IDLE_TAIL);

    private final Queen queen;

    public QueenAnimationDispatcher(Queen queen) {
        this.queen = queen;
    }

    public void idle() {
        IDLE.sendForEntity(queen);
    }

    public void run() {
        RUN.sendForEntity(queen);
    }

    public void swim() {
        SWIM.sendForEntity(queen);
    }

    public void walk() {
        WALK.sendForEntity(queen);
    }

    public void clawAttack() {
        if (queen.getRandom().nextInt(1, 10) >= 8) {
            ATTACK_CLAW_DOWN.sendForEntity(queen);
        } else {
            ATTACK_CLAW.sendForEntity(queen);
        }
    }

    public void tailAttack() {
        ATTACK_TAIL.sendForEntity(queen);
    }

    public void screamAttack() {
        ATTACK_SCREAM.sendForEntity(queen);
    }

    public void chargeAttack() {
        ATTACK_CHARGE.sendForEntity(queen);
    }
}
