package com.avp.client.animation;

import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;
import com.avp.common.entity.living.human.marine.MarineAnimationRefs;
import com.avp.common.entity.living.human.marine.MarineMob;

public class MarineAnimator extends AzEntityAnimator<MarineMob> {

    private static final String NAME = "marine";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    @Override
    public void registerControllers(AzAnimationControllerContainer<MarineMob> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, MarineAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(MarineMob animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(MarineMob animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);

        var boneCache = this.context().boneCache();
        var leftArm = boneCache.getBakedModel().getBone("gLeftArm");
        var rightArm = boneCache.getBakedModel().getBone("gRightArm");
        var leftLeg = boneCache.getBakedModel().getBone("gLeftLeg");
        var rightLeg = boneCache.getBakedModel().getBone("gRightLeg");
        if (leftArm.isPresent() && !animatable.isAggressive())
            leftArm.get()
                .setRotX(
                    Mth.cos(
                        animatable.walkAnimation.position(
                            partialTicks
                        ) * 0.6662F
                    ) * 2.0F * animatable.walkAnimation.speed() * 0.9F
                );
        if (rightArm.isPresent() && !animatable.isAggressive())
            rightArm.get()
                .setRotX(
                    Mth.cos(
                        animatable.walkAnimation.position(
                            partialTicks
                        ) * 0.6662F + 3.1415927F
                    ) * 2.0F * animatable.walkAnimation.speed() * 0.9F
                );
        leftLeg.ifPresent(
            azBone -> azBone.setRotX(
                Mth.cos(
                    animatable.walkAnimation.position(
                        partialTicks
                    ) * 0.6662F + 3.1415927F
                ) * 1.4F * animatable.walkAnimation.speed() * 0.9F
            )
        );
        rightLeg.ifPresent(
            azBone -> azBone.setRotX(
                Mth.cos(
                    animatable.walkAnimation.position(
                        partialTicks
                    ) * 0.6662F
                ) * 1.4F * animatable.walkAnimation.speed() * 0.9F
            )
        );
    }
}
