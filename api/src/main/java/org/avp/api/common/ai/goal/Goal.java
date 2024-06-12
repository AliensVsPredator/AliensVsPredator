package org.avp.api.common.ai.goal;

import org.avp.api.common.ai.progress.ProgressKey;
import org.avp.api.common.ai.action.Action;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class Goal {

    private final Optional<ProgressKey> progresses = createProgresses();

    private final Optional<ProgressKey> progressedBy = createProgressedBy();

    protected final Set<Action> availableActions;

    protected Goal() {
        this.availableActions = new HashSet<>();
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

    public final Goal addAction(Action action) {
        availableActions.add(action);
        return this;
    }

    public void tick() {
        availableActions.forEach(Action::tick);
    }

    public void onComplete() {
        availableActions.forEach(Action::onComplete);
    }

    public Optional<Action> getBestAction() {
        return availableActions.stream()
            .filter(Action::isValid)
            .min(Comparator.comparingInt(
                action -> action.getCostConstraint().apply(action.getCost())
            ));
    }

    public final Optional<ProgressKey> getProgresses() {
        return progresses;
    }

    public final Optional<ProgressKey> getProgressedBy() {
        return progressedBy;
    }
}
