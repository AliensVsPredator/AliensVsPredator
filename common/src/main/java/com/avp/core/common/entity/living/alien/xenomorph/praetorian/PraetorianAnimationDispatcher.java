package com.avp.core.common.entity.living.alien.xenomorph.praetorian;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class PraetorianAnimationDispatcher {

    private static final AzCommand ATTACK_CLAW = AzCommand.create(
        PraetorianAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        PraetorianAnimationRefs.ATTACK_CLAW_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_TAIL = AzCommand.create(
        PraetorianAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        PraetorianAnimationRefs.ATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand CRAWL = AzCommand.create(
        PraetorianAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        PraetorianAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand CRAWL_HOLD = AzCommand.create(
        PraetorianAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        PraetorianAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand FLAIL_TAIL = AzCommand.create(
        PraetorianAnimationRefs.TAIL_CONTROLLER_NAME,
        PraetorianAnimationRefs.FLAIL_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE_BODY = AzCommand.create(
        PraetorianAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        PraetorianAnimationRefs.IDLE_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE_TAIL = AzCommand.create(
        PraetorianAnimationRefs.TAIL_CONTROLLER_NAME,
        PraetorianAnimationRefs.IDLE_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand RUN_BODY = AzCommand.create(
        PraetorianAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        PraetorianAnimationRefs.RUN_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SWIM = AzCommand.create(
        PraetorianAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        PraetorianAnimationRefs.SWIM_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK_BODY = AzCommand.create(
        PraetorianAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        PraetorianAnimationRefs.WALK_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    // Composed Animations

    private static final AzCommand IDLE = AzCommand.compose(IDLE_BODY, IDLE_TAIL);

    private static final AzCommand RUN = AzCommand.compose(RUN_BODY, FLAIL_TAIL);

    private static final AzCommand WALK = AzCommand.compose(WALK_BODY, IDLE_TAIL);

    private final Praetorian praetorian;

    public PraetorianAnimationDispatcher(Praetorian praetorian) {
        this.praetorian = praetorian;
    }

    public void crawl() {
        CRAWL.sendForEntity(praetorian);
    }

    public void crawlHold() {
        CRAWL_HOLD.sendForEntity(praetorian);
    }

    public void idle() {
        IDLE.sendForEntity(praetorian);
    }

    public void run() {
        RUN.sendForEntity(praetorian);
    }

    public void swim() {
        SWIM.sendForEntity(praetorian);
    }

    public void walk() {
        WALK.sendForEntity(praetorian);
    }

    public void clawAttack() {
        ATTACK_CLAW.sendForEntity(praetorian);
    }

    public void tailAttack() {
        ATTACK_TAIL.sendForEntity(praetorian);
    }
}
