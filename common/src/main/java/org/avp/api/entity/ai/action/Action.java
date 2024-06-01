package org.avp.api.entity.ai.action;

public abstract class Action {
    /**
     * The cost to execute this action. Lower is better.
     */
    public abstract int getCost();
    public abstract boolean isValid();
    public abstract void execute();

    public void tick() {}

    public void onComplete() {}
}
