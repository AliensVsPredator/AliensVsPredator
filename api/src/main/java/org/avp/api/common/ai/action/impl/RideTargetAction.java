package org.avp.api.common.ai.action.impl;

import net.minecraft.world.entity.Mob;
import org.avp.api.common.ai.CostConstraint;
import org.avp.api.common.ai.action.Action;

import java.util.Objects;

public class RideTargetAction extends Action {

    private final Mob mob;

    public RideTargetAction(Mob mob) {
        this.mob = mob;
    }

    @Override
    public CostConstraint createCostConstraint() {
        return CostConstraint.DEFAULT;
    }

    @Override
    public boolean isValid() {
        return mob.getTarget() != null && mob.getTarget().getPassengers().isEmpty();
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        var target = Objects.requireNonNull(mob.getTarget());
        mob.startRiding(target);
        // TODO: Send out a packet to tell the clients that the target entity is now being ridden.
    }
}
