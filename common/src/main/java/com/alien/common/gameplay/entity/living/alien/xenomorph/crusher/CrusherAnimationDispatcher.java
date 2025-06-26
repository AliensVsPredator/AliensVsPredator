package com.alien.common.gameplay.entity.living.alien.xenomorph.crusher;

import com.alien.common.constant.animation.CrusherAnimationRefs;
import com.alien.common.util.AzAlienAnimationUtil;
import com.lib.common.util.AzAnimationUtil;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

import java.util.Objects;

public class CrusherAnimationDispatcher {

    private static final AzCommand BITEATTACK_HEAD = AzCommand.create(
        AzAlienAnimationUtil.HEAD_CONTROLLER_NAME,
        CrusherAnimationRefs.BITEATTACK_HEAD_ANIMATION_NAME
    );

    private static final AzCommand IDLE_TAIL = AzCommand.create(
        AzAlienAnimationUtil.TAIL_CONTROLLER_NAME,
        CrusherAnimationRefs.IDLE_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand RUN_TAIL_PLAY_ONCE = AzCommand.create(
        AzAlienAnimationUtil.TAIL_CONTROLLER_NAME,
        CrusherAnimationRefs.RUN_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand IDLE_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "idle");

    private static final AzCommand LEAP_ALL = AzCommand.compose(
        AzAnimationUtil.compose(
            AzAlienAnimationUtil.XENO_LIMB_NAMES.stream().filter(name -> !Objects.equals(name, "tail")).toList(),
            "leap",
            AzPlayBehaviors.PLAY_ONCE
        ),
        RUN_TAIL_PLAY_ONCE
    );

    private static final AzCommand RUN_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "run");

    private static final AzCommand SWIM_ALL = AzAnimationUtil.compose(AzAlienAnimationUtil.XENO_LIMB_NAMES, "swim");

    private static final AzCommand TAILATTACK_TAIL = AzCommand.create(
        AzAlienAnimationUtil.TAIL_CONTROLLER_NAME,
        CrusherAnimationRefs.TAILATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand WALK_ALL = AzCommand.compose(
        AzAnimationUtil.compose(
            AzAlienAnimationUtil.XENO_LIMB_NAMES.stream().filter(name -> !Objects.equals(name, "tail")).toList(),
            "walk",
            AzPlayBehaviors.LOOP
        ),
        IDLE_TAIL
    );

    private final Crusher crusher;

    public CrusherAnimationDispatcher(Crusher crusher) {
        this.crusher = crusher;
    }

    public void biteAttack() {
        BITEATTACK_HEAD.sendForEntity(crusher);
    }

    public void idle() {
        IDLE_ALL.sendForEntity(crusher);
    }

    public void lunge() {
        LEAP_ALL.sendForEntity(crusher);
    }

    public void run() {
        RUN_ALL.sendForEntity(crusher);
    }

    public void swim() {
        SWIM_ALL.sendForEntity(crusher);
    }

    public void tailAttack() {
        TAILATTACK_TAIL.sendForEntity(crusher);
    }

    public void walk() {
        WALK_ALL.sendForEntity(crusher);
    }
}
