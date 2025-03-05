package com.avp.core.common.entity.living.alien.xenomorph.drone;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class DroneAnimationDispatcher {

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

    private final Drone drone;

    public DroneAnimationDispatcher(Drone drone) {
        this.drone = drone;
    }

    public void crawl() {
        CRAWL.sendForEntity(drone);
    }

    public void crawlHold() {
        CRAWL_HOLD.sendForEntity(drone);
    }

    public void idle() {
        IDLE.sendForEntity(drone);
    }

    public void lunge() {
        LUNGE.sendForEntity(drone);
    }

    public void run() {
        RUN.sendForEntity(drone);
    }

    public void swim() {
        SWIM.sendForEntity(drone);
    }

    public void walk() {
        WALK.sendForEntity(drone);
    }

    public void clawAttack() {
        ATTACK_CLAW.sendForEntity(drone);
    }

    public void tailAttack() {
        ATTACK_TAIL.sendForEntity(drone);
    }
}
