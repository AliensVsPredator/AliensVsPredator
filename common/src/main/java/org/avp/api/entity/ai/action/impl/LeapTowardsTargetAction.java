package org.avp.api.entity.ai.action.impl;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.avp.api.entity.ai.action.CooldownAction;

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
        return (int) Mth.map(distanceToHost, MIN_LEAP_DISTANCE, MAX_LEAP_DISTANCE, 0F, 100F);
    }

    @Override
    public void execute() {
        var target = Objects.requireNonNull(mob.getTarget());
        var currentDistanceToTarget = mob.distanceTo(target);

        if (distanceToTarget == DEFAULT_DISTANCE_TARGET) {
            distanceToTarget = currentDistanceToTarget;
        }

        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

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

        Vec3 deltaMovement = mob.getDeltaMovement();
        Vec3 vectorDifference = new Vec3(target.getX() - mob.getX(), target.getY() - mob.getY(), target.getZ() - mob.getZ());

        vectorDifference = vectorDifference.normalize()
            .scale(0.2 * distanceToTarget)
            .add(deltaMovement.scale(0.2));

        mob.setDeltaMovement(vectorDifference.x, vectorDifference.y, vectorDifference.z);

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
