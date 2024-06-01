package org.avp.api.entity.ai.goal.impl;

import net.minecraft.world.entity.PathfinderMob;
import org.avp.api.entity.ai.ProgressKey;
import org.avp.api.entity.ai.action.impl.IdleLookAroundAction;
import org.avp.api.entity.ai.action.impl.IdleMoveAction;
import org.avp.api.entity.ai.goal.Goal;

import java.util.Optional;
import java.util.Set;

public class IdleMoveGoal<T extends PathfinderMob> extends Goal {

    protected final T pathfinderMob;

    public IdleMoveGoal(T pathfinderMob) {
        super(
            Set.of(
                new IdleLookAroundAction(pathfinderMob),
                new IdleMoveAction(pathfinderMob)
            )
        );
        this.pathfinderMob = pathfinderMob;
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
