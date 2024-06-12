package org.avp.api.common.ai.goal.impl;

import net.minecraft.world.entity.Mob;

import java.util.Optional;

import org.avp.api.common.ai.action.impl.RideTargetAction;
import org.avp.api.common.ai.goal.Goal;
import org.avp.api.common.ai.progress.ProgressKey;
import org.avp.api.common.ai.progress.Progressions;

public class RideTargetGoal extends Goal {

    private final Mob mob;

    public RideTargetGoal(Mob mob) {
        this.mob = mob;
        availableActions.add(new RideTargetAction(mob));
    }

    @Override
    public boolean isValid() {
        return mob.getTarget() != null &&
            mob.getTarget().getPassengers().isEmpty() &&
            mob.getVehicle() == null;
    }

    @Override
    public boolean isCompleted() {
        return mob.getVehicle() != null;
    }

    @Override
    public Optional<ProgressKey> createProgresses() {
        return Optional.of(Progressions.RIDE_TARGET);
    }

    @Override
    public Optional<ProgressKey> createProgressedBy() {
        return Optional.of(Progressions.MOVE_TO_TARGET);
    }
}
