package com.human.client.animation.entity;

import com.human.common.gameplay.entity.machine.SentryTurret;
import mod.azure.azurelib.common.animation.AzAnimatorConfig;
import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.client.animation.BasicAnimationUtils;

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
