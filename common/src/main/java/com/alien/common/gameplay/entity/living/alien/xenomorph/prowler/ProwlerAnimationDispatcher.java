package com.alien.common.gameplay.entity.living.alien.xenomorph.prowler;

import com.alien.common.constant.animation.ProwlerAnimationRefs;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class ProwlerAnimationDispatcher {

    private static final AzCommand ATTACK_CLAW = AzCommand.create(
        ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        ProwlerAnimationRefs.ATTACK_CLAW_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_TAIL = AzCommand.create(
        ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        ProwlerAnimationRefs.ATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand CRAWL = AzCommand.create(
        ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        ProwlerAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand CRAWL_HOLD = AzCommand.create(
        ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        ProwlerAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand IDLE = AzCommand.create(
        ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        ProwlerAnimationRefs.IDLE_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand LUNGE = AzCommand.create(
        ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        ProwlerAnimationRefs.LUNGE_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand RUN = AzCommand.create(
        ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        ProwlerAnimationRefs.RUN_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SWIM = AzCommand.create(
        ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        ProwlerAnimationRefs.SWIM_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK = AzCommand.create(
        ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        ProwlerAnimationRefs.WALK_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private final Prowler prowler;

    public ProwlerAnimationDispatcher(Prowler prowler) {
        this.prowler = prowler;
    }

    public void crawl() {
        CRAWL.sendForEntity(prowler);
    }

    public void crawlHold() {
        CRAWL_HOLD.sendForEntity(prowler);
    }

    public void idle() {
        IDLE.sendForEntity(prowler);
    }

    public void lunge() {
        LUNGE.sendForEntity(prowler);
    }

    public void run() {
        RUN.sendForEntity(prowler);
    }

    public void swim() {
        SWIM.sendForEntity(prowler);
    }

    public void walk() {
        WALK.sendForEntity(prowler);
    }

    public void clawAttack() {
        ATTACK_CLAW.sendForEntity(prowler);
    }

    public void tailAttack() {
        ATTACK_TAIL.sendForEntity(prowler);
    }
}
