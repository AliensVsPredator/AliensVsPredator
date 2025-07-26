package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.model.alien.HatchState;
import com.lib.common.gameplay.goap.GOAPAction;
import com.lib.common.gameplay.goap.condition.expression.GOAPExpression;
import com.lib.common.gameplay.goap.effect.GOAPEffect;
import com.lib.common.gameplay.goap.state.GOAPBlackboard;
import com.lib.common.gameplay.goap.state.GOAPWorldState;

public class HatchAction extends GOAPAction<Ovomorph> {

    public HatchAction() {
        addPrecondition(OvomorphGOAP.WANTS_TO_HATCH, GOAPExpression.isTrue());
        addPrecondition(OvomorphGOAP.HATCH_STATE, GOAPExpression.doesNotEqual(HatchState.HATCHED));

        addEffect(new GOAPEffect.Value<>(OvomorphGOAP.HATCH_STATE, HatchState.HATCHED));
    }

    @Override
    public boolean perform(Ovomorph ovomorph, GOAPWorldState worldState, GOAPBlackboard blackboard) {
        var hatchManager = ovomorph.getHatchManager();

        hatchManager.hatch();

        return hatchManager.isHatched();
    }
}
