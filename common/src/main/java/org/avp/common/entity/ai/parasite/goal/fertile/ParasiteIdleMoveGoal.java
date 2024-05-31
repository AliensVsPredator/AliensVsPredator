package org.avp.common.entity.ai.parasite.goal.fertile;

import net.minecraft.world.entity.PathfinderMob;
import org.avp.api.entity.Parasite;
import org.avp.api.entity.ai.goal.impl.IdleMoveGoal;

public class ParasiteIdleMoveGoal extends IdleMoveGoal {

    public ParasiteIdleMoveGoal(PathfinderMob pathfinderMob) {
        super(pathfinderMob);
    }

    @Override
    public boolean isValid() {
        return super.isValid() && ((Parasite) pathfinderMob).isFertile();
    }
}
