package org.avp.common.ai.parasite.goal;

import org.avp.api.common.ai.progress.ProgressKey;
import org.avp.api.common.ai.goal.Goal;
import org.avp.common.game.entity.AbstractParasite;
import org.avp.common.ai.parasite.action.SmotherHostAction;

import java.util.Optional;

public class SmotherHostGoal extends Goal {

    private final AbstractParasite parasite;

    public SmotherHostGoal(AbstractParasite parasite, int smotherTimeInTicks) {
        this.parasite = parasite;
        availableActions.add(new SmotherHostAction(parasite, smotherTimeInTicks));
    }

    @Override
    public boolean isValid() {
        return parasite.getVehicle() != null && !parasite.isFertile();
    }

    @Override
    public boolean isCompleted() {
        return parasite.getVehicle() == null;
    }

    @Override
    public Optional<ProgressKey> createProgresses() {
        return Optional.empty();
    }

    @Override
    public Optional<ProgressKey> createProgressedBy() {
        return Optional.empty();
    }
}
