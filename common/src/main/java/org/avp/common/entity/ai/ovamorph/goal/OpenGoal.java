package org.avp.common.entity.ai.ovamorph.goal;

import org.avp.api.entity.ai.ProgressKey;
import org.avp.api.entity.ai.goal.Goal;
import org.avp.common.entity.AVPAbstractOvamorph;
import org.avp.common.entity.ai.AVPProgressions;
import org.avp.common.entity.ai.ovamorph.action.OpenAction;

import java.util.Optional;
import java.util.Set;

public class OpenGoal extends Goal {

    private final AVPAbstractOvamorph ovamorph;

    public OpenGoal(AVPAbstractOvamorph ovamorph) {
        super(
            Set.of(
                new OpenAction(ovamorph)
            )
        );
        this.ovamorph = ovamorph;
    }

    @Override
    public boolean isValid() {
        return !ovamorph.isOpening.get();
    }

    @Override
    public boolean isCompleted() {
        return ovamorph.isOpening.get();
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
