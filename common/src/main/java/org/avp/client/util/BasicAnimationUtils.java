package org.avp.client.util;

import mod.azure.azurelib.common.api.client.model.GeoModel;
import mod.azure.azurelib.common.internal.common.constant.DataTickets;
import mod.azure.azurelib.common.internal.common.core.animatable.GeoAnimatable;
import mod.azure.azurelib.common.internal.common.core.animation.AnimationState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class BasicAnimationUtils {

    public static void applyHeadRotations(
        GeoModel<? extends GeoAnimatable> geoModel,
        AnimationState<? extends GeoAnimatable> animationState,
        String headName,
        float pitchOffset
    ) {
        var entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        var head = geoModel.getAnimationProcessor().getBone(headName);
        head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD + pitchOffset);
        head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
    }

    public static void applyLimbRotations(
        LivingEntity entity,
        GeoModel<? extends GeoAnimatable> geoModel,
        AnimationState<? extends GeoAnimatable> animationState,
        String leftArmName,
        String rightArmName,
        String leftLegName,
        String rightLegName,
        float armOffset,
        float legOffset
    ) {
        var constant = 0.6662F;
        var walkAnimation = entity.walkAnimation;
        var partialTicks = animationState.getPartialTick();
        var walkPosition = walkAnimation.position(partialTicks);
        var walkSpeed = walkAnimation.speed();
        var animationProcessor = geoModel.getAnimationProcessor();

        var leftArm = animationProcessor.getBone(leftArmName);
        var rightArm = animationProcessor.getBone(rightArmName);
        var leftLeg = animationProcessor.getBone(leftLegName);
        var rightLeg = animationProcessor.getBone(rightLegName);

        leftArm.setRotX(Mth.cos(walkPosition * constant) * 2 * walkSpeed * 0.5F + armOffset);
        rightArm.setRotX(Mth.cos(walkPosition * constant + Mth.PI) * 2 * walkSpeed * 0.5F + armOffset);

        leftLeg.setRotX(Mth.cos(walkPosition * constant + Mth.PI) * 1.4F * walkSpeed * 0.5F + legOffset);
        rightLeg.setRotX(Mth.cos(walkPosition * constant) * 1.4F * walkSpeed * 0.5F + legOffset);
    }

    private BasicAnimationUtils() {
        throw new UnsupportedOperationException();
    }
}
