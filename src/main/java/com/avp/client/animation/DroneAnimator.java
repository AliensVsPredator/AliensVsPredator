package com.avp.client.animation;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.entity.living.alien.xenomorph.drone.Drone;
import com.avp.common.entity.living.alien.xenomorph.drone.DroneAnimationRefs;

public class DroneAnimator extends AzEntityAnimator<Drone> {

    private static final String NAME = "drone";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public DroneAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Drone> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Drone animatable) {
        return ANIMATION;
    }

}
