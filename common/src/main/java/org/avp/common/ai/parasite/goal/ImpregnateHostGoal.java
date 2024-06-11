package org.avp.common.ai.parasite.goal;

import org.avp.api.ai.progress.Progressions;
import org.avp.api.ai.goal.Goal;
import org.avp.api.ai.progress.ProgressKey;
import org.avp.common.ai.parasite.action.ImpregnateHostAction;
import org.avp.common.game.entity.AbstractParasite;

import java.util.Optional;

public class ImpregnateHostGoal extends Goal {

    private final AbstractParasite parasite;

    public ImpregnateHostGoal(AbstractParasite parasite) {
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
