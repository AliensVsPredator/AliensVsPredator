package com.avp.client.animation.guns;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzItemAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.item.old_painless.OldPainlessAnimationRefs;

public class M88Mod4CombatPistolAnimator extends AzItemAnimator {

    private static final String NAME = "m88mod4_combat_pistol";

    private static final ResourceLocation ANIMATION = AVPResources.itemAnimationLocation(NAME);

    public M88Mod4CombatPistolAnimator() {
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
