package com.avp.fabric.client.animation.entity;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.fabric.client.animation.BasicAnimationUtils;
import com.avp.fabric.common.entity.machine.SentryTurret;

public class SentryTurretAnimator extends AzEntityAnimator<SentryTurret> {

    private static final ResourceLocation ANIMATIONS = AVPResources.blockAnimationLocation("sentry_turret");

    public SentryTurretAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<SentryTurret> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(SentryTurret animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(SentryTurret animatable, float partialTicks) {
        BasicAnimationUtils.applyHeadRotations(animatable, context(), partialTicks, "gRotationJoint", 0F);
    }
}
