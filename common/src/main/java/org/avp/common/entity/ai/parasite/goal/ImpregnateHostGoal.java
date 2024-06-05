package org.avp.common.entity.ai.parasite.goal;

import org.avp.api.entity.ai.progress.Progressions;
import org.avp.api.entity.ai.goal.Goal;
import org.avp.api.entity.ai.progress.ProgressKey;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.entity.ai.parasite.action.ImpregnateHostAction;

import java.util.Optional;

public class ImpregnateHostGoal extends Goal {

    private final AVPAbstractParasite parasite;

    public ImpregnateHostGoal(AVPAbstractParasite parasite) {
        this.parasite = parasite;
        availableActions.add(new ImpregnateHostAction(parasite));
    }

    @Override
    public boolean isValid() {
        return parasite.isFertile() && parasite.getTarget() != null;
    }

    @Override
    public boolean isCompleted() {
        return !parasite.isFertile();
    }

    @Override
    public Optional<ProgressKey> createProgresses() {
        return Optional.empty();
    }

    @Override
    public Optional<ProgressKey> createProgressedBy() {
        return Optional.of(Progressions.RIDE_TARGET);
    }
}
