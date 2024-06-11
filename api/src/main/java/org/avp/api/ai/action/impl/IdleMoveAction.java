package org.avp.api.ai.action.impl;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import org.avp.api.ai.action.CooldownAction;

public class IdleMoveAction extends CooldownAction {

    private final PathfinderMob pathfinderMob;

    public IdleMoveAction(PathfinderMob pathfinderMob) {
        super(120, pathfinderMob.getRandom(), true);
        this.pathfinderMob = pathfinderMob;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
            pathfinderMob.onGround() &&
            pathfinderMob.getNavigation().isDone();
    }

    @Override
    public void execute() {
        var pos = LandRandomPos.getPos(this.pathfinderMob, 10, 7);

        if (pos != null) {
            pathfinderMob.getNavigation().moveTo(pos.x, pos.y, pos.z, 0.8);
            resetCooldown();
        }
    }

    @Override
    public void onComplete() {
        super.onComplete();
        pathfinderMob.getNavigation().stop();
    }
}
