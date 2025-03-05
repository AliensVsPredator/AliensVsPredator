package com.avp.core.client.animation;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;
import com.avp.core.common.entity.living.alien.chestburster.Chestburster;
import com.avp.core.common.entity.living.alien.chestburster.ChestbursterAnimationRefs;

public class ChestbursterAnimator extends AzEntityAnimator<Chestburster> {

    private static final String NAME = "chestburster";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public ChestbursterAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Chestburster> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, ChestbursterAnimationRefs.HEAD_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, ChestbursterAnimationRefs.TAIL_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Chestburster animatable) {
        return ANIMATION;
    }

}
