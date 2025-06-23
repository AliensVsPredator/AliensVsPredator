package com.alien.client.animation.entity;

import com.alien.common.constant.animation.BoilerAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.xenomorph.boiler.Boiler;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class BoilerAnimator extends AzEntityAnimator<Boiler> {

    private static final String NAME = "boiler";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public BoilerAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Boiler> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, BoilerAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Boiler animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Boiler animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Boiler boiler) {
        var dispatcher = boiler.getAnimationDispatcher();
        var isMovingOnGround = boiler.isMovingHorizontally.get() && boiler.onGround();
        var isCrawling = boiler.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (boiler.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (isCrawling) {
                animFunction = dispatcher::crawl;
            } else if (boiler.hasTarget.get()) {
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
