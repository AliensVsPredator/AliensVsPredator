package com.avp.core.client.animation;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;
import com.avp.core.common.entity.living.alien.xenomorph.warrior.Warrior;
import com.avp.core.common.entity.living.alien.xenomorph.warrior.WarriorAnimationRefs;

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

}
