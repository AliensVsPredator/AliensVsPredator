package com.alien.common.gameplay.entity.living.alien.predalien_adolescent;

import com.alien.common.constant.animation.AdolescentAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.lib.common.util.AzAnimationUtil;
import mod.azure.azurelib.common.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.common.animation.play_behavior.AzPlayBehaviors;

public class PredalienAdolescentAnimationDispatcher {

    private static final AzCommand ARMATTACK_RIGHTARM = AzCommand.create(
        AzAlienAnimationUtil.RIGHT_ARM_CONTROLLER_NAME,
        AdolescentAnimationRefs.ATTACKCLAW_RIGHTARM_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand BITEATTACK_HEAD = AzCommand.create(
        AzAlienAnimationUtil.HEAD_CONTROLLER_NAME,
        AdolescentAnimationRefs.ATTACKBITE_HEAD_ANIMATION_NAME,
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

    private final PredalienAdolescent predalienAdolescent;

    public PredalienAdolescentAnimationDispatcher(PredalienAdolescent predalienAdolescent) {
        this.predalienAdolescent = predalienAdolescent;
    }

    public void crawl() {
        CRAWL_ALL.sendForEntity(predalienAdolescent);
    }

    public void crawlHold() {
        CRAWL_ALL_HOLD.sendForEntity(predalienAdolescent);
    }

    public void idle() {
        IDLE_ALL.sendForEntity(predalienAdolescent);
    }

    public void lunge() {
        LUNGE_ALL.sendForEntity(predalienAdolescent);
    }

    public void run() {
        RUN_ALL.sendForEntity(predalienAdolescent);
    }

    public void swim() {
        SWIM_ALL.sendForEntity(predalienAdolescent);
    }

    public void walk() {
        WALK_ALL.sendForEntity(predalienAdolescent);
    }

    public void biteAttack() {
        BITEATTACK_HEAD.sendForEntity(predalienAdolescent);
    }

    public void rightClawAttack() {
        ARMATTACK_RIGHTARM.sendForEntity(predalienAdolescent);
    }
}
