package org.avp.api.entity.ai.action.impl;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import org.avp.api.entity.ai.action.Action;

public class IdleMoveAction extends Action {

    private static final int DEFAULT_INTERVAL = 6 * 20;

    private int idleMoveCooldown;

    private final PathfinderMob pathfinderMob;

    public IdleMoveAction(PathfinderMob pathfinderMob) {
        this.pathfinderMob = pathfinderMob;
        this.idleMoveCooldown = pathfinderMob.getRandom().nextInt(DEFAULT_INTERVAL);
    }

    @Override
    public int getCost() {
        return idleMoveCooldown;
    }

    @Override
    public void execute() {
        if (idleMoveCooldown > 0) {
            idleMoveCooldown--;
            return;
        }

        var pos = LandRandomPos.getPos(this.pathfinderMob, 10, 7);

        if (pos != null) {
            pathfinderMob.getNavigation().moveTo(pos.x, pos.y, pos.z, 1);
            idleMoveCooldown = pathfinderMob.getRandom().nextInt(DEFAULT_INTERVAL);
        }
    }
}
