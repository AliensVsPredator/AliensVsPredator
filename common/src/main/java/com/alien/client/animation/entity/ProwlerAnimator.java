package com.alien.client.animation.entity;

import com.alien.common.constant.animation.ProwlerAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.xenomorph.prowler.Prowler;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class ProwlerAnimator extends AzEntityAnimator<Prowler> {

    private static final String NAME = "prowler";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public ProwlerAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Prowler> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, ProwlerAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Prowler animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Prowler animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Prowler prowler) {
        var dispatcher = prowler.getAnimationDispatcher();
        var movementAnalyzer = prowler.getMovementAnalyzer();
        var isMovingOnGround = movementAnalyzer.isMovingHorizontally() && prowler.onGround();
        var isCrawling = prowler.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (prowler.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            animFunction = isCrawling ? dispatcher::crawl : dispatcher::walk;
        } else {
            // TODO: idle crawl
            animFunction = isCrawling ? dispatcher::crawlHold : dispatcher::idle;
        }

        animFunction.run();
    }
}
