package org.avp.common.entity.ai.parasite.action;

import org.avp.api.entity.ai.action.Action;

public class FindHostAction extends Action {
    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        System.out.println("FINDING HOST...");
    }
}
