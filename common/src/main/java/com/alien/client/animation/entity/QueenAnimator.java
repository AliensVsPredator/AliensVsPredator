package com.alien.client.animation.entity;

import com.alien.common.constant.animation.QueenAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class QueenAnimator extends AzEntityAnimator<Queen> {

    private static final String NAME = "queen";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public QueenAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Queen> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, QueenAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, QueenAnimationRefs.TAIL_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Queen animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Queen animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        runPassiveAnimations(animatable);

        var bakedModel = context().boneCache().getBakedModel();
        var eggSack = bakedModel.getBoneOrNull("root2");

        if (eggSack != null) {
            eggSack.setHidden(true);
        }
    }

    private void runPassiveAnimations(Queen queen) {
        var dispatcher = queen.getAnimationDispatcher();
        var isMovingOnGround = queen.isMovingHorizontally.get() && queen.onGround();
        Runnable animFunction;

        if (queen.getOvipositorManager().hasOvipositor()) {
            animFunction = dispatcher::sitOnOvipositor;
        } else if (queen.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (queen.hasTarget.get()) {
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
