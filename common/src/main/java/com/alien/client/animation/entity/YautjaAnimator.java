package com.alien.client.animation.entity;

import com.predator.common.gameplay.entity.living.yautja.Yautja;
import com.predator.common.gameplay.entity.living.yautja.YautjaAnimationRefs;
import mod.azure.azurelib.rewrite.animation.AzAnimationContext;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.client.animation.BasicAnimationUtils;

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
        showHelmet(animatable, context());
        showWristblades(animatable, context());
        BasicAnimationUtils.applyHeadRotations(animatable, context(), partialTick, "gNeckUpper", 0F);
        BasicAnimationUtils.applyLimbRotations(
            animatable,
            context(),
            partialTick,
            "gLeftArm",
            "gRightArm",
            "gLeftLeg",
            "gRightLeg",
            0F,
            0F,
            animatable.isAggressive()
        );
    }

    private static void showWristblades(Yautja entity, AzAnimationContext<?> context) {
        var bakedModel = context.boneCache().getBakedModel();
        var blade = bakedModel.getBoneOrNull("gWristBlade");

        if (blade != null) {
            blade.setHidden(!entity.getMainHandItem().isEmpty() && !entity.isAggressive());
        }
    }

    private static void showHelmet(Yautja entity, AzAnimationContext<?> context) {
        var bakedModel = context.boneCache().getBakedModel();
        var helmet = bakedModel.getBoneOrNull("gArmorMask");

        if (helmet != null) {
            helmet.setHidden(!entity.hasMask());
        }
    }
}
