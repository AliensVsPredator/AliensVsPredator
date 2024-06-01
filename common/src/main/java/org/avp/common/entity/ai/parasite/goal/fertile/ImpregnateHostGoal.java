package org.avp.common.entity.ai.parasite.goal.fertile;

import org.avp.api.entity.ai.goal.Goal;
import org.avp.api.entity.ai.ProgressKey;
import org.avp.common.entity.AVPAbstractParasite;
import org.avp.common.entity.ai.AVPProgressions;
import org.avp.common.entity.ai.parasite.action.fertile.ImpregnateHostAction;

import java.util.Optional;
import java.util.Set;

public class ImpregnateHostGoal extends Goal {

    private final AVPAbstractParasite parasite;

    public ImpregnateHostGoal(AVPAbstractParasite parasite) {
        super(
            Set.of(
                new ImpregnateHostAction(parasite)
            )
        );
        this.parasite = parasite;
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
        return Optional.of(AVPProgressions.ATTACH_TO_HOST);
    }
}
