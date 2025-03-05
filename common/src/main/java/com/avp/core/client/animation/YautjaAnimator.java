package com.avp.core.client.animation;

import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.core.AVPResources;
import com.avp.core.common.entity.living.yautja.Yautja;
import com.avp.core.common.entity.living.yautja.YautjaAnimationRefs;

public class YautjaAnimator extends AzEntityAnimator<Yautja> {

    private static final String NAME = "yautja";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public YautjaAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Yautja> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, YautjaAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Yautja animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Yautja animatable, float partialTick) {
        // TODO:
        // this.showHelmet(entity.hasHelmet.get());
        // this.showWristblades(entity.wristbladesVisible.get());

        BasicAnimationUtils.applyHeadRotations(animatable, context(), partialTick, "gNeckUpper", -0.2F);
        BasicAnimationUtils.applyLimbRotations(
            animatable,
            context(),
            partialTick,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            0F,
            0F
        );
    }
}
