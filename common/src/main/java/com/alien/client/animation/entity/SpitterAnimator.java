package com.alien.client.animation.entity;

import com.alien.common.constant.animation.SpitterAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.xenomorph.spitter.Spitter;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class SpitterAnimator extends AzEntityAnimator<Spitter> {

    private static final String NAME = "spitter";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public SpitterAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Spitter> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, SpitterAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Spitter animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Spitter animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        // FIXME:
        // runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Spitter spitter) {
        var dispatcher = spitter.getAnimationDispatcher();
        var isMovingOnGround = spitter.isMovingHorizontally.get() && spitter.onGround();
        var isCrawling = spitter.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (spitter.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (isCrawling) {
                animFunction = dispatcher::crawl;
            } else if (spitter.hasTarget.get()) {
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
