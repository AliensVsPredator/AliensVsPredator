package com.blib.internal.client.animation;

import com.blib.api.client.model.v1.AzBone;
import com.blib.internal.client.animation.controller.keyframe.AzBoneAnimationQueue;
import com.blib.internal.client.animation.easing.AzEasingType;
import com.blib.internal.client.animation.easing.AzEasingUtil;
import com.blib.internal.client.model.AzBoneSnapshot;

public class AzBoneAnimationUpdateUtil {

    public static void updatePositions(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        AzEasingType easingType,
        AzBoneSnapshot snapshot
    ) {
        var posXPoint = boneAnimation.positionXQueue().poll();
        var posYPoint = boneAnimation.positionYQueue().poll();
        var posZPoint = boneAnimation.positionZQueue().poll();

        if (posXPoint != null && posYPoint != null && posZPoint != null) {
            bone.setPosX((float) AzEasingUtil.lerpWithOverride(posXPoint, easingType));
            bone.setPosY((float) AzEasingUtil.lerpWithOverride(posYPoint, easingType));
            bone.setPosZ((float) AzEasingUtil.lerpWithOverride(posZPoint, easingType));
            snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
            snapshot.startPosAnim();
            bone.markPositionAsChanged();
        }
    }

    public static void updateRotations(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        AzEasingType easingType,
        AzBoneSnapshot initialSnapshot,
        AzBoneSnapshot snapshot
    ) {
        var rotXPoint = boneAnimation.rotationXQueue().poll();
        var rotYPoint = boneAnimation.rotationYQueue().poll();
        var rotZPoint = boneAnimation.rotationZQueue().poll();

        if (rotXPoint != null && rotYPoint != null && rotZPoint != null) {
            bone.setRotX(
                (float) AzEasingUtil.lerpWithOverride(rotXPoint, easingType) + initialSnapshot.getRotX()
            );
            bone.setRotY(
                (float) AzEasingUtil.lerpWithOverride(rotYPoint, easingType) + initialSnapshot.getRotY()
            );
            bone.setRotZ(
                (float) AzEasingUtil.lerpWithOverride(rotZPoint, easingType) + initialSnapshot.getRotZ()
            );
            snapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
            snapshot.startRotAnim();
            bone.markRotationAsChanged();
        }
    }

    public static void updateScale(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        AzEasingType easingType,
        AzBoneSnapshot snapshot
    ) {
        var scaleXPoint = boneAnimation.scaleXQueue().poll();
        var scaleYPoint = boneAnimation.scaleYQueue().poll();
        var scaleZPoint = boneAnimation.scaleZQueue().poll();

        if (scaleXPoint != null && scaleYPoint != null && scaleZPoint != null) {
            bone.setScaleX((float) AzEasingUtil.lerpWithOverride(scaleXPoint, easingType));
            bone.setScaleY((float) AzEasingUtil.lerpWithOverride(scaleYPoint, easingType));
            bone.setScaleZ((float) AzEasingUtil.lerpWithOverride(scaleZPoint, easingType));
            snapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
            snapshot.startScaleAnim();
            bone.markScaleAsChanged();
        }
    }
}
