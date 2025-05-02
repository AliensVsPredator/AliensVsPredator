package com.avp.client.animation.entity;

import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.entity.nuke.MushroomCloudEntity;

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
