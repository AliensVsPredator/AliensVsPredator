package com.alien.client.animation.entity;

import com.alien.common.constant.animation.WarriorAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
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
            AzAnimationController.builder(this, WarriorAnimationRefs.FULL_BODY_CONTROLLER_NAME)
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
        var isMovingOnGround = warrior.isMovingHorizontally() && warrior.onGround();
        var isCrawling = warrior.getCrawlingManager().isCrawling();
        Runnable animFunction;

        if (warrior.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (isCrawling) {
                animFunction = dispatcher::crawl;
            } else if (warrior.hasTarget()) {
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
