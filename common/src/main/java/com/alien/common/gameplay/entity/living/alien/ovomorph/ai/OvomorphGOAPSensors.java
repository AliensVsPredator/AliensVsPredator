package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.model.alien.HatchState;
import com.just.goap.Sensor;

public class OvomorphGOAPSensors {

    static final Sensor<Ovomorph, HatchState> HATCH_STATE =
        Sensor.direct(OvomorphGOAPKeys.HATCH_STATE, ovomorph -> ovomorph.getHatchState().unwrapOr(Ovomorph.DEFAULT_HATCH_STATE));

    static final Sensor<Ovomorph, Boolean> WANTS_TO_HATCH =
        Sensor.direct(OvomorphGOAPKeys.WANTS_TO_HATCH, ovomorph -> ovomorph.getHatchManager().getHatchDesireManager().wantsToHatch());

    private OvomorphGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
