package com.blib.api.common.pathfinding.v1.physics;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

/**
 * Handles the {@code travel()} physics for entities attached to a climbing surface. Called from
 * {@code MixinLivingEntity_Travel} when the entity's {@link ClimbingMoveControl} has an active surface, completely
 * replacing vanilla's gravity, friction, and movement pipeline for that tick.
 * <p>
 * Since vanilla {@code travel()} is cancelled, no downward gravity is applied. Instead, a sticking force pushes the
 * entity into the surface (wall, ceiling) using the real gravity attribute value. Friction is decomposed into
 * tangential (along the surface) and orthogonal (into/away from the surface) components with separate drag rates.
 * </p>
 */
public final class ClimbingTravelHandler {

    private static final float TANGENTIAL_DRAG = 0.91f;

    private static final float ORTHOGONAL_DRAG = 0.98f;

    private ClimbingTravelHandler() {}

    /**
     * Replaces vanilla {@code travel()} for a climbing entity. Assumes {@link ClimbingMoveControl#tick()} has already
     * set the entity's delta movement toward the current path target.
     */
    public static void travel(Mob mob, ClimbingMoveControl moveControl) {
        var surface = moveControl.getActiveSurface();

        if (surface == null || surface == Direction.DOWN) {
            return;
        }

        applyStickingForce(mob, surface, moveControl.isNearEdgeTransition());
        mob.move(MoverType.SELF, mob.getDeltaMovement());
        applySurfaceRelativeFriction(mob, surface);
        mob.calculateEntityAnimation(false);
    }

    /**
     * Pushes the entity into the surface with a force equal to the entity's gravity attribute. Skipped near edge
     * transitions to allow smoother surface changes.
     */
    private static void applyStickingForce(Mob mob, Direction surface, boolean nearEdgeTransition) {
        var normal = surface.step();
        var gravity = mob.getGravity();

        if (nearEdgeTransition) {
            if (surface != Direction.UP) {
                // Wall edge: boost upward to clear the wall top.
                mob.setDeltaMovement(mob.getDeltaMovement().add(0, gravity, 0));
            }

            // Ceiling edge: no boost, no sticking force — momentum carries the entity past the edge.
            return;
        }

        mob.setDeltaMovement(
            mob.getDeltaMovement()
                .add(
                    normal.x * gravity,
                    normal.y * gravity,
                    normal.z * gravity
                )
        );
    }

    /**
     * Decomposes the entity's velocity into tangential (along surface) and orthogonal (into/away from surface)
     * components, applying different drag rates to each.
     */
    private static void applySurfaceRelativeFriction(Mob mob, Direction surface) {
        var awayFromSurface = new Vec3(
            -surface.getStepX(),
            -surface.getStepY(),
            -surface.getStepZ()
        );

        var motion = mob.getDeltaMovement();
        var orthogonalScale = awayFromSurface.dot(motion);
        var orthogonal = awayFromSurface.scale(orthogonalScale);
        var tangential = motion.subtract(orthogonal);

        mob.setDeltaMovement(
            tangential.x * TANGENTIAL_DRAG + orthogonal.x * ORTHOGONAL_DRAG,
            tangential.y * TANGENTIAL_DRAG + orthogonal.y * ORTHOGONAL_DRAG,
            tangential.z * TANGENTIAL_DRAG + orthogonal.z * ORTHOGONAL_DRAG
        );
    }
}
