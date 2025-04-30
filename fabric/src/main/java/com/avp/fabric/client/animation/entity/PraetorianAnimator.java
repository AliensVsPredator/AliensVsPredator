package com.avp.fabric.client.animation.entity;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.fabric.AVPResources;
import com.avp.fabric.common.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.avp.fabric.common.entity.living.alien.xenomorph.praetorian.PraetorianAnimationRefs;

public class PraetorianAnimator extends AzEntityAnimator<Praetorian> {

    private static final String NAME = "praetorian";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public PraetorianAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Praetorian> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, PraetorianAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, PraetorianAnimationRefs.TAIL_CONTROLLER_NAME)
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
        var movementAnalyzer = praetorian.getMovementAnalyzer();
        var isMovingOnGround = movementAnalyzer.isMovingHorizontally() && praetorian.onGround();
        var isCrawling = praetorian.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (praetorian.isUnderWater()) {
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
