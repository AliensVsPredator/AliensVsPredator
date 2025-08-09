package com.human.client.animation.block;

import com.avp.AVPResources;
import com.human.common.gameplay.block.entity.power.impl.SolarPanelBlockEntity;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzBlockAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

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
