package org.avp.common.entity.ai.ovamorph.goal;

import org.avp.api.entity.ai.progress.ProgressKey;
import org.avp.api.entity.ai.goal.Goal;
import org.avp.common.entity.AVPAbstractOvamorph;
import org.avp.common.entity.ai.AVPProgressions;
import org.avp.common.entity.ai.ovamorph.action.DisturbAction;

import java.util.Optional;

public class DisturbedGoal extends Goal {

    private final AVPAbstractOvamorph ovamorph;

    public DisturbedGoal(AVPAbstractOvamorph ovamorph) {
        this.ovamorph = ovamorph;
        availableActions.add(new DisturbAction(ovamorph));
    }

    @Override
    public boolean isValid() {
        return ovamorph.getTarget() != null;
    }

    @Override
    public boolean isCompleted() {
        return ovamorph.isFullyDisturbed();
    }

    @Override
    protected Optional<ProgressKey> createProgresses() {
        return Optional.of(AVPProgressions.OPEN);
    }

    @Override
    protected Optional<ProgressKey> createProgressedBy() {
        return Optional.empty();
    }
}
