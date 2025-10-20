package com.human.client.animation.entity;

import com.human.common.gameplay.entity.nuke.MushroomCloudEntity;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class MushroomCloudAnimator extends AzEntityAnimator<MushroomCloudEntity> {

    private static final ResourceLocation ANIMATIONS = AVPResources.entityAnimationLocation("mushroom_cloud");

    @Override
    public void registerControllers(AzAnimationControllerContainer<MushroomCloudEntity> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(MushroomCloudEntity animatable) {
        return ANIMATIONS;
    }
}
