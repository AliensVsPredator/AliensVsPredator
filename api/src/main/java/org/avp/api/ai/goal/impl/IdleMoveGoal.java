package org.avp.api.ai.goal.impl;

import net.minecraft.world.entity.PathfinderMob;
import org.avp.api.ai.action.impl.IdleMoveAction;
import org.avp.api.ai.progress.ProgressKey;
import org.avp.api.ai.action.impl.IdleLookAroundAction;
import org.avp.api.ai.goal.Goal;

import java.util.Optional;

public class IdleMoveGoal<T extends PathfinderMob> extends Goal {

    protected final T pathfinderMob;

    public IdleMoveGoal(T pathfinderMob) {
        this.pathfinderMob = pathfinderMob;
        availableActions.add(new IdleLookAroundAction(pathfinderMob));
        availableActions.add(new IdleMoveAction(pathfinderMob));
    }

    @Override
    public boolean isValid() {
        return pathfinderMob.getTarget() == null && pathfinderMob.getVehicle() == null;
    }

    @Override
    public boolean isCompleted() {
        // This task has no definitive end.
        return false;
    }

    @Override
    protected Optional<ProgressKey> createProgresses() {
        return Optional.empty();
    }

    @Override
    protected Optional<ProgressKey> createProgressedBy() {
        return Optional.empty();
    }
}
