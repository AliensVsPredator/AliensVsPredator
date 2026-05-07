package com.blib.api.client.render.v1;

import com.blib.api.client.render.v1.item.BLibGeoBoneItemRenderer;
import com.blib.api.client.render.v1.item.BLibItemTransforms;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Vector3f;

/**
 * A single translation/rotation/scale to apply to a {@link PoseStack}, in that order. Rotation is Euler
 * degrees (XYZ order, applied via {@link PoseStack#mulPose} with quaternion conversion). Scale is per-axis.
 * Used by {@link BLibItemTransforms} to position a {@link BLibGeoBoneItemRenderer}'s rendered bone for each
 * {@link net.minecraft.world.item.ItemDisplayContext}.
 *
 * @param translation Translation in model space (blocks, where 1.0 = 16 pixels at scale 1.0).
 * @param rotation    Rotation in degrees, applied as X then Y then Z.
 * @param scale       Per-axis scale.
 */
public record BLibTransform(Vector3f translation, Vector3f rotation, Vector3f scale) {

    public static final BLibTransform IDENTITY = new BLibTransform(
        new Vector3f(0, 0, 0),
        new Vector3f(0, 0, 0),
        new Vector3f(1, 1, 1)
    );

    public static BLibTransform of(float tx, float ty, float tz, float rx, float ry, float rz, float scale) {
        return new BLibTransform(new Vector3f(tx, ty, tz), new Vector3f(rx, ry, rz), new Vector3f(scale, scale, scale));
    }

    public static BLibTransform of(Vector3f translation, Vector3f rotation, float scale) {
        return new BLibTransform(translation, rotation, new Vector3f(scale, scale, scale));
    }

    public void apply(PoseStack poseStack) {
        poseStack.translate(translation.x, translation.y, translation.z);

        if (rotation.x != 0) {
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation.x));
        }

        if (rotation.y != 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation.y));
        }

        if (rotation.z != 0) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation.z));
        }

        poseStack.scale(scale.x, scale.y, scale.z);
    }
}
