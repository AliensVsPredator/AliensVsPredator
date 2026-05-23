package com.blib.api.client.render.v1.dismemberment;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;

@ApiStatus.Internal
final class LimbRenderTransforms {

    private LimbRenderTransforms() {}

    static void applySourceTransform(PoseStack poseStack, DismemberedLimbEntity limb) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - limb.getYRot()));

        var sourceScale = limb.getSourceScale();
        poseStack.scale(sourceScale, sourceScale, sourceScale);
    }
}
