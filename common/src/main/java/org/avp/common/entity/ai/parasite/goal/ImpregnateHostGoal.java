package org.avp.common.entity.ai.parasite.goal;

import net.minecraft.world.entity.monster.Monster;
import org.avp.api.entity.Parasite;
import org.avp.api.entity.ai.goal.Goal;
import org.avp.api.entity.ai.ProgressKey;
import org.avp.common.entity.ai.AVPProgressions;
import org.avp.common.entity.ai.parasite.action.ImpregnateHostAction;

import java.util.Optional;
import java.util.Set;

public class ImpregnateHostGoal extends Goal {

    private final Monster parasite;

    public ImpregnateHostGoal(Monster parasite) {
        super(
            Set.of(
                new ImpregnateHostAction(parasite)
            )
        );
        this.parasite = parasite;
    }

    @Override
    public boolean isValid() {
        return ((Parasite) parasite).isFertile();
    }

    @Override
    public boolean isCompleted() {
        return !((Parasite) parasite).isFertile();
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
