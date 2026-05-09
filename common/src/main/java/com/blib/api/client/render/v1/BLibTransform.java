package com.blib.api.client.render.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.joml.Vector3f;

import com.blib.api.client.render.v1.item.BLibGeoBoneItemRenderer;
import com.blib.api.client.render.v1.item.BLibItemTransforms;

/**
 * A translation / rotation / scale to apply to a {@link PoseStack}, with an optional pivot point that rotation and
 * scale are applied around. Used by {@link BLibItemTransforms} to position a {@link BLibGeoBoneItemRenderer}'s rendered
 * bone for each {@link net.minecraft.world.item.ItemDisplayContext}.
 * <p>
 * Application order on the pose stack:
 * <ol>
 * <li>{@code translate(translation)} — moves the pose-stack origin to the configured anchor point.</li>
 * <li>{@code translate(pivot)} — shifts the pose-stack origin to the pivot point relative to that anchor.</li>
 * <li>{@code rotate(rotation)} — rotates around the pivot point. Euler XYZ order.</li>
 * <li>{@code scale(scale)} — scales around the pivot point.</li>
 * <li>{@code translate(-pivot)} — restores the pose-stack origin back to {@code translation} in the
 * now-rotated-and-scaled frame, so subsequent bone-walk transforms cascade from there.</li>
 * </ol>
 * Default pivot {@code (0, 0, 0)} keeps the rotation/scale anchored to the {@code translation} point. When used with
 * {@link BLibGeoBoneItemRenderer}, the renderer separately anchors the bone's authored pivot to pose-stack origin, so
 * {@code translation=(0,0,0)} and {@code pivot=(0,0,0)} already gives rotations that sweep around the bone's pivot —
 * set {@code pivot} only when the user wants a rotation center somewhere other than the bone pivot itself (e.g. nudge
 * the rotation center to the bottom of a hat).
 *
 * @param translation Translation in model space (blocks, where 1.0 = 16 pixels at scale 1.0).
 * @param rotation    Rotation in degrees, applied as X then Y then Z.
 * @param scale       Per-axis scale.
 * @param pivot       Offset from {@code translation} that rotation and scale are applied around.
 */
public record BLibTransform(
    Vector3f translation,
    Vector3f rotation,
    Vector3f scale,
    Vector3f pivot
) {

    public static final BLibTransform IDENTITY = new BLibTransform(
        new Vector3f(0, 0, 0),
        new Vector3f(0, 0, 0),
        new Vector3f(1, 1, 1),
        new Vector3f(0, 0, 0)
    );

    public static BLibTransform of(float tx, float ty, float tz, float rx, float ry, float rz, float scale) {
        return new BLibTransform(
            new Vector3f(tx, ty, tz),
            new Vector3f(rx, ry, rz),
            new Vector3f(scale, scale, scale),
            new Vector3f(0, 0, 0)
        );
    }

    public static BLibTransform of(
        float tx,
        float ty,
        float tz,
        float rx,
        float ry,
        float rz,
        float scale,
        float px,
        float py,
        float pz
    ) {
        return new BLibTransform(
            new Vector3f(tx, ty, tz),
            new Vector3f(rx, ry, rz),
            new Vector3f(scale, scale, scale),
            new Vector3f(px, py, pz)
        );
    }

    public static BLibTransform of(Vector3f translation, Vector3f rotation, float scale) {
        return new BLibTransform(translation, rotation, new Vector3f(scale, scale, scale), new Vector3f(0, 0, 0));
    }

    public void apply(PoseStack poseStack) {
        poseStack.translate(translation.x, translation.y, translation.z);

        boolean hasPivot = pivot.x != 0 || pivot.y != 0 || pivot.z != 0;

        if (hasPivot) {
            poseStack.translate(pivot.x, pivot.y, pivot.z);
        }

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

        if (hasPivot) {
            poseStack.translate(-pivot.x, -pivot.y, -pivot.z);
        }
    }
}
