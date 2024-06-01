package org.avp.api.entity.ai.goal;

import org.avp.api.entity.ai.action.Action;
import org.avp.api.entity.ai.ProgressKey;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

public abstract class Goal {

    private final Optional<ProgressKey> progresses = createProgresses();

    private final Optional<ProgressKey> progressedBy = createProgressedBy();

    private final Set<Action> availableActions;

    protected Goal(Set<Action> availableActions) {
        this.availableActions = availableActions;
    }

    public abstract boolean isValid();

    public abstract boolean isCompleted();

    /**
     * The type of progress that this goal makes.
     */
    protected abstract Optional<ProgressKey> createProgresses();

    /**
     * The type of progress required for this goal to be available.
     */
    protected abstract Optional<ProgressKey> createProgressedBy();

    public void tick() {
        availableActions.forEach(Action::tick);
    }

    public void onComplete() {
        availableActions.forEach(Action::onComplete);
    }

    public Optional<Action> getBestAction() {
        return availableActions.stream()
            .filter(Action::isValid)
            .min(Comparator.comparingInt(Action::getCost));
    }

    public final Optional<ProgressKey> getProgresses() {
        return progresses;
    }

    public final Optional<ProgressKey> getProgressedBy() {
        return progressedBy;
    }
}
