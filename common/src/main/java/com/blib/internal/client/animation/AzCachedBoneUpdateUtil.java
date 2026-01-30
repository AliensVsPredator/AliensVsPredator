package com.blib.internal.client.animation;

import java.util.Map;

import com.blib.api.client.model.v1.AzBone;
import com.blib.internal.client.model.AzBoneSnapshot;

public class AzCachedBoneUpdateUtil {

    public static void updateCachedBonePosition(
        AzBone bone,
        Map<String, AzBoneSnapshot> boneSnapshots,
        double animTime,
        double resetTickLength
    ) {
        if (bone.hasPositionChanged()) {
            return;
        }

        var initialSnapshot = bone.getInitialAzSnapshot();
        var saveSnapshot = boneSnapshots.get(bone.getName());

        if (saveSnapshot.isPosAnimInProgress()) {
            saveSnapshot.stopPosAnim(animTime);
        }

        var percentageReset = Math.min(
            (animTime - saveSnapshot.getLastResetPositionTick()) / resetTickLength,
            1
        );

        bone.setPosX(
            (float) Interpolations.lerp(
                saveSnapshot.getOffsetX(),
                initialSnapshot.getOffsetX(),
                percentageReset
            )
        );
        bone.setPosY(
            (float) Interpolations.lerp(
                saveSnapshot.getOffsetY(),
                initialSnapshot.getOffsetY(),
                percentageReset
            )
        );
        bone.setPosZ(
            (float) Interpolations.lerp(
                saveSnapshot.getOffsetZ(),
                initialSnapshot.getOffsetZ(),
                percentageReset
            )
        );

        if (percentageReset >= 1) {
            saveSnapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
        }
    }

    public static void updateCachedBoneRotation(
        AzBone bone,
        Map<String, AzBoneSnapshot> boneSnapshots,
        double animTime,
        double resetTickLength
    ) {
        if (bone.hasRotationChanged()) {
            return;
        }

        var initialSnapshot = bone.getInitialAzSnapshot();
        var saveSnapshot = boneSnapshots.get(bone.getName());

        if (saveSnapshot.isRotAnimInProgress()) {
            saveSnapshot.stopRotAnim(animTime);
        }

        double percentageReset = Math.min(
            (animTime - saveSnapshot.getLastResetRotationTick()) / resetTickLength,
            1
        );

        bone.setRotX(
            (float) Interpolations.lerp(saveSnapshot.getRotX(), initialSnapshot.getRotX(), percentageReset)
        );
        bone.setRotY(
            (float) Interpolations.lerp(saveSnapshot.getRotY(), initialSnapshot.getRotY(), percentageReset)
        );
        bone.setRotZ(
            (float) Interpolations.lerp(saveSnapshot.getRotZ(), initialSnapshot.getRotZ(), percentageReset)
        );

        if (percentageReset >= 1) {
            saveSnapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
        }
    }

    public static void updateCachedBoneScale(
        AzBone bone,
        Map<String, AzBoneSnapshot> boneSnapshots,
        double animTime,
        double resetTickLength
    ) {
        if (bone.hasScaleChanged()) {
            return;
        }

        var initialSnapshot = bone.getInitialAzSnapshot();
        var saveSnapshot = boneSnapshots.get(bone.getName());

        if (saveSnapshot.isScaleAnimInProgress()) {
            saveSnapshot.stopScaleAnim(animTime);
        }

        double percentageReset = Math.min(
            (animTime - saveSnapshot.getLastResetScaleTick()) / resetTickLength,
            1
        );

        bone.setScaleX(
            (float) Interpolations.lerp(saveSnapshot.getScaleX(), initialSnapshot.getScaleX(), percentageReset)
        );
        bone.setScaleY(
            (float) Interpolations.lerp(saveSnapshot.getScaleY(), initialSnapshot.getScaleY(), percentageReset)
        );
        bone.setScaleZ(
            (float) Interpolations.lerp(saveSnapshot.getScaleZ(), initialSnapshot.getScaleZ(), percentageReset)
        );

        if (percentageReset >= 1) {
            saveSnapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
        }
    }
}
