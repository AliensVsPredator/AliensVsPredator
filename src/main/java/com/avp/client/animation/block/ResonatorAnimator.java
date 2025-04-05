package com.avp.client.animation.block;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzBlockAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.block.entity.ResonatorBE;

public class ResonatorAnimator extends AzBlockAnimator<ResonatorBE> {

    private static final ResourceLocation ANIMATIONS = AVPResources.blockAnimationLocation("resonator");

    public ResonatorAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<ResonatorBE> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(ResonatorBE animatable) {
        return ANIMATIONS;
    }
}
