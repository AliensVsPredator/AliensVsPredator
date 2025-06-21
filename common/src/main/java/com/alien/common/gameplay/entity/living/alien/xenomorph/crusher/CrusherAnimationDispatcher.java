package com.alien.common.gameplay.entity.living.alien.xenomorph.crusher;

import com.alien.common.constant.animation.CrusherAnimationRefs;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehavior;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

import java.util.List;
import java.util.Objects;

public class CrusherAnimationDispatcher {

    private static final List<String> LIMB_NAMES = List.of(
        CrusherAnimationRefs.BODY_CONTROLLER_NAME,
        CrusherAnimationRefs.HEAD_CONTROLLER_NAME,
        CrusherAnimationRefs.LEFT_ARM_CONTROLLER_NAME,
        CrusherAnimationRefs.LEFT_LEG_CONTROLLER_NAME,
        CrusherAnimationRefs.RIGHT_ARM_CONTROLLER_NAME,
        CrusherAnimationRefs.RIGHT_LEG_CONTROLLER_NAME,
        CrusherAnimationRefs.TAIL_CONTROLLER_NAME
    );

    private static final AzCommand BITEATTACK_HEAD = AzCommand.create(
        CrusherAnimationRefs.HEAD_CONTROLLER_NAME,
        CrusherAnimationRefs.BITEATTACK_HEAD_ANIMATION_NAME
    );

    private static final AzCommand IDLE_TAIL = AzCommand.create(
        CrusherAnimationRefs.TAIL_CONTROLLER_NAME,
        CrusherAnimationRefs.IDLE_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand RUN_TAIL_PLAY_ONCE = AzCommand.create(
        CrusherAnimationRefs.TAIL_CONTROLLER_NAME,
        CrusherAnimationRefs.RUN_TAIL_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    private static final AzCommand IDLE_ALL = compose("idle");

    private static final AzCommand LEAP_ALL = AzCommand.compose(
        compose(
            LIMB_NAMES.stream().filter(name -> !Objects.equals(name, "tail")).toList(),
            "leap",
            AzPlayBehaviors.PLAY_ONCE
        ),
        RUN_TAIL_PLAY_ONCE
    );

    private static final AzCommand SWIM_ALL = compose("swim");

    private static final AzCommand TAILATTACK_TAIL = AzCommand.create(
        CrusherAnimationRefs.TAIL_CONTROLLER_NAME,
        CrusherAnimationRefs.TAILATTACK_TAIL_ANIMATION_NAME
    );

    private static final AzCommand WALK_ALL = AzCommand.compose(
        compose(
            LIMB_NAMES.stream().filter(name -> !Objects.equals(name, "tail")).toList(),
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

    public void swim() {
        SWIM_ALL.sendForEntity(crusher);
    }

    public void tailAttack() {
        TAILATTACK_TAIL.sendForEntity(crusher);
    }

    public void walk() {
        WALK_ALL.sendForEntity(crusher);
    }

    private static AzCommand compose(String baseName) {
        return compose(baseName, AzPlayBehaviors.LOOP);
    }

    private static AzCommand compose(String baseName, AzPlayBehavior playBehavior) {
        return compose(LIMB_NAMES, baseName, playBehavior);
    }

    private static AzCommand compose(List<String> limbNames, String baseName, AzPlayBehavior playBehavior) {
        return AzCommand.compose(
            limbNames.stream()
                .map(limbName -> AzCommand.create(limbName, baseName + "." + limbName, playBehavior))
                .toList()
        );
    }
}
