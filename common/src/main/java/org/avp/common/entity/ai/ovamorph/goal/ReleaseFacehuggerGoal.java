package org.avp.common.entity.ai.ovamorph.goal;

import org.avp.api.entity.ai.ProgressKey;
import org.avp.api.entity.ai.goal.Goal;
import org.avp.common.entity.AVPAbstractOvamorph;
import org.avp.common.entity.ai.AVPProgressions;
import org.avp.common.entity.ai.ovamorph.action.ReleaseFacehuggerAction;

import java.util.Optional;
import java.util.Set;

public class ReleaseFacehuggerGoal extends Goal {

    private final AVPAbstractOvamorph ovamorph;

    public ReleaseFacehuggerGoal(AVPAbstractOvamorph ovamorph) {
        super(
            Set.of(
                new ReleaseFacehuggerAction(ovamorph)
            )
        );
        this.ovamorph = ovamorph;
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
