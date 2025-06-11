package com.alien.common.gameplay.entity.living.alien.xenomorph.prowler;

import com.alien.common.constant.animation.RunnerAnimationRefs;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class ProwlerAnimationDispatcher {

    private static final AzCommand ATTACK_CLAW = AzCommand.create(
        RunnerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        RunnerAnimationRefs.ATTACK_CLAW_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_TAIL = AzCommand.create(
        RunnerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        RunnerAnimationRefs.ATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand CRAWL = AzCommand.create(
        RunnerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        RunnerAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand CRAWL_HOLD = AzCommand.create(
        RunnerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        RunnerAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand IDLE = AzCommand.create(
        RunnerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        RunnerAnimationRefs.IDLE_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand LUNGE = AzCommand.create(
        RunnerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        RunnerAnimationRefs.LUNGE_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand RUN = AzCommand.create(
        RunnerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        RunnerAnimationRefs.RUN_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SWIM = AzCommand.create(
        RunnerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        RunnerAnimationRefs.SWIM_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK = AzCommand.create(
        RunnerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        RunnerAnimationRefs.WALK_ANIMATION_NAME,
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
