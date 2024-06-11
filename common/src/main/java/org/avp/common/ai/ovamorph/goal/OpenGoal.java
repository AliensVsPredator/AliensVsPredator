package org.avp.common.ai.ovamorph.goal;

import org.avp.api.ai.progress.ProgressKey;
import org.avp.api.ai.goal.Goal;
import org.avp.common.ai.ovamorph.action.OpenAction;
import org.avp.common.game.entity.AbstractOvamorph;
import org.avp.common.ai.AVPProgressions;

import java.util.Optional;

public class OpenGoal extends Goal {

    private final AbstractOvamorph ovamorph;

    public OpenGoal(AbstractOvamorph ovamorph) {
        this.ovamorph = ovamorph;
        availableActions.add(new OpenAction(ovamorph));
    }

    @Override
    public boolean isValid() {
        return !ovamorph.isOpening.get() && !ovamorph.isOpen.get();
    }

    @Override
    public boolean isCompleted() {
        return ovamorph.isOpening.get() || ovamorph.isOpen.get();
    }

    @Override
    protected Optional<ProgressKey> createProgresses() {
        return Optional.of(AVPProgressions.RELEASE_FACEHUGGER);
    }

    @Override
    protected Optional<ProgressKey> createProgressedBy() {
        return Optional.of(AVPProgressions.OPEN);
    }
}
