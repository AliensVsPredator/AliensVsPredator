package com.alien.common.gameplay.entity.living.alien.xenomorph.runner;

import com.alien.common.constant.animation.RunnerAnimationRefs;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class RunnerAnimationDispatcher {

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

    private final Runner runner;

    public RunnerAnimationDispatcher(Runner runner) {
        this.runner = runner;
    }

    public void crawl() {
        CRAWL.sendForEntity(runner);
    }

    public void crawlHold() {
        CRAWL_HOLD.sendForEntity(runner);
    }

    public void idle() {
        IDLE.sendForEntity(runner);
    }

    public void lunge() {
        LUNGE.sendForEntity(runner);
    }

    public void run() {
        RUN.sendForEntity(runner);
    }

    public void swim() {
        SWIM.sendForEntity(runner);
    }

    public void walk() {
        WALK.sendForEntity(runner);
    }

    public void clawAttack() {
        ATTACK_CLAW.sendForEntity(runner);
    }

    public void tailAttack() {
        ATTACK_TAIL.sendForEntity(runner);
    }
}
