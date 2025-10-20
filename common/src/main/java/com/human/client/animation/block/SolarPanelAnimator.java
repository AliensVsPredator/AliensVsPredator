package com.human.client.animation.block;

import com.human.common.gameplay.block.entity.power.impl.SolarPanelBlockEntity;
import mod.azure.azurelib.common.animation.AzAnimatorConfig;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzBlockAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class SolarPanelAnimator extends AzBlockAnimator<SolarPanelBlockEntity> {

    private static final ResourceLocation ANIMATIONS = AVPResources.blockAnimationLocation("solar_panel");

    public SolarPanelAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<SolarPanelBlockEntity> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(SolarPanelBlockEntity animatable) {
        return ANIMATIONS;
    }
}
