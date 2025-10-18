package com.alien.client.animation.entity;

import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import com.alien.common.util.AzAlienAnimationUtil;
import mod.azure.azurelib.common.animation.AzAnimatorConfig;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class WarriorAnimator extends AzEntityAnimator<Warrior> {

    private static final String NAME = "warrior";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public WarriorAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Warrior> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, AzAlienAnimationUtil.BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.HEAD_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.LEFT_ARM_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.LEFT_LEG_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.RIGHT_ARM_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.RIGHT_LEG_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.TAIL_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Warrior animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Warrior animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Warrior warrior) {
        var dispatcher = warrior.getAnimationDispatcher();
        var isMovingOnGround = warrior.isMovingHorizontally.get() && warrior.onGround();
        var isCrawling = warrior.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (warrior.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (isCrawling) {
                animFunction = dispatcher::crawl;
            } else if (warrior.hasTarget.get()) {
                animFunction = dispatcher::run;
            } else {
                animFunction = dispatcher::walk;
            }
        } else {
            // TODO: idle crawl
            animFunction = isCrawling ? dispatcher::crawlHold : dispatcher::idle;
        }

        animFunction.run();
    }
}
