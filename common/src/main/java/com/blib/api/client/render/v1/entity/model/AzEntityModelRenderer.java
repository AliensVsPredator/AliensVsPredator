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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(AzEntityModelRenderer.class);

    private static final int CLIENT_LOG_INTERVAL = 20;

    private static final float SURFACE_BLEND_SPEED = 0.25f;

    protected final AzEntityRendererPipeline<T> entityRendererPipeline;

    private final Map<Integer, float[]> displayedNormals = new HashMap<>();

    private int previousSurfaceOrdinal;

    private int clientLogCounter;

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
        float lerpBodyRot = getLerpRot(animatable, partialTick);

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
     * Applies climbing surface orientation using normal-vector blending (crawling-port approach). The raw surface
     * normal is smoothly interpolated per-frame, then yaw/pitch/roll are computed from the blended normal. This avoids
     * Euler angle interpolation artifacts. Body rotation uses vanilla's yBodyRot (set by ClimbingMoveControl to track
     * world-space movement direction).
     */
    private void applyClimbingOrientation(T animatable, PoseStack poseStack) {
        if (!(animatable instanceof ClimbingOrientationProvider provider)) {
            return;
        }

        var surfaceOrdinal = provider.getClimbingSurfaceDirection();
        var entityId = animatable.getId();

        if (surfaceOrdinal != previousSurfaceOrdinal) {
            var prevDir = Direction.values()[Math.min(previousSurfaceOrdinal, 5)];
            var newDir = Direction.values()[Math.min(surfaceOrdinal, 5)];

            LOGGER.info(
                "[RENDER] {} surface {} -> {} entityPos=({}, {}, {}) blockPos={}",
                animatable.getName().getString(),
                previousSurfaceOrdinal == 0 ? "GROUND" : prevDir.toString(),
                surfaceOrdinal == 0 ? "GROUND" : newDir.toString(),
                String.format("%.2f", animatable.getX()),
                String.format("%.2f", animatable.getY()),
                String.format("%.2f", animatable.getZ()),
                animatable.blockPosition()
            );

            previousSurfaceOrdinal = surfaceOrdinal;
        }

        var displayed = displayedNormals.get(entityId);

        if (surfaceOrdinal <= 0) {
            if (displayed != null) {
                displayed[0] = Mth.lerp(SURFACE_BLEND_SPEED, displayed[0], 0);
                displayed[1] = Mth.lerp(SURFACE_BLEND_SPEED, displayed[1], 0);
                displayed[2] = Mth.lerp(SURFACE_BLEND_SPEED, displayed[2], 0);

                var remaining = Math.abs(displayed[0]) + Math.abs(displayed[1]) + Math.abs(displayed[2]);

                if (remaining < 0.01f) {
                    displayedNormals.remove(entityId);
                    return;
                }

                applyNormalRotation(poseStack, displayed[0], displayed[1], displayed[2], animatable.getBbHeight());
            }

            return;
        }

        var surface = Direction.values()[surfaceOrdinal];
        var targetNormal = surface.getOpposite().step();

        if (displayed == null) {
            displayed = new float[] { targetNormal.x, targetNormal.y, targetNormal.z };
            displayedNormals.put(entityId, displayed);
        } else {
            displayed[0] = Mth.lerp(SURFACE_BLEND_SPEED, displayed[0], targetNormal.x);
            displayed[1] = Mth.lerp(SURFACE_BLEND_SPEED, displayed[1], targetNormal.y);
            displayed[2] = Mth.lerp(SURFACE_BLEND_SPEED, displayed[2], targetNormal.z);
        }

        var length = Mth.sqrt(displayed[0] * displayed[0] + displayed[1] * displayed[1] + displayed[2] * displayed[2]);

        if (length < 0.001f) {
            return;
        }

        var normalX = displayed[0] / length;
        var normalY = displayed[1] / length;
        var normalZ = displayed[2] / length;

        clientLogCounter++;

        if (clientLogCounter % CLIENT_LOG_INTERVAL == 0) {
            LOGGER.info(
                "[RENDER] {} surface={} blendedNormal=({},{},{}) bbHeight={}",
                animatable.getName().getString(),
                surface,
                String.format("%.2f", normalX),
                String.format("%.2f", normalY),
                String.format("%.2f", normalZ),
                String.format("%.2f", animatable.getBbHeight())
            );
        }

        applyNormalRotation(poseStack, normalX, normalY, normalZ, animatable.getBbHeight());
    }

    private static void applyNormalRotation(PoseStack poseStack, float normalX, float normalY, float normalZ, float entityHeight) {
        var orientationYaw = (float) Math.toDegrees(Mth.atan2(normalX, normalZ));

        var yawRad = Math.toRadians(orientationYaw);
        var recomputedZ = (float) (Math.sin(yawRad) * normalX + Math.cos(yawRad) * normalZ);
        var recomputedY = normalY;
        var recomputedX = (float) (Math.sin(yawRad - Math.PI / 2) * normalX + Math.cos(yawRad - Math.PI / 2) * normalZ);

        var horizontalLength = Mth.sqrt(recomputedX * recomputedX + recomputedZ * recomputedZ);
        var orientationPitch = (float) Math.toDegrees(Mth.atan2(horizontalLength, recomputedY));

        var rollSign = Math.signum(0.5f - recomputedY - recomputedZ - recomputedX);
        var roll = rollSign * orientationYaw;

        var halfHeight = entityHeight / 2.0;

        poseStack.translate(-normalX * halfHeight, -normalY * halfHeight, -normalZ * halfHeight);
        poseStack.mulPose(Axis.YP.rotationDegrees(orientationYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(orientationPitch));
        poseStack.mulPose(Axis.YP.rotationDegrees(roll));
    }
}
