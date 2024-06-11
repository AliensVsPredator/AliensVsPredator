package org.avp.api.ai.goal.impl;

import net.minecraft.world.entity.Mob;
import org.avp.api.ai.goal.Goal;
import org.avp.api.ai.progress.ProgressKey;
import org.avp.api.ai.progress.Progressions;
import org.avp.api.ai.action.impl.RideTargetAction;

import java.util.Optional;

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
