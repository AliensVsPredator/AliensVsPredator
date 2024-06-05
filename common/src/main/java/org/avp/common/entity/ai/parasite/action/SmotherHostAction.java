package org.avp.common.entity.ai.parasite.action;

import net.minecraft.world.entity.LivingEntity;
import org.avp.api.entity.ai.CostConstraint;
import org.avp.api.entity.ai.action.Action;
import org.avp.common.entity.AVPAbstractParasite;

public class SmotherHostAction extends Action {

    private final int smotherTimeInTicks;

    private final AVPAbstractParasite parasite;

    public SmotherHostAction(AVPAbstractParasite parasite, int smotherTimeInTicks) {
        this.parasite = parasite;
        this.smotherTimeInTicks = smotherTimeInTicks;
    }

    @Override
    public CostConstraint createCostConstraint() {
        return CostConstraint.DEFAULT;
    }

    @Override
    public boolean isValid() {
        return parasite.getVehicle() instanceof LivingEntity;
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public void execute() {
        // Tick counter.
        parasite.incrementTicksAttachedToHost();
        parasite.smotherHost();

        if (parasite.getTicksAttachedToHost() >= smotherTimeInTicks) {
            // Reset state.
            parasite.restoreHost();
            parasite.stopRiding();
        }
    }
}
