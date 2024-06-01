package org.avp.common.entity.ai.parasite.goal.fertile;

import org.avp.api.entity.ai.goal.Goal;
import org.avp.api.entity.ai.ProgressKey;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.entity.ai.AVPProgressions;
import org.avp.common.entity.ai.parasite.action.fertile.AttachToHostAction;

import java.util.Optional;
import java.util.Set;

public class AttachToHostGoal extends Goal {

    private final AVPAbstractParasite parasite;

    public AttachToHostGoal(AVPAbstractParasite parasite) {
        super(
            Set.of(
                new AttachToHostAction(parasite)
            )
        );
        this.parasite = parasite;
    }

    @Override
    public boolean isValid() {
        return parasite.getTarget() != null &&
            parasite.getTarget().getPassengers().isEmpty() &&
            parasite.getVehicle() == null;
    }

    @Override
    public boolean isCompleted() {
        return parasite.getVehicle() != null;
    }

    @Override
    public Optional<ProgressKey> createProgresses() {
        return Optional.of(AVPProgressions.ATTACH_TO_HOST);
    }

    @Override
    public Optional<ProgressKey> createProgressedBy() {
        return Optional.of(AVPProgressions.MOVE_TO_TARGET);
    }
}
