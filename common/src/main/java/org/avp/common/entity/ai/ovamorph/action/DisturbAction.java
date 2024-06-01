package org.avp.common.entity.ai.ovamorph.action;

import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractOvamorph;

public class DisturbAction extends Action {

    private final AVPAbstractOvamorph ovamorph;

    public DisturbAction(AVPAbstractOvamorph ovamorph) {
        this.ovamorph = ovamorph;
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
