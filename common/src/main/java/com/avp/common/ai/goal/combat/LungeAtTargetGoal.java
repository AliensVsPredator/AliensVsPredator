package com.avp.common.ai.goal.combat;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class LungeAtTargetGoal extends Goal {

    private static final int DEFAULT_WIND_UP_TIME_IN_TICKS = 10;

    private static final float DEFAULT_DISTANCE_TARGET = -1F;

    private final Mob mob;

    private final int maxCooldown;

    private final float normalizedChance;

    private final double minLungeRange;

    private final double maxLungeRange;

    private float distanceToTarget;

    private int windUpTimeInTicks;

    private int cooldown;

    @Nullable
    Runnable onLungeCallback;

    public LungeAtTargetGoal(Mob mob, float normalizedChance, int cooldown, double minLungeRange, double maxLungeRange) {
        this.mob = mob;
        this.maxCooldown = cooldown;
        this.normalizedChance = normalizedChance;
        this.minLungeRange = minLungeRange;
        this.maxLungeRange = maxLungeRange;
        this.cooldown = 0;

        resetWindUpTimeInTicks();
        distanceToTarget = DEFAULT_DISTANCE_TARGET;

        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        cooldown = Math.max(cooldown - 1, 0);

        var canUse = !isOnCooldown() && mob.getRandom().nextFloat() < normalizedChance;

        // Target is not null (duh).
        return mob.getTarget() != null
            // AND on solid ground, we can't lunge off of nothing.
            && mob.onGround()
            // AND is within range to lunge at the target.
            && isInRange()
            // AND cooldown has passed.
            && canUse
            // AND has line of sight, we need to see what we want to lunge at.
            && mob.getSensing().hasLineOfSight(mob.getTarget());
    }

    @Override
    public boolean canContinueToUse() {
        // Target is not null (duh).
        return mob.getTarget() != null
            // AND on solid ground, we can't lunge off of nothing.
            && mob.onGround()
            // AND has line of sight, we need to see what we're lunging at.
            && mob.getSensing().hasLineOfSight(mob.getTarget());
    }

    @Override
    public void start() {
        super.start();

        var target = mob.getTarget();

        if (target != null) {
            mob.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            mob.getLookControl().setLookAt(target);
        }
    }

    @Override
    public void tick() {
        var target = mob.getTarget();

        if (target == null) {
            return;
        }

        mob.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        mob.getLookControl().setLookAt(target);

        var currentDistanceToTarget = mob.distanceTo(target);

        if (distanceToTarget == DEFAULT_DISTANCE_TARGET) {
            distanceToTarget = currentDistanceToTarget;
        }

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

        var deltaMovement = mob.getDeltaMovement().scale(0.2);
        var vectorDifference = target.getEyePosition().subtract(mob.getEyePosition());

        vectorDifference = vectorDifference.normalize()
            .scale(0.2 * distanceToTarget)
            .add(deltaMovement.x, 0, deltaMovement.z);

        // 0.6 seems to be a good minimum value for lunging towards the target's upper half.
        mob.setDeltaMovement(vectorDifference.x, Math.max(0.6, vectorDifference.y), vectorDifference.z);

        if (onLungeCallback != null) {
            onLungeCallback.run();
        }

        resetWindUpTimeInTicks();
        resetCooldown();
    }

    @Override
    public void stop() {
        super.stop();
        resetCooldown();
        resetWindUpTimeInTicks();
        distanceToTarget = DEFAULT_DISTANCE_TARGET;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public LungeAtTargetGoal setOnLungeCallback(@Nullable Runnable onLungeCallback) {
        this.onLungeCallback = onLungeCallback;
        return this;
    }

    private boolean isOnCooldown() {
        return cooldown > 0;
    }

    private void resetCooldown() {
        cooldown = maxCooldown;
    }

    private void resetWindUpTimeInTicks() {
        this.windUpTimeInTicks = DEFAULT_WIND_UP_TIME_IN_TICKS;
    }

    private boolean isInRange() {
        var target = mob.getTarget();

        if (target == null) {
            return false;
        }

        // Compute squared horizontal distance (ignore Y-axis).
        var dx = mob.getX() - target.getX();
        var dz = mob.getZ() - target.getZ();
        var horizontalDistanceSqr = dx * dx + dz * dz;

        // TODO: Store these instead of constantly recomputing.
        var minimumRangeSquared = minLungeRange * minLungeRange;
        var maximumRangeSquared = maxLungeRange * maxLungeRange;

        // Always reject targets too far horizontally.
        var isTargetTooFarAwayHorizontally = horizontalDistanceSqr > maximumRangeSquared;

        if (isTargetTooFarAwayHorizontally) {
            return false;
        }

        // Vertical distance (Y only).
        var dy = Math.abs(mob.getY() - target.getY());
        var verticalDistanceSqr = dy * dy;

        var isTargetTooFarAwayVertically = verticalDistanceSqr > maximumRangeSquared;

        if (isTargetTooFarAwayVertically) {
            return false;
        }

        var minVerticalLungeRange = mob.getBbHeight() * mob.getBbHeight();

        var isTargetTooCloseHorizontally = horizontalDistanceSqr < minimumRangeSquared;
        var isTargetTooCloseVertically = dy <= minVerticalLungeRange;

        // Allow targets outside minimum horizontal range if they are far enough vertically.
        return !isTargetTooCloseHorizontally || !isTargetTooCloseVertically;
    }
}
