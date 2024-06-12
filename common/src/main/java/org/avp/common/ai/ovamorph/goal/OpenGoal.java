package org.avp.common.ai.ovamorph.goal;

import java.util.Optional;

import org.avp.api.common.ai.goal.Goal;
import org.avp.api.common.ai.progress.ProgressKey;
import org.avp.common.ai.AVPProgressions;
import org.avp.common.ai.ovamorph.action.OpenAction;
import org.avp.common.game.entity.AbstractOvamorph;

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
