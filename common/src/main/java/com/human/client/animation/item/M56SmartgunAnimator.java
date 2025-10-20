package com.human.client.animation.item;

import com.human.common.gameplay.item.old_painless.OldPainlessAnimationRefs;
import mod.azure.azurelib.common.animation.AzAnimatorConfig;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzItemAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class M56SmartgunAnimator extends AzItemAnimator {

    private static final String NAME = "m56_smartgun";

    private static final ResourceLocation ANIMATION = AVPResources.itemAnimationLocation(NAME);

    public M56SmartgunAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<ItemStack> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME)
                .setTransitionLength(1)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(ItemStack animatable) {
        return ANIMATION;
    }
}
