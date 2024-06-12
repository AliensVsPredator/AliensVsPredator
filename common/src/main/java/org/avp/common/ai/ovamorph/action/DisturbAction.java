package org.avp.common.ai.ovamorph.action;

import org.avp.api.common.ai.CostConstraint;
import org.avp.api.common.ai.action.Action;
import org.avp.common.game.entity.AbstractOvamorph;

public class DisturbAction extends Action {

    private final AbstractOvamorph ovamorph;

    public DisturbAction(AbstractOvamorph ovamorph) {
        this.ovamorph = ovamorph;
    }

    @Override
    public CostConstraint createCostConstraint() {
        return CostConstraint.DEFAULT;
    }

    @Override
    public boolean isValid() {
        return !ovamorph.isFullyDisturbed();
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        ovamorph.disturb();
    }
}
