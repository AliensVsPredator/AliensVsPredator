package com.blib.engine.modeler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;

import com.blib.engine.modeler.animation.AnimationEditorState;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;

/**
 * Shared transform-stack convention for the modeler. Bone transforms are applied as
 * {@code translate → pivot → rotate Z-Y-X → scale → unpivot}; cube transforms as
 * {@code pivot → rotate Z-Y-X → unpivot}. Centralized here so {@code ModelerCubeRenderer}, the gizmo renderer, and the
 * picker can't drift out of sync — that would silently break selection / gizmo alignment with the rendered geometry.
 * <p>
 * Bedrock pixel convention (16 px = 1 block) — no divide-by-16 here, the modeler renders in those same units.
 */
@ApiStatus.Internal
public final class ModelerTransforms {

    private ModelerTransforms() {}

    public static void applyBone(PoseStack pose, ModelerBone bone) {
        var transform = EffectiveBoneTransform.of(bone);
        pose.translate(transform.position.x, transform.position.y, transform.position.z);
        pose.translate(bone.pivot.x, bone.pivot.y, bone.pivot.z);
        pose.mulPose(Axis.ZP.rotationDegrees((float) transform.rotation.z));
        pose.mulPose(Axis.YP.rotationDegrees((float) transform.rotation.y));
        pose.mulPose(Axis.XP.rotationDegrees((float) transform.rotation.x));
        pose.scale((float) transform.scale.x, (float) transform.scale.y, (float) transform.scale.z);
        pose.translate(-bone.pivot.x, -bone.pivot.y, -bone.pivot.z);
    }

    public static void applyCube(PoseStack pose, ModelerCube cube) {
        pose.translate(cube.pivot.x, cube.pivot.y, cube.pivot.z);
        applyBlockElementRescale(pose, cube.rotation, cube.blockElementRescale);
        pose.mulPose(Axis.ZP.rotationDegrees((float) cube.rotation.z));
        pose.mulPose(Axis.YP.rotationDegrees((float) cube.rotation.y));
        pose.mulPose(Axis.XP.rotationDegrees((float) cube.rotation.x));
        pose.translate(-cube.pivot.x, -cube.pivot.y, -cube.pivot.z);
    }

    /** Matrix4f overload — used by {@link ModelerPicker} which composes on Matrix4f rather than PoseStack. */
    public static void applyBone(Matrix4f m, ModelerBone bone) {
        var transform = EffectiveBoneTransform.of(bone);
        m.translate((float) transform.position.x, (float) transform.position.y, (float) transform.position.z);
        m.translate((float) bone.pivot.x, (float) bone.pivot.y, (float) bone.pivot.z);
        m.rotateZ((float) Math.toRadians(transform.rotation.z));
        m.rotateY((float) Math.toRadians(transform.rotation.y));
        m.rotateX((float) Math.toRadians(transform.rotation.x));
        m.scale((float) transform.scale.x, (float) transform.scale.y, (float) transform.scale.z);
        m.translate((float) -bone.pivot.x, (float) -bone.pivot.y, (float) -bone.pivot.z);
    }

    public static void applyCube(Matrix4f m, ModelerCube cube) {
        m.translate((float) cube.pivot.x, (float) cube.pivot.y, (float) cube.pivot.z);
        applyBlockElementRescale(m, cube.rotation, cube.blockElementRescale);
        m.rotateZ((float) Math.toRadians(cube.rotation.z));
        m.rotateY((float) Math.toRadians(cube.rotation.y));
        m.rotateX((float) Math.toRadians(cube.rotation.x));
        m.translate((float) -cube.pivot.x, (float) -cube.pivot.y, (float) -cube.pivot.z);
    }

    /**
     * Same shape as {@link #applyBone(PoseStack, ModelerBone)} but reads from a baseline snapshot — used by the ghost-
     * outline renderer to place a bone at its drag-start transform without touching the live model.
     */
    public static void applyBone(PoseStack pose, ModelerGizmoState.BoneBaseline baseline) {
        pose.translate(baseline.position().x, baseline.position().y, baseline.position().z);
        pose.translate(baseline.pivot().x, baseline.pivot().y, baseline.pivot().z);
        pose.mulPose(Axis.ZP.rotationDegrees((float) baseline.rotation().z));
        pose.mulPose(Axis.YP.rotationDegrees((float) baseline.rotation().y));
        pose.mulPose(Axis.XP.rotationDegrees((float) baseline.rotation().x));
        pose.scale((float) baseline.scale().x, (float) baseline.scale().y, (float) baseline.scale().z);
        pose.translate(-baseline.pivot().x, -baseline.pivot().y, -baseline.pivot().z);
    }

    /** Same shape as {@link #applyCube(PoseStack, ModelerCube)} but reads from a baseline snapshot. */
    public static void applyCube(PoseStack pose, ModelerGizmoState.CubeBaseline baseline) {
        pose.translate(baseline.pivot().x, baseline.pivot().y, baseline.pivot().z);
        applyBlockElementRescale(pose, baseline.rotation(), baseline.blockElementRescale());
        pose.mulPose(Axis.ZP.rotationDegrees((float) baseline.rotation().z));
        pose.mulPose(Axis.YP.rotationDegrees((float) baseline.rotation().y));
        pose.mulPose(Axis.XP.rotationDegrees((float) baseline.rotation().x));
        pose.translate(-baseline.pivot().x, -baseline.pivot().y, -baseline.pivot().z);
    }

    private static void applyBlockElementRescale(PoseStack pose, net.minecraft.world.phys.Vec3 rotation, boolean rescale) {
        if (!rescale) {
            return;
        }
        var scale = ModelerBlockElementRotation.rescaleVector(rotation, true);
        pose.scale((float) scale.x, (float) scale.y, (float) scale.z);
    }

    private static void applyBlockElementRescale(Matrix4f m, net.minecraft.world.phys.Vec3 rotation, boolean rescale) {
        if (!rescale) {
            return;
        }
        var scale = ModelerBlockElementRotation.rescaleVector(rotation, true);
        m.scale((float) scale.x, (float) scale.y, (float) scale.z);
    }

    private record EffectiveBoneTransform(
        net.minecraft.world.phys.Vec3 position,
        net.minecraft.world.phys.Vec3 rotation,
        net.minecraft.world.phys.Vec3 scale
    ) {

        static EffectiveBoneTransform of(ModelerBone bone) {
            var preview = AnimationEditorState.get().previewTransformFor(bone);
            if (preview == null) {
                return new EffectiveBoneTransform(bone.position, bone.rotation, bone.scale);
            }
            var position = preview.position() == null ? bone.position : preview.position();
            var rotation = preview.rotationDelta() == null ? bone.rotation : bone.rotation.add(preview.rotationDelta());
            var scale = preview.scale() == null ? bone.scale : preview.scale();
            return new EffectiveBoneTransform(position, rotation, scale);
        }
    }
}
