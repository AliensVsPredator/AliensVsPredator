package org.avp.api.entity.ai.action;

public abstract class Action {
    /**
     * The cost to execute this action. Lower is better.
     */
    public abstract int getCost();
    public abstract void execute();
}
