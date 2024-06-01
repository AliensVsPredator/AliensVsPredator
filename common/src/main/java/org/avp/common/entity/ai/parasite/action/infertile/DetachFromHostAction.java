package org.avp.common.entity.ai.parasite.action.infertile;

import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractParasite;

public class DetachFromHostAction extends Action {

    private final int smotherTimeInTicks;

    private final AVPAbstractParasite parasite;

    public DetachFromHostAction(AVPAbstractParasite parasite, int smotherTimeInTicks) {
        this.parasite = parasite;
        this.smotherTimeInTicks = smotherTimeInTicks;
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        if (parasite.getTicksAttachedToHost() >= smotherTimeInTicks) {
            parasite.stopRiding();
        } else {
            parasite.incrementTicksAttachedToHost();
        }
    }
}
