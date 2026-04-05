package com.blib.mod.client.render.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import com.blib.api.common.pathfinding.v1.physics.ClimbingOrientationProvider;

/**
 * Client-side debug renderer for climbing entities. Renders directional arrows and state labels for diagnosing
 * orientation and rotation issues.
 */
public final class ClimbingDebugRenderer {

    public static final ClimbingDebugRenderer INSTANCE = new ClimbingDebugRenderer();

    private static final float ARROW_LENGTH = 1.5f;

    private static final int SCAN_RADIUS = 64;

    public void render(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ
    ) {
        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;

        if (level == null) {
            return;
        }

        for (var entity : level.entitiesForRendering()) {
            if (!(entity instanceof ClimbingOrientationProvider provider)) {
                continue;
            }

            if (entity.distanceToSqr(cameraX, cameraY, cameraZ) > SCAN_RADIUS * SCAN_RADIUS) {
                continue;
            }

            renderEntityDebug(poseStack, bufferSource, cameraX, cameraY, cameraZ, entity, provider);
        }
    }

    private void renderEntityDebug(
        PoseStack poseStack,
        MultiBufferSource.BufferSource bufferSource,
        double cameraX,
        double cameraY,
        double cameraZ,
        Entity entity,
        ClimbingOrientationProvider provider
    ) {
        var centerX = (float) (entity.getX() - cameraX);
        var centerY = (float) (entity.getY() + entity.getBbHeight() / 2.0 - cameraY);
        var centerZ = (float) (entity.getZ() - cameraZ);
        var consumer = bufferSource.getBuffer(RenderType.lines());

        poseStack.pushPose();
        var pose = poseStack.last();
        var matrix = pose.pose();

        var surfaceOrdinal = provider.getClimbingSurfaceDirection();

        // 1. Surface normal arrow (magenta) — which surface the physics thinks the entity is on
        if (surfaceOrdinal > 0) {
            var surface = Direction.values()[surfaceOrdinal];

            drawLine(
                consumer,
                matrix,
                pose,
                centerX,
                centerY,
                centerZ,
                centerX + surface.getStepX() * ARROW_LENGTH,
                centerY + surface.getStepY() * ARROW_LENGTH,
                centerZ + surface.getStepZ() * ARROW_LENGTH,
                1.0f,
                0.0f,
                1.0f,
                1.0f
            );
        }

        // 2. Climbing yaw arrow (cyan) — what the renderer uses for facing
        var climbingYaw = provider.getClimbingYaw();
        var climbYawRad = (float) Math.toRadians(-climbingYaw);

        drawLine(
            consumer,
            matrix,
            pose,
            centerX,
            centerY,
            centerZ,
            centerX + Mth.sin(climbYawRad) * ARROW_LENGTH,
            centerY,
            centerZ + Mth.cos(climbYawRad) * ARROW_LENGTH,
            0.0f,
            1.0f,
            1.0f,
            1.0f
        );

        // 3. Vanilla yRot arrow (red) — entity's actual head rotation
        var yRotRad = (float) Math.toRadians(-entity.getYRot());

        drawLine(
            consumer,
            matrix,
            pose,
            centerX,
            centerY,
            centerZ,
            centerX + Mth.sin(yRotRad) * ARROW_LENGTH,
            centerY,
            centerZ + Mth.cos(yRotRad) * ARROW_LENGTH,
            1.0f,
            0.0f,
            0.0f,
            1.0f
        );

        // 4. yBodyRot arrow (orange) — entity's body rotation
        var yBodyRotRad = (float) Math.toRadians(-entity.getVisualRotationYInDegrees());

        drawLine(
            consumer,
            matrix,
            pose,
            centerX,
            centerY,
            centerZ,
            centerX + Mth.sin(yBodyRotRad) * ARROW_LENGTH,
            centerY,
            centerZ + Mth.cos(yBodyRotRad) * ARROW_LENGTH,
            1.0f,
            0.6f,
            0.0f,
            1.0f
        );

        // 5. Movement velocity arrow (green) — actual deltaMovement
        var delta = entity.getDeltaMovement();

        if (delta.lengthSqr() > 0.0001) {
            var scale = 10.0f;

            drawLine(
                consumer,
                matrix,
                pose,
                centerX,
                centerY,
                centerZ,
                centerX + (float) delta.x * scale,
                centerY + (float) delta.y * scale,
                centerZ + (float) delta.z * scale,
                0.0f,
                1.0f,
                0.0f,
                1.0f
            );
        }

        // 6. Current waypoint line (yellow) — next path node the entity is moving toward
        var waypointPacked = provider.getDebugCurrentWaypoint();

        if (waypointPacked != 0) {
            var waypoint = BlockPos.of(waypointPacked);

            drawLine(
                consumer,
                matrix,
                pose,
                centerX,
                centerY,
                centerZ,
                (float) (waypoint.getX() + 0.5 - cameraX),
                (float) (waypoint.getY() + 0.5 - cameraY),
                (float) (waypoint.getZ() + 0.5 - cameraZ),
                1.0f,
                1.0f,
                0.0f,
                1.0f
            );
        }

        // 7. Target destination line (white) — final path destination
        var targetPacked = provider.getDebugTargetPos();

        if (targetPacked != 0) {
            var target = BlockPos.of(targetPacked);

            drawLine(
                consumer,
                matrix,
                pose,
                centerX,
                centerY,
                centerZ,
                (float) (target.getX() + 0.5 - cameraX),
                (float) (target.getY() + 0.5 - cameraY),
                (float) (target.getZ() + 0.5 - cameraZ),
                1.0f,
                1.0f,
                1.0f,
                1.0f
            );
        }

        poseStack.popPose();
    }

    private static void drawLine(
        VertexConsumer consumer,
        org.joml.Matrix4f matrix,
        PoseStack.Pose pose,
        float x1,
        float y1,
        float z1,
        float x2,
        float y2,
        float z2,
        float r,
        float g,
        float b,
        float a
    ) {
        var dx = x2 - x1;
        var dy = y2 - y1;
        var dz = z2 - z1;
        var length = Mth.sqrt(dx * dx + dy * dy + dz * dz);

        if (length < 0.0001f) {
            return;
        }

        var nx = dx / length;
        var ny = dy / length;
        var nz = dz / length;

        consumer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
        consumer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
    }

    private ClimbingDebugRenderer() {}
}
