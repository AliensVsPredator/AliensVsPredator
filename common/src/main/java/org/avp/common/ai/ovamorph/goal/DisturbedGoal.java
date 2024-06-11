package org.avp.common.ai.ovamorph.goal;

import org.avp.api.ai.progress.ProgressKey;
import org.avp.api.ai.goal.Goal;
import org.avp.common.ai.ovamorph.action.DisturbAction;
import org.avp.common.game.entity.AbstractOvamorph;
import org.avp.common.ai.AVPProgressions;

import java.util.Optional;

public class DisturbedGoal extends Goal {

    private final AbstractOvamorph ovamorph;

    public DisturbedGoal(AbstractOvamorph ovamorph) {
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
