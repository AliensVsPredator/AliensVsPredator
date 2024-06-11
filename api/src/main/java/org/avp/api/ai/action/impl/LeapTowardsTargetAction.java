package org.avp.api.ai.action.impl;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.avp.api.ai.CostConstraint;
import org.avp.api.ai.action.CooldownAction;

import java.util.Objects;

public class LeapTowardsTargetAction extends CooldownAction {

    private static final int DEFAULT_WIND_UP_TIME_IN_TICKS = 10;

    private static final float DEFAULT_DISTANCE_TARGET = -1F;

    private static final int MAX_LEAP_DISTANCE = 12;

    private static final int MIN_LEAP_DISTANCE = 8;

    private int windUpTimeInTicks;

    private float distanceToTarget;

    private final Mob mob;

    public LeapTowardsTargetAction(Mob mob) {
        super(100, mob.getRandom(), false);
        this.mob = mob;
        resetWindUpTimeInTicks();
        distanceToTarget = DEFAULT_DISTANCE_TARGET;
    }

    @Override
    public CostConstraint createCostConstraint() {
        return new CostConstraint(MIN_LEAP_DISTANCE, MAX_LEAP_DISTANCE);
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
            mob.getTarget() != null &&
            mob.onGround() &&
            isInLeapingRange();
    }

    @Override
    public int getCost() {
        var target = Objects.requireNonNull(mob.getTarget());
        var distanceToHost = mob.distanceTo(target);
        return (int) distanceToHost;
    }

    @Override
    public void execute() {
        var target = Objects.requireNonNull(mob.getTarget());
        var currentDistanceToTarget = mob.distanceTo(target);

        if (distanceToTarget == DEFAULT_DISTANCE_TARGET) {
            distanceToTarget = currentDistanceToTarget;
        }

        mob.getLookControl().setLookAt(target, 180.0F, 180.0F);

        // If the mob was hurt,
        // Or if the target moved further away,
        // Then leap immediately.
        if (
            (mob.getLastHurtByMobTimestamp() > 0 && mob.tickCount - mob.getLastHurtByMobTimestamp() < 20) ||
            currentDistanceToTarget > distanceToTarget
        ) {
            windUpTimeInTicks = 0;
        }

        if (windUpTimeInTicks > 0) {
            windUpTimeInTicks--;
            mob.getNavigation().stop();
            return;
        }

        distanceToTarget = currentDistanceToTarget;

        var deltaMovement = mob.getDeltaMovement();
        // Target might be on the same y level as the mob.
        var y = Math.max(target.getY() - mob.getY(), target.getBbHeight());
        Vec3 vectorDifference = new Vec3(target.getX() - mob.getX(), y, target.getZ() - mob.getZ());

        vectorDifference = vectorDifference.normalize()
            .scale(0.2 * distanceToTarget)
            .add(deltaMovement.scale(0.2));

        // 0.6 seems to be a good minimum value for leaping towards the target's upper half.
        mob.setDeltaMovement(vectorDifference.x, Math.max(vectorDifference.y, 0.6), vectorDifference.z);

        resetCooldown();
    }

    @Override
    public void onComplete() {
        super.onComplete();
        resetWindUpTimeInTicks();
        distanceToTarget = DEFAULT_DISTANCE_TARGET;
    }

    private void resetWindUpTimeInTicks() {
        this.windUpTimeInTicks = DEFAULT_WIND_UP_TIME_IN_TICKS;
    }

    private boolean isInLeapingRange() {
        var target = mob.getTarget();

        if (target == null) {
            return false;
        }

        var distanceToHost = mob.distanceTo(target);

        return distanceToHost <= MAX_LEAP_DISTANCE && distanceToHost >= MIN_LEAP_DISTANCE;
    }
}
