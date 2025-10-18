package com.alien.client.animation.entity;

import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.alien.common.util.AzAlienAnimationUtil;
import mod.azure.azurelib.common.animation.AzAnimatorConfig;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class PraetorianAnimator extends AzEntityAnimator<Praetorian> {

    private static final String NAME = "praetorian";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public PraetorianAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Praetorian> animationControllerContainer) {
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
    public @NotNull ResourceLocation getAnimationLocation(Praetorian animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Praetorian animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Praetorian praetorian) {
        var dispatcher = praetorian.getAnimationDispatcher();
        var isMovingOnGround = praetorian.isMovingHorizontally.get() && praetorian.onGround();
        Runnable animFunction;

        if (praetorian.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (praetorian.hasTarget.get()) {
                animFunction = dispatcher::run;
            } else {
                animFunction = dispatcher::walk;
            }
        } else {
            // TODO: idle crawl
            animFunction = dispatcher::idle;
        }

        animFunction.run();
    }
}
