package com.avp.common.entity.living.alien.xenomorph.queen;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class QueenAnimationDispatcher {

    private static final AzCommand ATTACK_CLAW = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.ATTACK_CLAW_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_TAIL = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.ATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand CRAWL = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand CRAWL_HOLD = AzCommand.create(
        QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        QueenAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
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

    public void crawl() {
        CRAWL.sendForEntity(queen);
    }

    public void crawlHold() {
        CRAWL_HOLD.sendForEntity(queen);
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
        ATTACK_CLAW.sendForEntity(queen);
    }

    public void tailAttack() {
        ATTACK_TAIL.sendForEntity(queen);
    }
}
