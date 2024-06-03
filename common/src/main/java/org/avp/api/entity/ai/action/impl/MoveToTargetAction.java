package org.avp.api.entity.ai.action.impl;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import org.avp.api.entity.ai.action.Action;

import java.util.Objects;

public class MoveToTargetAction extends Action {

    private final Mob mob;

    public MoveToTargetAction(Mob mob) {
        this.mob = mob;
    }

    @Override
    public boolean isValid() {
        return mob.getTarget() != null && mob.onGround();
    }

    @Override
    public int getCost() {
        var target = Objects.requireNonNull(mob.getTarget());
        var distanceToHost = mob.distanceTo(target);
        return (int) Mth.map(distanceToHost, 0F, 16F, 0F, 100F);
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
