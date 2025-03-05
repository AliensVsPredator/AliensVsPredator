package com.avp.core.common.entity.living.alien.xenomorph.warrior;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class WarriorAnimationDispatcher {

    private static final AzCommand ATTACK_CLAW = AzCommand.create(
        WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        WarriorAnimationRefs.ATTACK_CLAW_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_TAIL = AzCommand.create(
        WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        WarriorAnimationRefs.ATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand CRAWL = AzCommand.create(
        WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        WarriorAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand CRAWL_HOLD = AzCommand.create(
        WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        WarriorAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand IDLE = AzCommand.create(
        WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        WarriorAnimationRefs.IDLE_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand LUNGE = AzCommand.create(
        WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        WarriorAnimationRefs.LUNGE_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand RUN = AzCommand.create(
        WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        WarriorAnimationRefs.RUN_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SWIM = AzCommand.create(
        WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        WarriorAnimationRefs.SWIM_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK = AzCommand.create(
        WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        WarriorAnimationRefs.WALK_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private final Warrior warrior;

    public WarriorAnimationDispatcher(Warrior warrior) {
        this.warrior = warrior;
    }

    public void crawl() {
        CRAWL.sendForEntity(warrior);
    }

    public void crawlHold() {
        CRAWL_HOLD.sendForEntity(warrior);
    }

    public void idle() {
        IDLE.sendForEntity(warrior);
    }

    public void lunge() {
        LUNGE.sendForEntity(warrior);
    }

    public void run() {
        RUN.sendForEntity(warrior);
    }

    public void swim() {
        SWIM.sendForEntity(warrior);
    }

    public void walk() {
        WALK.sendForEntity(warrior);
    }

    public void clawAttack() {
        ATTACK_CLAW.sendForEntity(warrior);
    }

    public void tailAttack() {
        ATTACK_TAIL.sendForEntity(warrior);
    }
}
