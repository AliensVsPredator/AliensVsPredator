package com.alien.common.gameplay.entity.living.alien.xenomorph.prowler;

import com.alien.common.constant.animation.ProwlerAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.lib.common.util.AzAnimationUtil;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class ProwlerAnimationDispatcher {

    private static final AzCommand CLAWATTACKQUAD_RIGHTARM = AzCommand.create(
        AzAlienAnimationUtil.RIGHT_ARM_CONTROLLER_NAME,
        ProwlerAnimationRefs.CLAWATTACKQUAD_RIGHTARM_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand BITEATTACK_HEAD = AzCommand.create(
        AzAlienAnimationUtil.HEAD_CONTROLLER_NAME,
        ProwlerAnimationRefs.BITEATTACK_HEAD_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand TAILATTACKQUAD_TAIL = AzCommand.create(
        AzAlienAnimationUtil.TAIL_CONTROLLER_NAME,
        ProwlerAnimationRefs.TAILATTACKQUAD_TAIL_ANIMATION_NAME,
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

    private final Prowler prowler;

    public ProwlerAnimationDispatcher(Prowler prowler) {
        this.prowler = prowler;
    }

    public void crawl() {
        CRAWL_ALL.sendForEntity(prowler);
    }

    public void crawlHold() {
        CRAWL_ALL_HOLD.sendForEntity(prowler);
    }

    public void idle() {
        IDLE_ALL.sendForEntity(prowler);
    }

    public void lunge() {
        LUNGE_ALL.sendForEntity(prowler);
    }

    public void run() {
        RUN_ALL.sendForEntity(prowler);
    }

    public void swim() {
        SWIM_ALL.sendForEntity(prowler);
    }

    public void walk() {
        WALK_ALL.sendForEntity(prowler);
    }

    public void biteAttack() {
        BITEATTACK_HEAD.sendForEntity(prowler);
    }

    public void rightClawAttack() {
        CLAWATTACKQUAD_RIGHTARM.sendForEntity(prowler);
    }

    public void tailAttackQuad() {
        TAILATTACKQUAD_TAIL.sendForEntity(prowler);
    }
}
