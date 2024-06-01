package org.avp.common.entity.ai.parasite.goal.fertile;

import org.avp.api.entity.ai.goal.Goal;
import org.avp.api.entity.ai.ProgressKey;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.entity.ai.AVPProgressions;
import org.avp.common.entity.ai.parasite.action.fertile.MoveToHostAction;

import java.util.Optional;
import java.util.Set;

public class MoveToHostGoal extends Goal {

    private final AVPAbstractParasite parasite;

    public MoveToHostGoal(AVPAbstractParasite parasite) {
        super(
            Set.of(
                new MoveToHostAction(parasite)
            )
        );
        this.parasite = parasite;
    }

    @Override
    public boolean isValid() {
        return parasite.getTarget() != null;
    }

    @Override
    public boolean isCompleted() {
        var target = parasite.getTarget();

        if (target == null) {
            return false;
        }

        return parasite.distanceTo(target) < 1 + target.getBbWidth();
    }

    @Override
    public Optional<ProgressKey> createProgresses() {
        return Optional.of(AVPProgressions.MOVE_TO_TARGET);
    }

    @Override
    public Optional<ProgressKey> createProgressedBy() {
        return Optional.empty();
    }
}
