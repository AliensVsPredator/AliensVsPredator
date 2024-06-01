package org.avp.common.entity.ai.parasite.goal.infertile;

import org.avp.api.entity.ai.ProgressKey;
import org.avp.api.entity.ai.goal.Goal;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.entity.ai.parasite.action.infertile.DetachFromHostAction;

import java.util.Optional;
import java.util.Set;

public class DetachFromHostGoal extends Goal {

    private final AVPAbstractParasite parasite;

    public DetachFromHostGoal(AVPAbstractParasite parasite, int smotherTimeInTicks) {
        super(
            Set.of(
                new DetachFromHostAction(parasite, smotherTimeInTicks)
            )
        );
        this.parasite = parasite;
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
