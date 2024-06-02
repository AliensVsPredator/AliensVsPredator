package org.avp.common.entity.ai.parasite.action.fertile;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.avp.api.entity.ai.action.Action;
import org.avp.api.entity.ai.action.CooldownAction;
import org.avp.common.entity.AVPAbstractParasite;

import java.util.Objects;

public class LeapTowardsHostAction extends CooldownAction {

    private static final int DEFAULT_WIND_UP_TIME_IN_TICKS = 20;

    private static final float DEFAULT_DISTANCE_TARGET = -1F;

    private static final int MAX_LEAP_DISTANCE = 12;

    private static final int MIN_LEAP_DISTANCE = 8;

    private int windUpTimeInTicks;

    private float distanceToTarget;

    private final AVPAbstractParasite parasite;

    public LeapTowardsHostAction(AVPAbstractParasite parasite) {
        super(100, parasite.getRandom(), false);
        this.parasite = parasite;
        resetWindUpTimeInTicks();
        distanceToTarget = DEFAULT_DISTANCE_TARGET;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && parasite.getTarget() != null && parasite.onGround();
    }

    @Override
    public int getCost() {
        var target = Objects.requireNonNull(parasite.getTarget());
        var distanceToHost = parasite.distanceTo(target);

        if (distanceToHost > MAX_LEAP_DISTANCE || distanceToHost < MIN_LEAP_DISTANCE) {
            return Integer.MAX_VALUE;
        }

        return (int) Mth.map(distanceToHost, MIN_LEAP_DISTANCE, MAX_LEAP_DISTANCE, 0F, 100F);
    }

    @Override
    public void execute() {
        var target = Objects.requireNonNull(parasite.getTarget());
        var currentDistanceToTarget = parasite.distanceTo(target);

        if (distanceToTarget == DEFAULT_DISTANCE_TARGET) {
            distanceToTarget = currentDistanceToTarget;
        }

        parasite.getLookControl().setLookAt(target, 30.0F, 30.0F);

        // If the mob was hurt,
        // Or if the target moved further away,
        // Then leap immediately.
        if (
            (parasite.getLastHurtByMobTimestamp() > 0 && parasite.tickCount - parasite.getLastHurtByMobTimestamp() < 20) ||
            currentDistanceToTarget > distanceToTarget
        ) {
            windUpTimeInTicks = 0;
        }

        if (windUpTimeInTicks > 0) {
            windUpTimeInTicks--;
            parasite.getNavigation().stop();
            return;
        }

        distanceToTarget = currentDistanceToTarget;

        Vec3 deltaMovement = parasite.getDeltaMovement();
        Vec3 vectorDifference = new Vec3(target.getX() - parasite.getX(), 0.0, target.getZ() - parasite.getZ());

        vectorDifference = vectorDifference.normalize()
            .scale(0.2 * distanceToTarget)
            .add(deltaMovement.scale(0.2));

        parasite.setDeltaMovement(vectorDifference.x, 0.6, vectorDifference.z);

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
}
