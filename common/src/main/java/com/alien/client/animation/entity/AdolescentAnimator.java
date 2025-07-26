package com.alien.client.animation.entity;

import com.alien.common.gameplay.entity.living.alien.adolescent.Adolescent;
import com.alien.common.util.AzAlienAnimationUtil;
import mod.azure.azurelib.rewrite.animation.AzAnimatorConfig;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.rewrite.model.AzBakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.avp.AVPResources;

public class AdolescentAnimator extends AzEntityAnimator<Adolescent> {

    private static final String NAME = "adolescent";

    private static final ResourceLocation ANIMATION = AVPResources.entityAnimationLocation(NAME);

    public AdolescentAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Adolescent> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, AzAlienAnimationUtil.BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.HEAD_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.LEFT_ARM_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.LEFT_LEG_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.RIGHT_ARM_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.RIGHT_LEG_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build(),
            AzAnimationController.builder(this, AzAlienAnimationUtil.TAIL_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Adolescent animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(Adolescent animatable, float partialTicks) {
        super.setCustomAnimations(animatable, partialTicks);
        showDorsalTubes(animatable);

        runPassiveAnimations(animatable);
    }

    private void showDorsalTubes(Adolescent entity) {
        var bakedModel = context().boneCache().getBakedModel();
        hideDorsalTube(entity, bakedModel, "gLeftUpperDorsalTubeNub");
        hideDorsalTube(entity, bakedModel, "gRightUpperDorsalTubeNub");
        hideDorsalTube(entity, bakedModel, "gLeftLowerDorsalTubeNub");
        hideDorsalTube(entity, bakedModel, "gRightLowerDorsalTubeNub");
    }

    private static void hideDorsalTube(Adolescent entity, AzBakedModel bakedModel, String dorsalTubeBoneName) {
        var dorsalTubeNub = bakedModel.getBoneOrNull(dorsalTubeBoneName);

        if (dorsalTubeNub != null) {
            dorsalTubeNub.setHidden(!entity.hasDorsalTubes.get());
        }
    }

    private void runPassiveAnimations(Adolescent adolescent) {
        var dispatcher = adolescent.getAnimationDispatcher();
        var isMovingOnGround = adolescent.isMovingHorizontally.get() && adolescent.onGround();
        Runnable animFunction;

        if (adolescent.isUnderWater()) {
            // TODO: idle swim
            animFunction = dispatcher::swim;
        } else if (isMovingOnGround) {
            if (adolescent.hasTarget.get()) {
                animFunction = dispatcher::run;
            } else {
                animFunction = dispatcher::walk;
            }
        } else {
            animFunction = dispatcher::idle;
        }

        animFunction.run();
    }
}
