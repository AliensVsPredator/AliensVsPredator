package com.alien.client.animation.entity;

import com.alien.common.constant.animation.FacehuggerAnimationRefs;
import com.alien.common.gameplay.entity.living.alien.parasite.facehugger.Facehugger;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class FacehuggerAnimator extends AzEntityAnimator<Facehugger> {

    private static final String NAME = "facehugger";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public FacehuggerAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Facehugger> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, FacehuggerAnimationRefs.LEGS_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, FacehuggerAnimationRefs.LUNGS_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, FacehuggerAnimationRefs.TAIL_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Facehugger animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Facehugger animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        runPassiveAnimations(animatable);
    }

    private void runPassiveAnimations(Facehugger facehugger) {
        var attachmentManager = facehugger.getAttachmentManager();
        var dispatcher = facehugger.getAnimationDispatcher();

        if ((!attachmentManager.isFertile() && !attachmentManager.isAttachedToHost()) || facehugger.isDeadOrDying()) {
            dispatcher.infertile();
            return;
        }

        if (attachmentManager.isAttachedToHost() && facehugger.isAlive()) {
            dispatcher.hug();
            return;
        }

        var isMovingOnGround = facehugger.isMovingHorizontally.get() && facehugger.onGround();

        if (facehugger.isUnderWater()) {
            // TODO: swim
        } else if (isMovingOnGround) {
            dispatcher.run();
        } else {
            dispatcher.idle();
        }
    }
}
