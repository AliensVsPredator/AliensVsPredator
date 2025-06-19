package com.alien.client.animation.entity;

import com.alien.common.constant.animation.AdolescentAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.adolescent.Adolescent;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class AdolescentAnimator extends AzEntityAnimator<Adolescent> {

    private static final String NAME = "adolescent";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public AdolescentAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Adolescent> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, AdolescentAnimationRefs.HEAD_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AdolescentAnimationRefs.TAIL_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Adolescent animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Adolescent animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        // TODO: Re-add this once adolescent animations are done.
        // runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Adolescent adolescent) {
        var dispatcher = adolescent.getAnimationDispatcher();
        var movementAnalyzer = adolescent.getMovementAnalyzer();
        var isMovingOnGround = movementAnalyzer.isMovingHorizontally() && adolescent.onGround();
        Runnable animFunction;

        if (isMovingOnGround) {
            animFunction = dispatcher::slowSlither;
        } else {
            animFunction = dispatcher::idle;
        }

        animFunction.run();
    }
}
