package com.alien.common.gameplay.entity.living.alien.xenomorph.spitter;

import com.alien.common.constant.animation.DroneAnimationRefs;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class SpitterAnimationDispatcher {

    private static final AzCommand ATTACK_CLAW = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.ATTACK_CLAW_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_TAIL = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.ATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand CRAWL = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand CRAWL_HOLD = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand IDLE = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.IDLE_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand LUNGE = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.LUNGE_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand RUN = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.RUN_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SWIM = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.SWIM_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.WALK_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private final Spitter spitter;

    public SpitterAnimationDispatcher(Spitter spitter) {
        this.spitter = spitter;
    }

    public void crawl() {
        CRAWL.sendForEntity(spitter);
    }

    public void crawlHold() {
        CRAWL_HOLD.sendForEntity(spitter);
    }

    public void idle() {
        IDLE.sendForEntity(spitter);
    }

    public void lunge() {
        LUNGE.sendForEntity(spitter);
    }

    public void run() {
        RUN.sendForEntity(spitter);
    }

    public void swim() {
        SWIM.sendForEntity(spitter);
    }

    public void walk() {
        WALK.sendForEntity(spitter);
    }

    public void clawAttack() {
        ATTACK_CLAW.sendForEntity(spitter);
    }

    public void tailAttack() {
        ATTACK_TAIL.sendForEntity(spitter);
    }
}
