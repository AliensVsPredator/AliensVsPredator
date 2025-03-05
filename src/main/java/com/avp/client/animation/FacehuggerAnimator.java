package com.avp.client.animation;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.entity.living.alien.parasite.facehugger.Facehugger;
import com.avp.common.entity.living.alien.parasite.facehugger.FacehuggerAnimationRefs;

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

}
