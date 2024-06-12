package org.avp.common.ai.ovamorph.goal;

import org.avp.api.common.ai.progress.ProgressKey;
import org.avp.api.common.ai.goal.Goal;
import org.avp.common.ai.ovamorph.action.ReleaseFacehuggerAction;
import org.avp.common.game.entity.AbstractOvamorph;
import org.avp.common.ai.AVPProgressions;

import java.util.Optional;

public class ReleaseFacehuggerGoal extends Goal {

    private final AbstractOvamorph ovamorph;

    public ReleaseFacehuggerGoal(AbstractOvamorph ovamorph) {
        this.ovamorph = ovamorph;
        availableActions.add(new ReleaseFacehuggerAction(ovamorph));
    }

    @Override
    public boolean isValid() {
        return ovamorph.hasFacehugger();
    }

    @Override
    public boolean isCompleted() {
        return !ovamorph.hasFacehugger();
    }

    @Override
    protected Optional<ProgressKey> createProgresses() {
        return Optional.empty();
    }

    @Override
    protected Optional<ProgressKey> createProgressedBy() {
        return Optional.of(AVPProgressions.RELEASE_FACEHUGGER);
    }
}
