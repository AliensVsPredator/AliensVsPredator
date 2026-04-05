package com.blib.api.client.render.v1.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.blib.api.client.model.v1.AzBone;
import com.blib.api.client.render.v1.AzLayerRenderer;
import com.blib.api.client.render.v1.AzModelRenderer;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.entity.pipeline.AzEntityRendererPipeline;
import com.blib.api.common.pathfinding.v1.physics.ClimbingOrientationProvider;
import com.blib.internal.client.render.util.RenderUtil;

public class AzEntityModelRenderer<T extends Entity> extends AzModelRenderer<UUID, T> {

    private static final float FORWARD_BLEND_SPEED = 0.25f;

    protected final AzEntityRendererPipeline<T> entityRendererPipeline;

    private final Map<Integer, float[]> displayedForwards = new HashMap<>();

    public AzEntityModelRenderer(
        AzEntityRendererPipeline<T> entityRendererPipeline,
        AzLayerRenderer<UUID, T> layerRenderer
    ) {
        super(entityRendererPipeline, layerRenderer);
        this.entityRendererPipeline = entityRendererPipeline;
    }

    @Override
    public void render(AzRendererPipelineContext<UUID, T> context, boolean isReRender) {
        var animatable = context.animatable();
        var partialTick = context.partialTick();
        var poseStack = context.poseStack();

        poseStack.pushPose();
        float lerpBodyRot = getClimbingAwareBodyRot(animatable, partialTick);

        if (animatable.getPose() == Pose.SLEEPING && animatable instanceof LivingEntity livingEntity) {
            Direction bedDirection = livingEntity.getBedOrientation();

            if (bedDirection != null) {
                float eyePosOffset = livingEntity.getEyeHeight(Pose.STANDING) - 0.1F;

                poseStack.translate(
                    -bedDirection.getStepX() * eyePosOffset,
                    0,
                    -bedDirection.getStepZ() * eyePosOffset
                );
            }
        }

        float nativeScale = animatable instanceof LivingEntity livingEntity ? livingEntity.getScale() : 1;
        float ageInTicks = animatable.tickCount + partialTick;

        poseStack.scale(nativeScale, nativeScale, nativeScale);
        applyClimbingOrientation(animatable, poseStack);
        applyRotations(animatable, poseStack, ageInTicks, lerpBodyRot, partialTick, nativeScale);

        if (!isReRender) {
            var animator = entityRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                handleAnimation(animator, animatable, context.partialTick());
            }
        }

        entityRendererPipeline.getModelRenderTranslations().set(poseStack.last().pose());

        if (context.vertexConsumer() != null) {
            super.render(context, isReRender);
        }

        poseStack.popPose();
    }

    @Override
    public void renderRecursively(AzRendererPipelineContext<UUID, T> context, AzBone bone, boolean isReRender) {
        var buffer = context.vertexConsumer();
        var bufferSource = context.multiBufferSource();
        var entity = context.animatable();
        var poseStack = context.poseStack();

        poseStack.pushPose();
        RenderUtil.translateMatrixToBone(poseStack, bone);
        RenderUtil.translateToPivotPoint(poseStack, bone);
        RenderUtil.rotateMatrixAroundBone(poseStack, bone);
        RenderUtil.scaleMatrixForBone(poseStack, bone);

        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());
            Matrix4f localMatrix = RenderUtil.invertAndMultiplyMatrices(
                poseState,
                entityRendererPipeline.getEntityRenderTranslations()
            );

            bone.setModelSpaceMatrix(
                RenderUtil.invertAndMultiplyMatrices(poseState, entityRendererPipeline.getModelRenderTranslations())
            );
            bone.setLocalSpaceMatrix(
                RenderUtil.translateMatrix(
                    localMatrix,
                    entityRendererPipeline.getRenderer().getRenderOffset(entity, 1).toVector3f()
                )
            );
            bone.setWorldSpaceMatrix(
                RenderUtil.translateMatrix(new Matrix4f(localMatrix), entity.position().toVector3f())
            );
        }

        RenderUtil.translateAwayFromPivotPoint(poseStack, bone);

        context.setVertexConsumer(getOrRefreshRenderBuffer(isReRender, context, bone));

        if (
            !boneRenderOverride(
                poseStack,
                bone,
                bufferSource,
                buffer,
                context.partialTick(),
                context.packedLight(),
                context.packedOverlay(),
                context.renderColor()
            )
        )
            super.renderCubesOfBone(context, bone);

        if (!isReRender) {
            layerRenderer.applyRenderLayersForBone(context, bone);
        }

        renderChildBones(context, bone, isReRender);

        poseStack.popPose();
    }

    private static <T extends Entity> float getLerpRot(T animatable, float partialTick) {
        boolean shouldSit = animatable.isPassenger() && (animatable.getVehicle() != null);

        float lerpBodyRot = animatable instanceof LivingEntity livingEntity
            ? Mth.rotLerp(
                partialTick,
                livingEntity.yBodyRotO,
                livingEntity.yBodyRot
            )
            : animatable.getYRot();
        float lerpHeadRot = animatable instanceof LivingEntity livingEntity
            ? Mth.rotLerp(
                partialTick,
                livingEntity.yHeadRotO,
                livingEntity.yHeadRot
            )
            : animatable.getYHeadRot();

        if (shouldSit && animatable.getVehicle() instanceof LivingEntity livingentity) {
            lerpBodyRot = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
            float netHeadYaw = lerpHeadRot - lerpBodyRot;
            float clampedHeadYaw = Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85, 85);
            lerpBodyRot = lerpHeadRot - clampedHeadYaw;

            if (clampedHeadYaw * clampedHeadYaw > 2500f)
                lerpBodyRot += clampedHeadYaw * 0.2f;
        }
        return lerpBodyRot;
    }

    protected void applyRotations(
        T animatable,
        PoseStack poseStack,
        float ageInTicks,
        float rotationYaw,
        float partialTick,
        float nativeScale
    ) {
        if (animatable.isFullyFrozen()) {
            rotationYaw += (float) (Math.cos(animatable.tickCount * 3.25d) * Math.PI * 0.4d);
        }

        if (!animatable.hasPose(Pose.SLEEPING)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));
        }

        if (animatable instanceof LivingEntity livingEntity) {
            var config = entityRendererPipeline.getRenderer().config();
            var deathMaxRotation = config.getDeathMaxRotation(animatable);

            if (livingEntity.deathTime > 0) {
                float deathRotation = (livingEntity.deathTime + partialTick - 1f) / 20f * 1.6f;

                poseStack.mulPose(
                    Axis.ZP.rotationDegrees(Math.min(Mth.sqrt(deathRotation), 1) * deathMaxRotation)
                );
            } else if (livingEntity.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f - livingEntity.getXRot()));
                poseStack.mulPose(Axis.YP.rotationDegrees((livingEntity.tickCount + partialTick) * -75f));
            } else if (animatable.hasPose(Pose.SLEEPING)) {
                Direction bedOrientation = livingEntity.getBedOrientation();

                poseStack.mulPose(
                    Axis.YP.rotationDegrees(
                        bedOrientation != null ? RenderUtil.getDirectionAngle(bedOrientation) : rotationYaw
                    )
                );
                poseStack.mulPose(Axis.ZP.rotationDegrees(deathMaxRotation));
                poseStack.mulPose(Axis.YP.rotationDegrees(270f));
            } else if (LivingEntityRenderer.isEntityUpsideDown(livingEntity)) {
                poseStack.translate(0, (animatable.getBbHeight() + 0.1f) / nativeScale, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180f));
            }
        }
    }

    /**
     * Applies climbing orientation by building the rotation directly from the surface normal and movement direction. No
     * Euler angle decomposition — the rotation matrix is constructed from the two vectors that fully define the
     * entity's orientation on the surface. The surface normal determines the surface tilt, and deltaMovement determines
     * which direction the entity faces along the surface.
     */
    private void applyClimbingOrientation(T animatable, PoseStack poseStack) {
        if (!(animatable instanceof ClimbingOrientationProvider provider)) {
            return;
        }

        var surfaceOrdinal = provider.getClimbingSurfaceDirection();
        var entityId = animatable.getId();

        if (surfaceOrdinal <= 0) {
            displayedForwards.remove(entityId);
            return;
        }

        var surface = Direction.values()[surfaceOrdinal];
        var normal = surface.getOpposite().step();
        float upX = normal.x;
        float upY = normal.y;
        float upZ = normal.z;

        var delta = animatable.getDeltaMovement();
        var stored = displayedForwards.get(entityId);

        float targetFwdX = (float) delta.x;
        float targetFwdY = (float) delta.y;
        float targetFwdZ = (float) delta.z;

        // Project movement onto the surface plane (remove the normal component).
        var dot = targetFwdX * upX + targetFwdY * upY + targetFwdZ * upZ;

        targetFwdX -= upX * dot;
        targetFwdY -= upY * dot;
        targetFwdZ -= upZ * dot;

        var targetLen = Mth.sqrt(targetFwdX * targetFwdX + targetFwdY * targetFwdY + targetFwdZ * targetFwdZ);

        if (targetLen > 0.001f) {
            targetFwdX /= targetLen;
            targetFwdY /= targetLen;
            targetFwdZ /= targetLen;

            if (stored == null) {
                stored = new float[] { targetFwdX, targetFwdY, targetFwdZ };
                displayedForwards.put(entityId, stored);
            } else {
                stored[0] = Mth.lerp(FORWARD_BLEND_SPEED, stored[0], targetFwdX);
                stored[1] = Mth.lerp(FORWARD_BLEND_SPEED, stored[1], targetFwdY);
                stored[2] = Mth.lerp(FORWARD_BLEND_SPEED, stored[2], targetFwdZ);
            }
        }

        if (stored == null) {
            return;
        }

        // Normalize the displayed forward.
        var fwdLen = Mth.sqrt(stored[0] * stored[0] + stored[1] * stored[1] + stored[2] * stored[2]);

        if (fwdLen < 0.001f) {
            return;
        }

        float fwdX = stored[0] / fwdLen;
        float fwdY = stored[1] / fwdLen;
        float fwdZ = stored[2] / fwdLen;

        // Right = cross(forward, up)
        float rightX = fwdY * upZ - fwdZ * upY;
        float rightY = fwdZ * upX - fwdX * upZ;
        float rightZ = fwdX * upY - fwdY * upX;

        // Translation offset: pivot around center of mass.
        var halfHeight = animatable.getBbHeight() / 2.0;

        poseStack.translate(-upX * halfHeight, -upY * halfHeight, -upZ * halfHeight);

        // Build rotation matrix (JOML column-major constructor).
        // Column 0: model +X → right
        // Column 1: model +Y (head) → forward (direction of travel on surface)
        // Column 2: model +Z (back) → surface normal (away from surface, so belly faces surface)
        var matrix = new Matrix4f(
            rightX,
            rightY,
            rightZ,
            0,
            fwdX,
            fwdY,
            fwdZ,
            0,
            upX,
            upY,
            upZ,
            0,
            0,
            0,
            0,
            1
        );

        poseStack.last().pose().mul(matrix);
        poseStack.last().normal().mul(new org.joml.Matrix3f(matrix));
    }

    /**
     * Returns the body rotation for rendering. Returns 180 when climbing to neutralize vanilla's
     * {@code YP(180 - bodyRot)} rotation, since {@code applyClimbingOrientation} already handles the full orientation.
     */
    private float getClimbingAwareBodyRot(T animatable, float partialTick) {
        if (
            animatable instanceof ClimbingOrientationProvider provider
                && provider.getClimbingSurfaceDirection() > 0
        ) {
            return 180.0f;
        }

        return getLerpRot(animatable, partialTick);
    }
}
