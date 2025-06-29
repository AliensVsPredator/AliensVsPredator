package com.alien.common.gameplay.entity.living.alien.xenomorph.boiler;

import com.alien.common.constant.animation.BoilerAnimationRefs;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;

public class BoilerAnimationDispatcher {

    private static final AzCommand CRAWL = AzCommand.create(
        BoilerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        BoilerAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand CRAWL_HOLD = AzCommand.create(
        BoilerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        BoilerAnimationRefs.CRAWL_ANIMATION_NAME,
        AzPlayBehaviors.HOLD_ON_LAST_FRAME
    );

    private static final AzCommand IDLE = AzCommand.create(
        BoilerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        BoilerAnimationRefs.IDLE_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand RUN = AzCommand.create(
        BoilerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        BoilerAnimationRefs.RUN_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand SWIM = AzCommand.create(
        BoilerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        BoilerAnimationRefs.SWIM_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private static final AzCommand WALK = AzCommand.create(
        BoilerAnimationRefs.FULL_BODY_CONTROLLER_NAME,
        BoilerAnimationRefs.WALK_ANIMATION_NAME,
        AzPlayBehaviors.LOOP
    );

    private final Boiler boiler;

    public BoilerAnimationDispatcher(Boiler boiler) {
        this.boiler = boiler;
    }

    public void crawl() {
        CRAWL.sendForEntity(boiler);
    }

    public void crawlHold() {
        CRAWL_HOLD.sendForEntity(boiler);
    }

    public void idle() {
        IDLE.sendForEntity(boiler);
    }

    public void run() {
        RUN.sendForEntity(boiler);
    }

    public void swim() {
        SWIM.sendForEntity(boiler);
    }

    public void walk() {
        WALK.sendForEntity(boiler);
    }

}
