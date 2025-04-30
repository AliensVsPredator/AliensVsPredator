package com.avp.fabric.client.animation.block;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzBlockAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.common.AVPResources;
import com.avp.fabric.common.block.entity.ResonatorBlockEntity;

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
