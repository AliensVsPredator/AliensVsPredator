package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.model.alien.HatchState;
import com.just.goap.Action;
import com.just.goap.condition.expression.Expressions;

public class OvomorphGOAPActions {

    static final Action<Ovomorph> HATCH = Action.<Ovomorph>builder("HatchAction")
        .addPrecondition(OvomorphGOAPKeys.WANTS_TO_HATCH, Expressions.Boolean.isTrue())
        .addPrecondition(OvomorphGOAPKeys.HATCH_STATE, Expressions.Compare.doesNotEqual(HatchState.HATCHED))
        .addEffect(OvomorphGOAPKeys.HATCH_STATE, HatchState.HATCHED)
        .withPerformPredicate((ovomorph, $2, $3) -> {
            ovomorph.getHatchManager().hatch();
            return ovomorph.getHatchManager().isHatched();
        })
        .build();

    private OvomorphGOAPActions() {
        throw new UnsupportedOperationException();
    }
}
