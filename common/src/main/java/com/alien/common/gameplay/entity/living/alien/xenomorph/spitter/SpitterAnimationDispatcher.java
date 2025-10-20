package com.alien.common.gameplay.entity.living.alien.xenomorph.spitter;

import com.alien.common.constant.animation.SpitterAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.lib.common.util.AzAnimationUtil;
import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;

public class SpitterAnimationDispatcher {

    private static final AzCommand ATTACKCLAW_RIGHTARM = AzCommand.create(
        AzAlienAnimationUtil.RIGHT_ARM_CONTROLLER_NAME,
        SpitterAnimationRefs.ATTACKCLAW_RIGHTARM_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand ATTACKCLAWQUAD_RIGHTARM = AzCommand.create(
        AzAlienAnimationUtil.RIGHT_ARM_CONTROLLER_NAME,
        SpitterAnimationRefs.ATTACKCLAWQUAD_RIGHTARM_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand BITEATTACK_HEAD = AzCommand.create(
        AzAlienAnimationUtil.HEAD_CONTROLLER_NAME,
        SpitterAnimationRefs.ATTACKBITE_HEAD_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand TAILATTACKQUAD_TAIL = AzCommand.create(
        AzAlienAnimationUtil.TAIL_CONTROLLER_NAME,
        SpitterAnimationRefs.ATTACKTAIL_TAIL_ANIMATION_NAME,
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

    private static final AzCommand RUN_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "sprint");

    private static final AzCommand SWIM_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "swim");

    private static final AzCommand WALK_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "walk");

    private final Spitter spitter;

    public SpitterAnimationDispatcher(Spitter spitter) {
        this.spitter = spitter;
    }

    public void crawl() {
        CRAWL_ALL.sendForEntity(spitter);
    }

    public void crawlHold() {
        CRAWL_ALL_HOLD.sendForEntity(spitter);
    }

    public void idle() {
        IDLE_ALL.sendForEntity(spitter);
    }

    public void lunge() {
        LUNGE_ALL.sendForEntity(spitter);
    }

    public void run() {
        RUN_ALL.sendForEntity(spitter);
    }

    public void swim() {
        SWIM_ALL.sendForEntity(spitter);
    }

    public void walk() {
        WALK_ALL.sendForEntity(spitter);
    }

    public void biteAttack() {
        BITEATTACK_HEAD.sendForEntity(spitter);
    }

    public void rightClawAttack() {
        ATTACKCLAW_RIGHTARM.sendForEntity(spitter);
    }

    public void rightClawAttackQuad() {
        ATTACKCLAWQUAD_RIGHTARM.sendForEntity(spitter);
    }

    public void tailAttack() {
        TAILATTACKQUAD_TAIL.sendForEntity(spitter);
    }
}
