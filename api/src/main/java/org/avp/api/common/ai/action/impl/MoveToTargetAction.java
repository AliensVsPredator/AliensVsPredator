package org.avp.api.common.ai.action.impl;

import net.minecraft.world.entity.Mob;
import org.avp.api.common.ai.CostConstraint;
import org.avp.api.common.ai.action.Action;

import java.util.Objects;

public class MoveToTargetAction extends Action {

    private final Mob mob;

    public MoveToTargetAction(Mob mob) {
        this.mob = mob;
    }

    @Override
    public CostConstraint createCostConstraint() {
        return new CostConstraint(0, 16);
    }

    @Override
    public boolean isValid() {
        return mob.getTarget() != null && mob.onGround();
    }

    @Override
    public int getCost() {
        var target = Objects.requireNonNull(mob.getTarget());
        var distanceToHost = mob.distanceTo(target);
        return (int) distanceToHost;
    }

    @Override
    public void execute() {
        var target = Objects.requireNonNull(mob.getTarget());
        mob.getNavigation().moveTo(target, 1);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        mob.getNavigation().stop();
    }
}
