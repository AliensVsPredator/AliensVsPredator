package com.human.client.animation.entity;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.living.human.marine.MarineAnimationRefs;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class MarineAnimator extends AzEntityAnimator<Marine> {

    private static final String NAME = "marine";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    @Override
    public void registerControllers(AzAnimationControllerContainer<Marine> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, MarineAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Marine animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Marine animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        var boneCache = this.context().boneCache();
        var leftArm = boneCache.getBakedModel().getBoneOrNull("gLeftArm");
        var rightArm = boneCache.getBakedModel().getBoneOrNull("gRightArm");
        var leftLeg = boneCache.getBakedModel().getBoneOrNull("gLeftLeg");
        var rightLeg = boneCache.getBakedModel().getBoneOrNull("gRightLeg");

        if (leftArm != null && !animatable.isAggressive()) {
            leftArm.setRotX(
                Mth.cos(
                    animatable.walkAnimation.position(
                        partialTicks
                    ) * 0.6662F
                ) * 1.0F * animatable.walkAnimation.speed() * 0.9F
            );
        }

        if (rightArm != null && !animatable.isAggressive()) {
            rightArm.setRotX(
                Mth.cos(
                    animatable.walkAnimation.position(
                        partialTicks
                    ) * 0.6662F + ((float) Math.PI)
                ) * 1.0F * animatable.walkAnimation.speed() * 0.9F
            );
        }

        if (leftLeg != null) {
            leftLeg.setRotX(
                Mth.cos(
                    animatable.walkAnimation.position(
                        partialTicks
                    ) * 0.6662F + ((float) Math.PI)
                ) * 1.4F * animatable.walkAnimation.speed() * 0.9F
            );
        }

        if (rightLeg != null) {
            rightLeg.setRotX(
                Mth.cos(
                    animatable.walkAnimation.position(
                        partialTicks
                    ) * 0.6662F
                ) * 1.4F * animatable.walkAnimation.speed() * 0.9F
            );
        }
    }
}
