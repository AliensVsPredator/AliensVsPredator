package com.alien.common.gameplay.entity.living.alien.xenomorph.warrior;

import com.alien.common.constant.animation.WarriorAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.lib.common.util.AzAnimationUtil;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class WarriorAnimationDispatcher {

    private static final AzCommand ARMATTACK_RIGHTARM = AzCommand.create(
        AzAlienAnimationUtil.RIGHT_ARM_CONTROLLER_NAME,
        WarriorAnimationRefs.ATTACKCLAW_RIGHTARM_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand BITEATTACK_HEAD = AzCommand.create(
        AzAlienAnimationUtil.HEAD_CONTROLLER_NAME,
        WarriorAnimationRefs.ATTACKBITE_HEAD_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand TAILATTACKQUAD_TAIL = AzCommand.create(
        AzAlienAnimationUtil.TAIL_CONTROLLER_NAME,
        WarriorAnimationRefs.ATTACKTAIL_TAIL_ANIMATION_NAME,
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

    private final Warrior warrior;

    public WarriorAnimationDispatcher(Warrior warrior) {
        this.warrior = warrior;
    }

    public void crawl() {
        CRAWL_ALL.sendForEntity(warrior);
    }

    public void crawlHold() {
        CRAWL_ALL_HOLD.sendForEntity(warrior);
    }

    public void idle() {
        IDLE_ALL.sendForEntity(warrior);
    }

    public void lunge() {
        LUNGE_ALL.sendForEntity(warrior);
    }

    public void run() {
        RUN_ALL.sendForEntity(warrior);
    }

    public void swim() {
        SWIM_ALL.sendForEntity(warrior);
    }

    public void walk() {
        WALK_ALL.sendForEntity(warrior);
    }

    public void biteAttack() {
        BITEATTACK_HEAD.sendForEntity(warrior);
    }

    public void rightClawAttack() {
        ARMATTACK_RIGHTARM.sendForEntity(warrior);
    }

    public void tailAttackQuad() {
        TAILATTACKQUAD_TAIL.sendForEntity(warrior);
    }
}
