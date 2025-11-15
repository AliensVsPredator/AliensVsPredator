package com.alien.client.animation.entity;

import com.alien.common.gameplay.entity.living.alien.predalien_adolescent.PredalienAdolescent;
import com.alien.common.util.AzAlienAnimationUtil;
import mod.azure.azurelib.common.animation.AzAnimatorConfig;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class PredalienAdolescentAnimator extends AzEntityAnimator<PredalienAdolescent> {

    private static final String NAME = "predalien_adolescent";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public PredalienAdolescentAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<PredalienAdolescent> animationControllerContainer) {
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
    public @NotNull ResourceLocation getAnimationLocation(PredalienAdolescent animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(PredalienAdolescent animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);
        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(PredalienAdolescent predalienAdolescent) {
        var dispatcher = predalienAdolescent.getAnimationDispatcher();
        var isMovingOnGround = predalienAdolescent.isMovingHorizontally.get() && predalienAdolescent.onGround();
        Runnable animFunction;

        if (predalienAdolescent.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (predalienAdolescent.hasTarget.get()) {
                animFunction = dispatcher::run;
            } else {
                animFunction = dispatcher::walk;
            }
        } else {
            animFunction = dispatcher::idle;
        }

        animFunction.run();
    }
}
