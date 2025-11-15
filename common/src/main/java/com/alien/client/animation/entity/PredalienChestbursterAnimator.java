package com.alien.client.animation.entity;

import com.alien.common.constant.animation.PredalienChestbursterAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.predalien_chestburster.PredalienChestburster;
import mod.azure.azurelib.common.animation.AzAnimatorConfig;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class PredalienChestbursterAnimator extends AzEntityAnimator<PredalienChestburster> {

    private static final String NAME = "predalien_chestburster";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public PredalienChestbursterAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<PredalienChestburster> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, PredalienChestbursterAnimationRefs.HEAD_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, PredalienChestbursterAnimationRefs.TAIL_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(PredalienChestburster animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(PredalienChestburster animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(PredalienChestburster chestburster) {
        var dispatcher = chestburster.getAnimationDispatcher();
        var isMovingOnGround = chestburster.isMovingHorizontally.get() && chestburster.onGround();
        Runnable animFunction;

        // if (isUnderWater()) {
        // // TODO: idle swim
        // animFunction = dispatcher::swim;
        // } else

        if (isMovingOnGround) {
            animFunction = dispatcher::slowSlither;
        } else {
            animFunction = dispatcher::idle;
        }

        animFunction.run();
    }
}
