package com.blib.common.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class EntityUtil {

    public static Vec3 getRelativePosition(LivingEntity entity, double leftOffset, double upOffset, double backwardOffset) {
        var forward = getBodyForward(entity);
        // perpendicular on XZ plane.
        var left = new Vec3(forward.z, 0, -forward.x).normalize();

        var bounds = entity.getBoundingBox();
        var base = new Vec3(
            (bounds.minX + bounds.maxX) / 2.0,
            bounds.minY,
            (bounds.minZ + bounds.maxZ) / 2.0
        );

        // Negative backwardOffset = forward, positive = behind.
        return base
            .add(forward.scale(-backwardOffset))
            .add(left.scale(leftOffset))
            .add(0, upOffset, 0);
    }

    public static Vec3 getBodyForward(LivingEntity entity) {
        // Body yaw in degrees -> radians.
        var yawRad = entity.yBodyRot * Mth.DEG_TO_RAD;
        // Minecraft’s X- (west / left) Z+ (south / forward) convention.
        return new Vec3(-Mth.sin(yawRad), 0, Mth.cos(yawRad)).normalize();
    }

    public static boolean canMobSeeBlock(Mob mob, Vec3 targetVec) {
        var level = mob.level();
        // Get eye position of the mob.
        var eyePos = mob.getEyePosition();

        var context = new ClipContext(
            eyePos,
            targetVec,
            // blocks that block vision.
            ClipContext.Block.OUTLINE,
            // ignore fluids.
            ClipContext.Fluid.NONE,
            mob
        );

        var hitResult = level.clip(context);

        // Check if it directly hits the block.
        return hitResult.getType() == HitResult.Type.MISS || hitResult.getLocation().distanceToSqr(targetVec) < 0.1;
    }
}
