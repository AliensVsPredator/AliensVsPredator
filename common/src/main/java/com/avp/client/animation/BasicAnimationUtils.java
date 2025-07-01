package com.avp.client.animation;

import mod.azure.azurelib.rewrite.animation.AzAnimationContext;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class BasicAnimationUtils {

    public static void applyHeadRotations(
        LivingEntity entity,
        AzAnimationContext<?> context,
        float partialTick,
        String headName,
        float pitchOffset
    ) {
        var bakedModel = context.boneCache().getBakedModel();
        var head = bakedModel.getBoneOrNull(headName);

        if (head != null) {
            var headPitch = computeHeadPitch(entity, partialTick);
            head.setRotX(headPitch * Mth.DEG_TO_RAD + pitchOffset);
            head.setRotY(computeNetHeadYaw(entity, partialTick) * Mth.DEG_TO_RAD);
        }
    }

    public static void applyLimbRotations(
        LivingEntity entity,
        AzAnimationContext<?> context,
        float partialTick,
        String leftArmName,
        String rightArmName,
        String leftLegName,
        String rightLegName,
        float armOffset,
        float legOffset
    ) {
        var bakedModel = context.boneCache().getBakedModel();
        var constant = 0.6662F;
        var walkAnimation = entity.walkAnimation;
        var walkPosition = walkAnimation.position(partialTick);
        var walkSpeed = walkAnimation.speed();

        var leftArm = bakedModel.getBoneOrNull(leftArmName);
        var rightArm = bakedModel.getBoneOrNull(rightArmName);
        var leftLeg = bakedModel.getBoneOrNull(leftLegName);
        var rightLeg = bakedModel.getBoneOrNull(rightLegName);

        if (leftArm != null) {
            leftArm.setRotX(Mth.cos(walkPosition * constant) * 2 * walkSpeed * 0.5F + armOffset);
        }

        if (rightArm != null) {
            rightArm.setRotX(Mth.cos(walkPosition * constant + Mth.PI) * 2 * walkSpeed * 0.5F + armOffset);
        }

        if (leftLeg != null) {
            leftLeg.setRotX(Mth.cos(walkPosition * constant + Mth.PI) * 1.4F * walkSpeed * 0.5F + legOffset);
        }

        if (rightLeg != null) {
            rightLeg.setRotX(Mth.cos(walkPosition * constant) * 1.4F * walkSpeed * 0.5F + legOffset);
        }
    }

    private static float computeNetHeadYaw(LivingEntity entity, float partialTick) {
        var shouldSit = entity.isPassenger() && (entity.getVehicle() != null);
        var lerpBodyRot = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        var lerpHeadRot = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        var netHeadYaw = lerpHeadRot - lerpBodyRot;

        if (shouldSit && entity.getVehicle() instanceof LivingEntity livingVehicle) {
            lerpBodyRot = Mth.rotLerp(partialTick, livingVehicle.yBodyRotO, livingVehicle.yBodyRot);
            netHeadYaw = lerpHeadRot - lerpBodyRot;
            var clampedHeadYaw = Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;

            if (clampedHeadYaw * clampedHeadYaw > 2500f)
                lerpBodyRot += clampedHeadYaw * 0.2f;

            netHeadYaw = lerpHeadRot - lerpBodyRot;
        }

        return -netHeadYaw;
    }

    private static float computeHeadPitch(LivingEntity entity, float partialTick) {
        return -Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
    }

    private BasicAnimationUtils() {
        throw new UnsupportedOperationException();
    }
}
