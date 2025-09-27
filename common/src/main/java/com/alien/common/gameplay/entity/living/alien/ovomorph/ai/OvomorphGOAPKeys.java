package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.model.alien.HatchState;
import com.just.goap.GOAPKey;

public class OvomorphGOAPKeys {

    static final GOAPKey<HatchState> HATCH_STATE = new GOAPKey<>("hatch_state");

    static final GOAPKey<Boolean> WANTS_TO_HATCH = new GOAPKey<>("wants_to_hatch");

    private OvomorphGOAPKeys() {
        throw new UnsupportedOperationException();
    }
}
