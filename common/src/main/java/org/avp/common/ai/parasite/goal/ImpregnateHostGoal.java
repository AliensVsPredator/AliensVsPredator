package org.avp.common.ai.parasite.goal;

import java.util.Optional;

import org.avp.api.common.ai.goal.Goal;
import org.avp.api.common.ai.progress.ProgressKey;
import org.avp.api.common.ai.progress.Progressions;
import org.avp.common.ai.parasite.action.ImpregnateHostAction;
import org.avp.common.game.entity.AbstractParasite;

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
