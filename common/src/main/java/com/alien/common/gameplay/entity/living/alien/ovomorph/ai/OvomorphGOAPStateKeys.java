package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.model.alien.HatchState;
import com.just.goap.StateKey;

public class OvomorphGOAPStateKeys {

    static final StateKey.Sensed<HatchState> HATCH_STATE = StateKey.sensed("hatch_state");

    static final StateKey.Sensed<Boolean> WANTS_TO_HATCH = StateKey.sensed("wants_to_hatch");

    private OvomorphGOAPStateKeys() {
        throw new UnsupportedOperationException();
    }
}
