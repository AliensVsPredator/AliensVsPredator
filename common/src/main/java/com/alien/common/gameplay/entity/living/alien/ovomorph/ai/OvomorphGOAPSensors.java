package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.model.alien.HatchState;
import com.just.goap.sensor.Sensor;

public class OvomorphGOAPSensors {

    static final Sensor.Mono<Ovomorph, HatchState> HATCH_STATE =
        Sensor.map(OvomorphGOAPStateKeys.HATCH_STATE, ovomorph -> ovomorph.getHatchState().unwrapOr(Ovomorph.DEFAULT_HATCH_STATE));

    static final Sensor.Mono<Ovomorph, Boolean> WANTS_TO_HATCH =
        Sensor.map(OvomorphGOAPStateKeys.WANTS_TO_HATCH, ovomorph -> ovomorph.getHatchManager().getHatchDesireManager().wantsToHatch());

    private OvomorphGOAPSensors() {
        throw new UnsupportedOperationException();
    }
}
