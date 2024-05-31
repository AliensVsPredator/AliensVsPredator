package org.avp.api.entity.ai.goal.impl;

import net.minecraft.world.entity.PathfinderMob;
import org.avp.api.entity.ai.ProgressKey;
import org.avp.api.entity.ai.action.impl.IdleMoveAction;
import org.avp.api.entity.ai.goal.Goal;

import java.util.Optional;
import java.util.Set;

public class IdleMoveGoal extends Goal {

    protected final PathfinderMob pathfinderMob;

    public IdleMoveGoal(PathfinderMob pathfinderMob) {
        super(
            Set.of(
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
        return true;
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
