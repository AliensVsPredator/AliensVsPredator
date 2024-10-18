package org.avp.common.ai.parasite.goal;

import org.avp.api.common.ai.goal.impl.IdleMoveGoal;
import org.avp.common.game.entity.AbstractParasite;

public class ParasiteIdleMoveGoal extends IdleMoveGoal<AbstractParasite> {

    public ParasiteIdleMoveGoal(AbstractParasite pathfinderMob) {
        super(pathfinderMob);
    }

    @Override
    public boolean isValid() {
        return super.isValid() && pathfinderMob.isFertile();
    }
}
