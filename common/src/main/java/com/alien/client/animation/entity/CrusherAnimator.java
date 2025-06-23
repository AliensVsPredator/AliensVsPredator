package com.alien.client.animation.entity;

import com.alien.common.constant.animation.CrusherAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.xenomorph.crusher.Crusher;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class CrusherAnimator extends AzEntityAnimator<Crusher> {

    private static final String NAME = "crusher";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public CrusherAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Crusher> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, CrusherAnimationRefs.BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, CrusherAnimationRefs.HEAD_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, CrusherAnimationRefs.LEFT_ARM_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, CrusherAnimationRefs.LEFT_LEG_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, CrusherAnimationRefs.RIGHT_ARM_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, CrusherAnimationRefs.RIGHT_LEG_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, CrusherAnimationRefs.TAIL_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Crusher animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Crusher animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Crusher crusher) {
        var dispatcher = crusher.getAnimationDispatcher();
        var isMovingOnGround = crusher.isMovingHorizontally.get() && crusher.onGround();
        Runnable animFunction;

        // FIXME:
        if (crusher.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (crusher.hasTarget.get()) {
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
