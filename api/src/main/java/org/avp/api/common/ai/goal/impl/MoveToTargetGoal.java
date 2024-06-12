package org.avp.api.common.ai.goal.impl;

import net.minecraft.world.entity.Mob;

import java.util.Optional;

import org.avp.api.common.ai.action.impl.MoveToTargetAction;
import org.avp.api.common.ai.goal.Goal;
import org.avp.api.common.ai.progress.ProgressKey;
import org.avp.api.common.ai.progress.Progressions;

public class MoveToTargetGoal extends Goal {

    private final Mob mob;

    public MoveToTargetGoal(Mob mob) {
        super();
        this.mob = mob;
        availableActions.add(new MoveToTargetAction(mob));
    }

    @Override
    public boolean isValid() {
        return mob.getTarget() != null &&
            mob.getVehicle() == null &&
            !isCloseEnoughToTarget();
    }

    @Override
    public boolean isCompleted() {
        return isCloseEnoughToTarget();
    }

    private boolean isCloseEnoughToTarget() {
        var target = mob.getTarget();

        if (target == null) {
            return false;
        }

        return mob.distanceTo(target) < 2 || mob.getVehicle() == target;
    }

    @Override
    public Optional<ProgressKey> createProgresses() {
        return Optional.of(Progressions.MOVE_TO_TARGET);
    }

    @Override
    public Optional<ProgressKey> createProgressedBy() {
        return Optional.empty();
    }
}
