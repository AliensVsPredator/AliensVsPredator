package com.alien.common.gameplay.entity.living.alien.xenomorph.drone;

import com.alien.common.constant.animation.DroneAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.lib.common.util.AzAnimationUtil;
import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;

public class DroneAnimationDispatcher {

    private static final AzCommand ARMATTACK_RIGHTARM = AzCommand.create(
        AzAlienAnimationUtil.RIGHT_ARM_CONTROLLER_NAME,
        DroneAnimationRefs.ATTACKCLAW_RIGHTARM_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand BITEATTACK_HEAD = AzCommand.create(
        AzAlienAnimationUtil.HEAD_CONTROLLER_NAME,
        DroneAnimationRefs.ATTACKBITE_HEAD_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand TAILATTACKQUAD_TAIL = AzCommand.create(
        AzAlienAnimationUtil.TAIL_CONTROLLER_NAME,
        DroneAnimationRefs.ATTACKTAIL_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand CRAWL_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "crawl");

    private static final AzCommand CRAWL_ALL_HOLD = AzAnimationUtil.compose(
        AzAlienAnimationUtil.XENO_LIMB_NAMES,
        "crawl",
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand IDLE_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "idle");

    private static final AzCommand LUNGE_ALL = AzAnimationUtil.compose(
        AzAlienAnimationUtil.XENO_LIMB_NAMES,
        "lunge",
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand RUN_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "run");

    private static final AzCommand SWIM_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "swim");

    private static final AzCommand WALK_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "walk");

    private final Drone drone;

    public DroneAnimationDispatcher(Drone drone) {
        this.drone = drone;
    }

    public void crawl() {
        CRAWL_ALL.sendForEntity(drone);
    }

    public void crawlHold() {
        CRAWL_ALL_HOLD.sendForEntity(drone);
    }

    public void idle() {
        IDLE_ALL.sendForEntity(drone);
    }

    public void lunge() {
        LUNGE_ALL.sendForEntity(drone);
    }

    public void run() {
        RUN_ALL.sendForEntity(drone);
    }

    public void swim() {
        SWIM_ALL.sendForEntity(drone);
    }

    public void walk() {
        WALK_ALL.sendForEntity(drone);
    }

    public void biteAttack() {
        BITEATTACK_HEAD.sendForEntity(drone);
    }

    public void rightClawAttack() {
        ARMATTACK_RIGHTARM.sendForEntity(drone);
    }

    public void tailAttack() {
        TAILATTACKQUAD_TAIL.sendForEntity(drone);
    }
}
