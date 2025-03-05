package com.avp.common.ai.goal.combat;

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

        return mob.getTarget() != null &&
            mob.onGround() &&
            isInRange() &&
            canUse;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() != null && mob.onGround();
    }

    @Override
    public void start() {
        super.start();

        if (mob.getTarget() != null) {
            mob.getLookControl().setLookAt(mob.getTarget(), 180.0F, 180.0F);
        }
    }

    @Override
    public void tick() {
        if (mob.getTarget() == null) {
            return;
        }

        mob.getLookControl().setLookAt(mob.getTarget(), 180.0F, 180.0F);

        var target = mob.getTarget();
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

        var distanceToHost = mob.distanceToSqr(target);

        var minimumRangeSquared = minLungeRange * minLungeRange;
        var maximumRangeSquared = maxLungeRange * maxLungeRange;

        return distanceToHost <= maximumRangeSquared && distanceToHost >= minimumRangeSquared;
    }
}
