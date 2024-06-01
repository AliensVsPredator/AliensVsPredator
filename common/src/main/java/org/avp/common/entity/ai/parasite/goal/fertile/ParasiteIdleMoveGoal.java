package org.avp.common.entity.ai.parasite.goal.fertile;

import org.avp.api.entity.ai.goal.impl.IdleMoveGoal;
import org.avp.common.entity.AVPAbstractParasite;

public class ParasiteIdleMoveGoal extends IdleMoveGoal<AVPAbstractParasite> {

    public ParasiteIdleMoveGoal(AVPAbstractParasite pathfinderMob) {
        super(pathfinderMob);
    }

    @Override
    public boolean isValid() {
        return super.isValid() && pathfinderMob.isFertile();
    }
}
