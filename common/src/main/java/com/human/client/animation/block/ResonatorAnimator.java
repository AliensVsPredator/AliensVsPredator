package com.human.client.animation.block;

import com.human.common.gameplay.block.entity.ResonatorBlockEntity;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzBlockAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class ResonatorAnimator extends AzBlockAnimator<ResonatorBlockEntity> {

    private static final ResourceLocation ANIMATIONS = AVPResources.blockAnimationLocation("resonator");

    public ResonatorAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<ResonatorBlockEntity> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(ResonatorBlockEntity animatable) {
        return ANIMATIONS;
    }
}
