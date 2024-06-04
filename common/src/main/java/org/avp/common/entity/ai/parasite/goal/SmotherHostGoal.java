package org.avp.common.entity.ai.parasite.goal;

import org.avp.api.entity.ai.ProgressKey;
import org.avp.api.entity.ai.goal.Goal;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.entity.ai.parasite.action.SmotherHostAction;

import java.util.Optional;

public class SmotherHostGoal extends Goal {

    private final AVPAbstractParasite parasite;

    public SmotherHostGoal(AVPAbstractParasite parasite, int smotherTimeInTicks) {
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
