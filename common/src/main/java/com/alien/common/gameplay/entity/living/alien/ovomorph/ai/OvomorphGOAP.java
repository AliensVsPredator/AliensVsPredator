package com.alien.common.gameplay.entity.living.alien.ovomorph.ai;

import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.model.alien.HatchState;
import com.lib.common.gameplay.entity.ai.EntityGOAP;
import com.lib.common.gameplay.goap.TypedIdentifier;
import com.lib.common.gameplay.goap.state.GOAPMutableWorldState;

public class OvomorphGOAP extends EntityGOAP<Ovomorph> {

    public static final TypedIdentifier<HatchState> HATCH_STATE = new TypedIdentifier<>("hatchState");

    public static final TypedIdentifier<Boolean> WANTS_TO_HATCH = new TypedIdentifier<>("wantsToHatch");

    public OvomorphGOAP(Ovomorph ovomorph) {
        addBaseRoutines();
    }

    @Override
    public void update(Ovomorph context) {
        if (context.getHatchManager().isHatched()) {
            // Don't run GOAP planning if the ovomorph is hatched.
            return;
        }

        super.update(context);
    }

    public void addBaseRoutines() {
        // Track the ovomorph's desire to hatch.
        addSensor(this::senseHatchDesireState);
        // Track the ovomorph's hatch state.
        addSensor(this::senseHatchState);

        // The desired goal of all ovomorphs is to hatch.
        addGoal(new HatchGoal());

        // How can the ovomorph hatch?
        addAction(new HatchAction());
    }

    private void senseHatchDesireState(Ovomorph ovomorph, GOAPMutableWorldState worldState) {
        worldState.set(OvomorphGOAP.WANTS_TO_HATCH, ovomorph.getHatchManager().getHatchDesireManager().wantsToHatch());
    }

    private void senseHatchState(Ovomorph ovomorph, GOAPMutableWorldState worldState) {
        worldState.set(OvomorphGOAP.HATCH_STATE, ovomorph.getHatchState().unwrapOr(Ovomorph.DEFAULT_HATCH_STATE));
    }
}
