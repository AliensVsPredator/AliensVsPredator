package com.alien.common.gameplay.entity.living.alien.xenomorph.crusher;

import com.alien.common.constant.animation.CrusherAnimationRefs;
import com.alien.common.constant.animation.DroneAnimationRefs;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class CrusherAnimationDispatcher {

    private static final AzCommand ATTACK_BITE = AzCommand.create(
        CrusherAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        CrusherAnimationRefs.ATTACK_BITE_ANIMATION_NAME
    );

    private static final AzCommand ATTACK_TAIL = AzCommand.create(
        CrusherAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        CrusherAnimationRefs.ATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand CRAWL = AzCommand.create(
        CrusherAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        CrusherAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand CRAWL_HOLD = AzCommand.create(
        CrusherAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        CrusherAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand FLAIL_TAIL = AzCommand.create(
        CrusherAnimationRefs.TAIL_CONTROLLER_NAME,
        CrusherAnimationRefs.FLAIL_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE_BODY = AzCommand.create(
        CrusherAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        CrusherAnimationRefs.IDLE_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand IDLE_TAIL = AzCommand.create(
        CrusherAnimationRefs.TAIL_CONTROLLER_NAME,
        CrusherAnimationRefs.IDLE_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand LUNGE = AzCommand.create(
        DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        DroneAnimationRefs.LUNGE_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand RUN_BODY = AzCommand.create(
        CrusherAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        CrusherAnimationRefs.RUN_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SWIM = AzCommand.create(
        CrusherAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        CrusherAnimationRefs.SWIM_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK_BODY = AzCommand.create(
        CrusherAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        CrusherAnimationRefs.WALK_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    // Composed Animations

    private static final AzCommand IDLE = AzCommand.compose(IDLE_BODY, IDLE_TAIL);

    private static final AzCommand RUN = AzCommand.compose(RUN_BODY, FLAIL_TAIL);

    private static final AzCommand WALK = AzCommand.compose(WALK_BODY, IDLE_TAIL);

    private final Crusher crusher;

    public CrusherAnimationDispatcher(Crusher crusher) {
        this.crusher = crusher;
    }

    public void biteAttack() {
        ATTACK_BITE.sendForEntity(crusher);
    }

    public void crawl() {
        CRAWL.sendForEntity(crusher);
    }

    public void crawlHold() {
        CRAWL_HOLD.sendForEntity(crusher);
    }

    public void idle() {
        IDLE.sendForEntity(crusher);
    }

    public void lunge() {
        LUNGE.sendForEntity(crusher);
    }

    public void run() {
        RUN.sendForEntity(crusher);
    }

    public void swim() {
        SWIM.sendForEntity(crusher);
    }

    public void walk() {
        WALK.sendForEntity(crusher);
    }

    public void tailAttack() {
        ATTACK_TAIL.sendForEntity(crusher);
    }
}
